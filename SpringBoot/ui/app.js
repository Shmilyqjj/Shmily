(() => {
  'use strict';

  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => [...root.querySelectorAll(sel)];

  function apiBase() {
    const v = ($('#apiBase').value || '').trim().replace(/\/$/, '');
    return v;
  }

  function extraHeaders() {
    const h = {};
    const auth = ($('#authHeader').value || '').trim();
    if (auth) h['Authorization'] = auth;
    return h;
  }

  function showToast(msg) {
    const t = $('#toast');
    t.textContent = msg;
    t.classList.add('show');
    clearTimeout(showToast._id);
    showToast._id = setTimeout(() => t.classList.remove('show'), 2600);
  }

  function pretty(obj) {
    if (typeof obj === 'string') return obj;
    try {
      return JSON.stringify(obj, null, 2);
    } catch {
      return String(obj);
    }
  }

  function parseSseFrames(buffer) {
    const sep = /\r?\n\r?\n/;
    const events = [];
    let rest = buffer;
    let m;
    while ((m = rest.match(sep))) {
      const frame = rest.slice(0, m.index);
      rest = rest.slice(m.index + m[0].length);
      if (!frame.trim()) continue;
      let eventName = 'message';
      const dataLines = [];
      for (const line of frame.split(/\r?\n/)) {
        if (line.startsWith('event:')) eventName = line.slice(6).trim();
        else if (line.startsWith('data:'))
          dataLines.push(line.slice(5).startsWith(' ') ? line.slice(6) : line.slice(5));
      }
      const dataStr = dataLines.join('\n');
      if (dataStr) events.push({ event: eventName, data: dataStr });
    }
    return { events, carry: rest };
  }

  function processSseEventList(events, pushToken, meta, t0, mdOut) {
    for (const { event, data } of events) {
      if (event === 'error') {
        try {
          const j = JSON.parse(data);
          meta.textContent = `流式错误 · ${Math.round(performance.now() - t0)} ms`;
          mdOut.textContent = j.message || data;
        } catch {
          mdOut.textContent = data;
        }
        return false;
      }
      try {
        const j = JSON.parse(data);
        if (j && typeof j.d === 'string') pushToken(j.d);
      } catch {
        /* ignore non-JSON */
      }
    }
    return true;
  }

  async function runCsMarkdownStream(cardEl, opts) {
    const btn = cardEl.querySelector('[data-run]');
    const mdOut = cardEl.querySelector('[data-md-out]');
    const meta = cardEl.querySelector('[data-meta]');
    const base = apiBase();
    const t0 = performance.now();

    if (typeof window.marked === 'undefined') {
      showToast('Markdown 库未加载');
      return;
    }

    if (cardEl._csAbort) cardEl._csAbort.abort();
    cardEl._csAbort = new AbortController();

    mdOut.innerHTML = '';
    meta.textContent = '';

    btn.disabled = true;
    btn.classList.add('loading');
    btn.textContent = '生成中…';

    let full = '';
    let rafPending = false;
    const flushMd = () => {
      rafPending = false;
      try {
        mdOut.innerHTML = window.marked.parse(full, { breaks: true, gfm: true });
      } catch (e) {
        mdOut.textContent = full;
      }
    };
    const pushToken = (t) => {
      full += t;
      if (!rafPending) {
        rafPending = true;
        requestAnimationFrame(flushMd);
      }
    };

    try {
      const headers = {
        ...extraHeaders(),
        'Content-Type': 'application/json',
        Accept: 'text/event-stream',
        ...(opts.headers || {}),
      };
      const res = await fetch(base + opts.url, {
        method: 'POST',
        headers,
        body: opts.body,
        signal: cardEl._csAbort.signal,
      });
      const ct = (res.headers.get('content-type') || '').toLowerCase();

      if (!res.ok || !ct.includes('text/event-stream')) {
        const ms = Math.round(performance.now() - t0);
        let bodyText = '';
        try {
          bodyText = ct.includes('application/json') ? pretty(await res.json()) : await res.text();
        } catch {
          bodyText = '(无法读取响应体)';
        }
        meta.textContent = `${res.status} ${res.statusText} · ${ms} ms`;
        mdOut.textContent = bodyText || '(empty)';
        showToast(`HTTP ${res.status}`);
        return;
      }

      const reader = res.body.getReader();
      const dec = new TextDecoder();
      let carry = '';
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        carry += dec.decode(value, { stream: true });
        const { events, carry: next } = parseSseFrames(carry);
        carry = next;
        if (!processSseEventList(events, pushToken, meta, t0, mdOut)) {
          showToast('流式失败');
          return;
        }
      }
      carry += dec.decode();
      const tail = parseSseFrames(carry);
      if (!processSseEventList(tail.events, pushToken, meta, t0, mdOut)) {
        showToast('流式失败');
        return;
      }
      if (tail.carry.trim()) {
        const line = tail.carry.trim();
        if (line.startsWith('data:')) {
          const raw = line.slice(5).trim();
          if (!processSseEventList([{ event: 'message', data: raw }], pushToken, meta, t0, mdOut)) {
            showToast('流式失败');
            return;
          }
        }
      }

      if (rafPending) {
        await new Promise((r) => requestAnimationFrame(r));
      }
      flushMd();
      meta.textContent = `200 OK · ${Math.round(performance.now() - t0)} ms · 流式结束`;
      showToast('回复完成');
    } catch (e) {
      if (e.name === 'AbortError') {
        meta.textContent = '已取消';
        showToast('已取消');
      } else {
        meta.textContent = '网络错误';
        mdOut.textContent = String(e.message || e);
        showToast('请求失败');
      }
    } finally {
      btn.disabled = false;
      btn.classList.remove('loading');
      btn.textContent = '发送请求';
    }
  }

  async function runRequest(cardEl, opts) {
    const btn = cardEl.querySelector('[data-run]');
    const out = cardEl.querySelector('[data-out]');
    const meta = cardEl.querySelector('[data-meta]');
    const base = apiBase();
    const t0 = performance.now();

    out.textContent = '';
    meta.textContent = '';

    btn.disabled = true;
    btn.classList.add('loading');
    btn.textContent = '请求中…';

    try {
      const headers = { ...extraHeaders(), ...(opts.headers || {}) };
      const res = await fetch(base + opts.url, {
        method: opts.method || 'GET',
        headers,
        body: opts.body,
      });
      const ms = Math.round(performance.now() - t0);
      const ct = res.headers.get('content-type') || '';
      let bodyText;
      if (ct.includes('application/json')) {
        try {
          bodyText = pretty(await res.json());
        } catch {
          bodyText = await res.text();
        }
      } else {
        bodyText = await res.text();
      }
      meta.textContent = `${res.status} ${res.statusText} · ${ms} ms`;
      out.textContent = bodyText || '(empty)';
      if (!res.ok) showToast(`HTTP ${res.status}`);
      else showToast('请求完成');
    } catch (e) {
      meta.textContent = '网络错误';
      out.textContent = String(e.message || e);
      showToast('请求失败');
    } finally {
      btn.disabled = false;
      btn.classList.remove('loading');
      btn.textContent = '发送请求';
    }
  }

  function cardTemplate({ title, sub, method, path, inner, markdownStream }) {
    const m = (method || 'GET').toLowerCase();
    const outBlock = markdownStream
      ? '<div class="md-reply" data-md-out></div>'
      : '<pre data-out></pre>';
    return `
      <article class="card">
        <div class="card-head">
          <div>
            <h2 class="card-title">${title}</h2>
            <p class="card-sub">${sub || ''}</p>
          </div>
          <span class="method ${m}">${method || 'GET'}</span>
        </div>
        <div class="path-line">${path}</div>
        ${inner}
        <div class="actions">
          <button type="button" class="btn btn-primary" data-run>发送请求</button>
        </div>
        <div class="response-wrap" hidden>
          <div class="response-meta" data-meta></div>
          ${outBlock}
        </div>
      </article>
    `;
  }

  function wireCard(cardEl, handler) {
    const wrap = cardEl.querySelector('.response-wrap');
    cardEl.querySelector('[data-run]').addEventListener('click', async () => {
      wrap.hidden = false;
      await handler(cardEl);
    });
  }

  const PAGES = [
    {
      id: 'home',
      nav: { icon: '⌂', label: '概览' },
      title: '概览',
      desc: '本页为 Spring Boot 接口调试台。请先确认左侧 API 根地址（同域留空），再在各分类中发起请求。',
      html: `
        <div class="welcome-grid">
          <div class="welcome-tile">
            <h3>AI 实验室</h3>
            <p>配置诊断、JSON 聊天、纯文本简单聊天。</p>
          </div>
          <div class="welcome-tile">
            <h3>在线客服 & 知识库</h3>
            <p>RAG 对话与文档上传、列表、删除。</p>
          </div>
          <div class="welcome-tile">
            <h3>用户 & HTTP 演示</h3>
            <p>MyBatis 用户查询与各类入参示例。</p>
          </div>
          <div class="welcome-tile">
            <h3>打开方式</h3>
            <p>请通过 <strong>http://localhost:8082/ui/index.html</strong> 访问，避免 file:// 跨域。</p>
          </div>
        </div>
      `,
    },
    {
      id: 'ai',
      nav: { icon: '✦', label: 'AI 实验室' },
      title: 'AI 实验室',
      desc: 'Spring AI 聊天与配置接口。',
      cards: [
        {
          title: '查看 AI 配置',
          sub: 'GET /api/ai/config',
          method: 'GET',
          path: '/api/ai/config',
          run: (el) => runRequest(el, { url: '/api/ai/config', method: 'GET', headers: { Accept: 'application/json' } }),
        },
        {
          title: 'AI 聊天（JSON）',
          sub: 'POST /api/ai/chat · application/json',
          method: 'POST',
          path: '/api/ai/chat',
          fields: [
            { name: 'message', label: 'message', type: 'textarea', default: '用一句话介绍 Spring AI。' },
            { name: 'model', label: 'model（可选）', default: '' },
            { name: 'temperature', label: 'temperature（可选）', default: '' },
          ],
          run: (el) => {
            const message = el.querySelector('[name="message"]').value;
            const model = el.querySelector('[name="model"]').value.trim();
            const temp = el.querySelector('[name="temperature"]').value.trim();
            const body = { message };
            if (model) body.model = model;
            if (temp) body.temperature = parseFloat(temp);
            return runRequest(el, {
              url: '/api/ai/chat',
              method: 'POST',
              headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
              body: JSON.stringify(body),
            });
          },
        },
        {
          title: '简单聊天（纯文本）',
          sub: 'POST /api/ai/chat/simple · text/plain',
          method: 'POST',
          path: '/api/ai/chat/simple',
          fields: [{ name: 'text', label: '请求体（纯文本）', type: 'textarea', default: '你好' }],
          run: (el) => {
            const text = el.querySelector('[name="text"]').value;
            return runRequest(el, {
              url: '/api/ai/chat/simple',
              method: 'POST',
              headers: { 'Content-Type': 'text/plain; charset=UTF-8', Accept: 'text/plain,*/*' },
              body: text,
            });
          },
        },
      ],
    },
    {
      id: 'cs',
      nav: { icon: '💬', label: '在线客服' },
      title: '在线客服',
      desc: '基于知识库的 RAG 对话。',
      cards: [
        {
          title: '客服对话（流式 · Markdown）',
          sub: 'POST /api/cs/chat/stream · text/event-stream，data 为 JSON 字段 d',
          method: 'POST',
          path: '/api/cs/chat/stream',
          markdownStream: true,
          fields: [{ name: 'message', label: '用户问题', type: 'textarea', default: '你们支持退换货吗？' }],
          run: (el) => {
            const message = el.querySelector('[name="message"]').value;
            return runCsMarkdownStream(el, {
              url: '/api/cs/chat/stream',
              body: JSON.stringify({ message }),
            });
          },
        },
        {
          title: '客服对话（整段 JSON）',
          sub: 'POST /api/cs/chat · 非流式，原始 JSON',
          method: 'POST',
          path: '/api/cs/chat',
          fields: [{ name: 'message', label: '用户问题', type: 'textarea', default: '你们支持退换货吗？' }],
          run: (el) => {
            const message = el.querySelector('[name="message"]').value;
            return runRequest(el, {
              url: '/api/cs/chat',
              method: 'POST',
              headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
              body: JSON.stringify({ message }),
            });
          },
        },
      ],
    },
    {
      id: 'kb',
      nav: { icon: '📚', label: '知识库管理' },
      title: '知识库管理',
      desc: '上传、列出、删除知识库文档。',
      cards: [
        {
          title: '文档列表',
          sub: 'GET /api/admin/kb/documents',
          method: 'GET',
          path: '/api/admin/kb/documents',
          run: (el) =>
            runRequest(el, {
              url: '/api/admin/kb/documents',
              method: 'GET',
              headers: { Accept: 'application/json' },
            }),
        },
        {
          title: '上传文档',
          sub: 'POST /api/admin/kb/documents · multipart field: file',
          method: 'POST',
          path: '/api/admin/kb/documents',
          custom: `
            <div class="file-row">
              <input type="file" name="file" data-file accept=".txt,.md,.json,.markdown" />
            </div>
          `,
          run: (el) => {
            const input = el.querySelector('[data-file]');
            if (!input.files?.length) {
              showToast('请选择文件');
              return Promise.resolve();
            }
            const fd = new FormData();
            fd.append('file', input.files[0]);
            return runRequest(el, {
              url: '/api/admin/kb/documents',
              method: 'POST',
              headers: { ...extraHeaders() },
              body: fd,
            });
          },
        },
        {
          title: '删除文档',
          sub: 'DELETE /api/admin/kb/documents/{id}',
          method: 'DELETE',
          path: '/api/admin/kb/documents/{id}',
          fields: [{ name: 'id', label: '文档 ID', default: '1' }],
          run: (el) => {
            const id = el.querySelector('[name="id"]').value.trim();
            return runRequest(el, {
              url: `/api/admin/kb/documents/${encodeURIComponent(id)}`,
              method: 'DELETE',
              headers: { Accept: 'application/json' },
            });
          },
        },
      ],
    },
    {
      id: 'user',
      nav: { icon: '👤', label: '用户模块' },
      title: '用户模块',
      desc: 'UserController 提供的用户查询接口。',
      cards: [
        {
          title: '用户列表',
          sub: 'GET /user/list',
          method: 'GET',
          path: '/user/list',
          run: (el) =>
            runRequest(el, { url: '/user/list', method: 'GET', headers: { Accept: 'application/json' } }),
        },
        {
          title: '按用户名查询',
          sub: 'GET /user/get?name=',
          method: 'GET',
          path: '/user/get',
          fields: [{ name: 'name', label: 'name', default: 'Shmily' }],
          run: (el) => {
            const name = el.querySelector('[name="name"]').value.trim();
            const q = new URLSearchParams({ name });
            return runRequest(el, {
              url: '/user/get?' + q.toString(),
              method: 'GET',
              headers: { Accept: 'application/json' },
            });
          },
        },
        {
          title: '用户列表（stream 接口）',
          sub: 'GET /user/list/stream',
          method: 'GET',
          path: '/user/list/stream',
          run: (el) =>
            runRequest(el, {
              url: '/user/list/stream',
              method: 'GET',
              headers: { Accept: 'application/json' },
            }),
        },
      ],
    },
    {
      id: 'http',
      nav: { icon: '🔗', label: 'HTTP 演示' },
      title: 'HTTP 演示',
      desc: 'HttpController 入参与上传示例。',
      cards: [
        {
          title: 'Hello 路径参数',
          sub: 'GET /hello/{name}',
          method: 'GET',
          path: '/hello/{name}',
          fields: [{ name: 'name', label: 'name', default: 'qjj' }],
          run: (el) => {
            const name = el.querySelector('[name="name"]').value.trim();
            return runRequest(el, { url: `/hello/${encodeURIComponent(name)}`, method: 'GET' });
          },
        },
        {
          title: 'Hello 查询参数',
          sub: 'GET /hello?name=&age=',
          method: 'GET',
          path: '/hello',
          fields: [
            { name: 'name', label: 'name', default: 'qjj' },
            { name: 'age', label: 'age（可选）', default: '26' },
          ],
          run: (el) => {
            const name = el.querySelector('[name="name"]').value.trim();
            const age = el.querySelector('[name="age"]').value.trim();
            const q = new URLSearchParams({ name });
            if (age) q.set('age', age);
            return runRequest(el, { url: '/hello?' + q.toString(), method: 'GET' });
          },
        },
        {
          title: 'Hello POST JSON',
          sub: 'POST /hello',
          method: 'POST',
          path: '/hello',
          fields: [
            { name: 'name', label: 'name', default: 'qjj' },
            { name: 'age', label: 'age', default: '24' },
          ],
          run: (el) => {
            const name = el.querySelector('[name="name"]').value.trim();
            const age = el.querySelector('[name="age"]').value.trim();
            return runRequest(el, {
              url: '/hello',
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({ name, age: parseInt(age, 10) || age }),
            });
          },
        },
        {
          title: 'Form 表单',
          sub: 'POST /form · application/x-www-form-urlencoded',
          method: 'POST',
          path: '/form',
          fields: [
            { name: 'name', label: 'name', default: 'qjj' },
            { name: 'age', label: 'age', default: '24' },
          ],
          run: (el) => {
            const name = el.querySelector('[name="name"]').value.trim();
            const age = el.querySelector('[name="age"]').value.trim();
            const body = new URLSearchParams({ name, age });
            return runRequest(el, {
              url: '/form',
              method: 'POST',
              headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
              body: body.toString(),
            });
          },
        },
        {
          title: 'JSON（Gson JsonObject）',
          sub: 'POST /json',
          method: 'POST',
          path: '/json',
          fields: [
            {
              name: 'raw',
              label: 'JSON 原文',
              type: 'textarea',
              default: '{"name":"qjj","age":"24","version":"2.0"}',
            },
          ],
          run: (el) => {
            const raw = el.querySelector('[name="raw"]').value;
            return runRequest(el, {
              url: '/json',
              method: 'POST',
              headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
              body: raw,
            });
          },
        },
        {
          title: 'Demo POST',
          sub: 'POST /demo',
          method: 'POST',
          path: '/demo',
          fields: [
            { name: 'name', label: 'name', default: 'qjj' },
            { name: 'age', label: 'age', default: '24' },
          ],
          run: (el) => {
            const name = el.querySelector('[name="name"]').value.trim();
            const age = el.querySelector('[name="age"]').value.trim();
            return runRequest(el, {
              url: '/demo',
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({ name, age }),
            });
          },
        },
        {
          title: 'PUT 示例',
          sub: 'PUT /put',
          method: 'PUT',
          path: '/put',
          fields: [
            { name: 'name', label: 'name', default: 'qjj' },
            { name: 'age', label: 'age', default: '24' },
          ],
          run: (el) => {
            const name = el.querySelector('[name="name"]').value.trim();
            const age = el.querySelector('[name="age"]').value.trim();
            return runRequest(el, {
              url: '/put',
              method: 'PUT',
              headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
              body: JSON.stringify({ name, age }),
            });
          },
        },
        {
          title: '文件上传',
          sub: 'POST /file/upload · field: file',
          method: 'POST',
          path: '/file/upload',
          custom: `<div class="file-row"><input type="file" name="file" data-file /></div>`,
          run: (el) => {
            const input = el.querySelector('[data-file]');
            if (!input.files?.length) {
              showToast('请选择文件');
              return Promise.resolve();
            }
            const fd = new FormData();
            fd.append('file', input.files[0]);
            return runRequest(el, { url: '/file/upload', method: 'POST', headers: { ...extraHeaders() }, body: fd });
          },
        },
      ],
    },
    {
      id: 'exchange',
      nav: { icon: '⇄', label: 'HTTP Exchange' },
      title: 'HTTP Exchange',
      desc: '依赖拦截器注入的 userInfo；勿传 Authorization: 666666。',
      cards: [
        {
          title: 'Exchange GET',
          sub: 'GET /exchange/get/{name}',
          method: 'GET',
          path: '/exchange/get/{name}',
          fields: [{ name: 'name', label: '路径 name', default: 'qjj' }],
          run: (el) => {
            const name = el.querySelector('[name="name"]').value.trim();
            return runRequest(el, {
              url: `/exchange/get/${encodeURIComponent(name)}`,
              method: 'GET',
              headers: { Accept: 'application/json' },
            });
          },
        },
        {
          title: 'Exchange POST',
          sub: 'POST /exchange/post',
          method: 'POST',
          path: '/exchange/post',
          fields: [
            {
              name: 'raw',
              label: 'JSON 负载（将合并到请求体）',
              type: 'textarea',
              default: '{"name":"qjj","age":24}',
            },
          ],
          run: (el) => {
            let raw = el.querySelector('[name="raw"]').value;
            let obj;
            try {
              obj = JSON.parse(raw);
            } catch {
              showToast('JSON 格式错误');
              return Promise.resolve();
            }
            return runRequest(el, {
              url: '/exchange/post',
              method: 'POST',
              headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
              body: JSON.stringify(obj),
            });
          },
        },
      ],
    },
  ];

  function renderFields(fields) {
    if (!fields?.length) return '';
    return `
      <div class="field-grid cols-2">
        ${fields
          .map((f) => {
            const t = f.type === 'textarea' ? 'textarea' : 'input';
            const val = f.default ?? '';
            if (t === 'textarea') {
              return `<label class="f">${f.label}<textarea class="f-input" name="${f.name}">${escapeHtml(val)}</textarea></label>`;
            }
            return `<label class="f">${f.label}<input class="f-input" name="${f.name}" value="${escapeHtml(val)}" /></label>`;
          })
          .join('')}
      </div>
    `;
  }

  function escapeHtml(s) {
    return String(s)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function buildCard(c) {
    const inner = (c.fields ? renderFields(c.fields) : '') + (c.custom || '');
    return cardTemplate({
      title: c.title,
      sub: c.sub,
      method: c.method,
      path: c.path,
      inner,
      markdownStream: Boolean(c.markdownStream),
    });
  }

  function init() {
    const nav = $('#nav');
    const panels = $('#panels');

    PAGES.forEach((page, idx) => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'nav-item' + (idx === 0 ? ' active' : '');
      btn.dataset.page = page.id;
      btn.innerHTML = `<span class="ni-icon">${page.nav.icon}</span><span>${page.nav.label}</span>`;
      nav.appendChild(btn);

      const panel = document.createElement('div');
      panel.className = 'panel' + (idx === 0 ? ' active' : '');
      panel.id = 'panel-' + page.id;
      if (page.html) {
        panel.innerHTML = page.html;
      } else {
        panel.innerHTML = (page.cards || []).map(buildCard).join('');
        $$( '.card', panel).forEach((cardEl, i) => {
          wireCard(cardEl, page.cards[i].run);
        });
      }
      panels.appendChild(panel);
    });

    $$('.nav-item').forEach((btn) => {
      btn.addEventListener('click', () => {
        const id = btn.dataset.page;
        $$('.nav-item').forEach((b) => b.classList.toggle('active', b === btn));
        $$('.panel').forEach((p) => p.classList.toggle('active', p.id === 'panel-' + id));
        const page = PAGES.find((p) => p.id === id);
        $('#pageTitle').textContent = page.title;
        $('#pageDesc').textContent = page.desc;
      });
    });

    const savedBase = localStorage.getItem('shmily_api_base');
    const savedAuth = localStorage.getItem('shmily_api_auth');
    if (savedBase) $('#apiBase').value = savedBase;
    if (savedAuth) $('#authHeader').value = savedAuth;

    $('#apiBase').addEventListener('change', () => localStorage.setItem('shmily_api_base', $('#apiBase').value));
    $('#authHeader').addEventListener('change', () => localStorage.setItem('shmily_api_auth', $('#authHeader').value));
  }

  document.addEventListener('DOMContentLoaded', init);
})();

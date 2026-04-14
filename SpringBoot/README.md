# SpringBoot 模块说明

基于 **Spring Boot 3.3** 的示例与实验工程，集成 **MyBatis**、**Spring AI（OpenAI 兼容对话 + Redis 向量库 RAG）**、**Redis**、**Kafka（可选）**、**WebSocket**，并提供打包进 Jar 的 **静态调试控制台 UI** 与 **Swagger / OpenAPI** 文档。

---

## 环境要求

| 组件 | 说明 |
|------|------|
| **JDK** | 17 |
| **Maven** | 3.8+（随父 POM 拉依赖） |
| **MySQL** | 8.x，默认库名 `test`（见 `application.yml`） |
| **Redis** | **Redis Stack**（含 **RediSearch** 与向量能力），用于 Spring AI **向量存储**；客户端为 **Jedis**，逻辑库 **0** 用于向量索引 |
| **Kafka** | 可选；默认 `kafka.enabled: false`，未启用时不影响启动 |
| **大模型** | 需可用的 **OpenAI 兼容** Chat / Embeddings 网关与 API Key |

---

## 快速开始

### 1. 初始化数据库

在 MySQL 中执行：

```bash
mysql -u root -p < src/main/bin/db_init.sql
```

脚本会创建 `test` 库、`user` 演示表及 **`kb_document`** 知识库元数据表，并插入示例用户数据。

按需修改 `src/main/resources/application.yml` 中的 `spring.datasource.url` / `username` / `password`。

### 2. 准备 Redis

- 使用支持 **向量检索** 的 Redis（如 Redis Stack）。
- 无密码时 **`REDIS_USERNAME` 勿填默认占位**，避免无意义 `AUTH`；有密码时再配置 `REDIS_PASSWORD`。
- 向量索引建在 **db0**；若业务使用其他 `database`，向量仍写入 **db0**（见 `application.yml` 注释）。

### 3. 配置 AI 与知识库（环境变量推荐）

在 Shell 或部署环境中导出（示例占位，请换成你自己的 Key 与地址）：

```bash
# OpenAI 兼容网关（base-url 建议以 /v1 结尾）
export OPENAI_BASE_URL=https://api.openai.com/v1
export OPENAI_API_KEY=your-chat-api-key

# 若 Embedding 使用另一 Key 或另一路径（如部分云厂商拆分接口）
export OPENAI_EMBEDDING_API_KEY=${OPENAI_API_KEY}
export OPENAI_EMBEDDINGS_PATH=/embeddings

# 模型名（与网关一致）
export AI_MODEL=glm-5
export AI_EMBEDDING_MODEL=text-embedding-v4
export AI_EMBEDDING_DIMENSIONS=2048

# Redis（向量库）
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=your-redis-password

# 知识库文件落盘目录（须可写；不要用易变的相对路径作为唯一配置）
export KB_STORAGE_DIR=/tmp/shmily/knowledge-base
```

更多占位项见 **`application.yml`**（如 `OPENAI_COMPLETIONS_PATH`、`REDIS_VECTOR_INDEX`、`KB_REINDEX_FROM_DB_ON_STARTUP` 等）。

### 4. 编译与运行

```bash
cd SpringBoot
mvn -DskipTests package
java -jar target/SpringBoot-0.0.1.jar
```

开发时可直接：

```bash
mvn spring-boot:run
```

默认端口：**8082**（`server.port`）。

---

## 内置 Web 控制台（静态 UI）

构建时 **`ui/`** 目录会被拷贝到 **`classpath:/static/ui/`**（见 `pom.xml` `build.resources`）。

浏览器访问（需通过 HTTP 服务，避免 `file://` 导致跨域）：

- **API 调试台**：<http://localhost:8082/ui/index.html>  
  包含 AI 实验室、**在线客服（Markdown + SSE 流式）**、知识库管理、用户与 HTTP 演示等卡片。

---

## OpenAPI / Swagger

启动后访问：

- **Swagger UI**：<http://localhost:8082/swagger-ui/index.html>

---

## 主要 HTTP 接口一览

### AI（`/api/ai`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/ai/config` | 查看当前 AI 相关配置（脱敏展示） |
| POST | `/api/ai/chat` | JSON 聊天 |
| POST | `/api/ai/chat/simple` | `text/plain` 简单聊天 |

### 在线客服 RAG（`/api/cs`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/cs/chat` | 非流式，返回整段 `reply`（可为 Markdown 文本） |
| POST | `/api/cs/chat/stream` | **SSE**（`text/event-stream`），增量 JSON：`{"d":"片段"}`；错误事件名为 `error`，body：`{"message":"..."}` |

请求体均为 JSON，字段与 **`CsChatReq`** 一致（如 `message`）。

### 知识库管理（`/api/admin/kb`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/kb/documents` | `multipart/form-data`，字段 **`file`** 上传文档 |
| GET | `/api/admin/kb/documents` | 文档列表 |
| DELETE | `/api/admin/kb/documents/{id}` | 按 ID 删除 |

### 用户演示（`/user/...`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/list` | 用户列表 |
| GET | `/user/get?name=` | 按名称查询 |
| GET | `/user/list/stream` | 列表流式接口示例 |

### HttpController 演示（根路径）

含 `/hello`、`/form`、`/json`、`/demo`、`/put`、`/file/upload` 以及 **`/exchange/get/{name}`**、**`/exchange/post`** 等；**`/exchange/*` 请勿携带 `Authorization: 666666`**（全局拦截器会返回 401）。

### WebSocket（JSR-356）

连接基址与 HTTP 相同（默认 `ws://localhost:8082`）：

| 路径 | 说明 |
|------|------|
| `/ws/query/{clientId}` | 带客户端 ID 的查询通道（见 `WebsocketChannel`） |
| `/ws/demo` | 演示通道（见 `WebSocketDemoChannel`） |

---

## 全局拦截器说明

**`AuthInterceptor`** 拦截所有路径：若请求头 **`Authorization` 恰好为 `666666`**，返回 **401**；否则向 `request` 注入模拟 **`userInfo`**（供 Exchange 等演示使用）。

---

## Kafka

`application.yml` 中 **`kafka.enabled`** 默认为 **`false`**。若需消费示例，请改为 `true` 并保证 `spring.kafka.consumer.bootstrap-servers` 可达。

---

## 工程结构（节选）

```text
SpringBoot/
├── pom.xml
├── ui/                          # 静态控制台（打包进 static/ui）
├── src/main/java/.../
│   ├── Application.java
│   ├── config/                  # Web、Swagger、RAG、Redis 向量库等
│   ├── controller/              # Ai、CustomerService、Http、User
│   ├── controller/admin/        # KnowledgeBaseAdmin
│   └── ...
├── src/main/resources/
│   ├── application.yml
│   └── mapper/                  # MyBatis XML
└── src/main/bin/db_init.sql     # 数据库初始化
```

---

## 常见问题

1. **向量 / 客服报错**  
   检查 Redis 是否为 Stack、网络与密码、**Embedding 维度**与模型是否一致（`AI_EMBEDDING_DIMENSIONS`）。

2. **Embedding URL 重复 `/v1`**  
   `base-url` 若已含 `/v1`，`embeddings-path` 应为 **`/embeddings`**，避免出现 `/v1/v1/embeddings`（见 `application.yml` 注释）。

3. **知识库文件目录**  
   使用 **`KB_STORAGE_DIR`** 配置为稳定、可写的绝对路径；仅依赖相对路径时，工作目录可能在临时目录下导致路径不可用。

4. **Swagger 与 Gson**  
   若将来启用全局 Gson 作为首选 JSON 转换器，需注意与 **springdoc** 的兼容性（仓库内相关 Bean 注释见 `WebConfig`）。

---

## 许可证与作者

个人学习/模板项目；具体许可证以仓库根目录为准。

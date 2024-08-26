package top.shmilyqjj.springboot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import top.shmilyqjj.springboot.services.HttpExchangeService;
import top.shmilyqjj.springboot.services.api.LocalHostWebClient1Api;
import top.shmilyqjj.springboot.services.api.LocalHostWebClient2Api;

/**
 * @author Shmily
 * @Description: HttpExchange WebClient配置类 方便调用外部API 且性能较好
 * @CreateTime: 2024/8/26 下午7:51
 */


@Configuration
public class HttpExchangeConfig {

    /**
     * 支持多个WebClient的配置
     */
    @Bean
    @Qualifier("LocalHostWebClient1")
    WebClient LocalHostWebClient1() {
        return WebClient.builder()
                //添加全局默认请求头
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer 1234567890")
                .baseUrl("http://localhost:8082")
                .build();
    }

    /**
     * 支持多个WebClient的配置
     */
    @Bean
    @Qualifier("LocalHostWebClient2")
    WebClient LocalHostWebClient2() {
        return WebClient.builder()
                //添加全局默认请求头
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer 1234567890")
                .baseUrl("http://127.0.0.1:8082")
                .build();
    }

    @Bean
    LocalHostWebClient1Api LocalHostWebClient1Api(@Qualifier("LocalHostWebClient1") WebClient client) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(LocalHostWebClient1Api.class);
    }

    @Bean
    LocalHostWebClient2Api LocalHostWebClient2Api(@Qualifier("LocalHostWebClient2") WebClient client) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(LocalHostWebClient2Api.class);
    }



    /**
     * 单独的HttpExchangeService的WebClient创建
     * @return HttpExchangeService
     */
    @Bean
    public HttpExchangeService httpExchangeService() {
        WebClient webClient = WebClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .filter((request, next) -> {
                    ClientRequest filtered = ClientRequest.from(request).build();
                    return next.exchange(filtered);
                })
                .baseUrl("http://localhost:8082")
                .build();

        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();
        //创建某个Interface的代理服务
        return proxyFactory.createClient(HttpExchangeService.class);

    }

}

package top.shmilyqjj.springboot.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/** 将 Spring AI 使用的 RestClient 换为 JDK {@link HttpClient}，避免 401 时 HttpURLConnection 抛出 HttpRetryException 导致无法读响应体。 */
@Configuration
public class OpenAiCompatibleRestClientConfig {

    @Bean
    RestClientCustomizer openAiCompatibleRestClientCustomizer() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofMinutes(2));
        return (RestClient.Builder builder) -> builder.requestFactory(factory);
    }
}

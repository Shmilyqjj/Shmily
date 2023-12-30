package top.shmilyqjj.springboot.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private License license() {
        return new License()
                .name("MIT")
                .url("https://opensource.org/licenses/MIT");
    }

    private Info info(){
        return new Info()
                .title("Shmily's API")
                .description("A Springboot project for Shmily.")
                .version("v1.0.0")
                .license(license());
    }
    private ExternalDocumentation externalDocumentation() {
        return new ExternalDocumentation()
                .description("This is an additional description")
                .url("http://shmily-qjj.top/");
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(info())
                .externalDocs(externalDocumentation());
    }

}

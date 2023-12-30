package top.shmilyqjj.springboot.config;

import com.google.gson.*;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 支持Gson作为默认json处理框架 （注：此种方式启用gson 会导致swagger不可用  改为在application.yml增加配置spring.mvc.converters.preferred-json-mapper: gson）
//    @Bean
//    public Gson gson() {
//        GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapterFactory(DateTypeAdapter.FACTORY);
//        return builder.create();
//    }
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.forEach(c -> System.out.println(c.toString()));
//        converters.clear();
//        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
//        gsonHttpMessageConverter.setGson(gson());
//        gsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,
//                MediaType.MULTIPART_FORM_DATA,
//                MediaType.TEXT_HTML,
//                MediaType.TEXT_PLAIN,
//                MediaType.APPLICATION_OCTET_STREAM));
//        converters.add(gsonHttpMessageConverter);
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
//                .allowCredentials(true)
//                .exposedHeaders(
//                        "fileName",
//                        "fileSzie",
//                        "md5")
                .maxAge(3600);

    }
}
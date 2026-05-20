package com.example.movierecommendationapi.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {

    /**
     * RestTemplate that ignores unknown JSON properties. TMDB responses
     * contain ~20 fields per movie but our DTOs only declare a handful.
     * Without this, every TMDB call dies with
     *   "Error while extracting response for type [...] and content type ..."
     *
     * The global `spring.jackson.*` properties don't reach a manually
     * constructed RestTemplate, so we wire the ObjectMapper ourselves.
     */
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter(mapper);

        // Some endpoints (e.g. OpenRouter) return application/json with extra
        // parameters; make the converter accept them all.
        jacksonConverter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.valueOf("application/json;charset=utf-8"),
                MediaType.valueOf("application/*+json"),
                MediaType.ALL
        ));

        RestTemplate restTemplate = new RestTemplate();

        // Replace any existing Jackson converter with our permissive one.
        List<HttpMessageConverter<?>> converters = new ArrayList<>(restTemplate.getMessageConverters());
        converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
        converters.add(0, jacksonConverter); // first, so it wins ordering
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}

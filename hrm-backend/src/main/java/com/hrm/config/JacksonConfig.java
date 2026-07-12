package com.hrm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Dang ky Hibernate6Module de Jackson khong crash khi gap quan he LAZY.
     * FORCE_LAZY_LOADING = false: quan he chua load se tra ve null thay vi nem loi.
     */
    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();
        module.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer(Hibernate6Module module) {
        return builder -> builder.modulesToInstall(module);
    }
}

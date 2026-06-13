package com.sicred.votacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ValidationConfig implements WebMvcConfigurer {

    @Override
    public Validator getValidator() {
        return new LocalValidatorFactoryBean();
    }
}
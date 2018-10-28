package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.converter.FileReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public SelectService selectService() {
        return new SelectService(converter());
    }

    @Bean
    public Converter converter() {
        return new Converter(fileReader());
    }

    @Bean
    public FileReader fileReader() {
        return new FileReader();
    }
}

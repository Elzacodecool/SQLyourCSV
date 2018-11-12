package com.codecool.service;

import com.codecool.converter.FileReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SelectServiceConfiguration {

    @Bean
    FileReader fileReader() {
        return new  CsvFileReader();
    }

}

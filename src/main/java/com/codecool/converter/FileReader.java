package com.codecool.converter;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileReader {

    public FileReader() {}

    List<String[]> readData(String file) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}

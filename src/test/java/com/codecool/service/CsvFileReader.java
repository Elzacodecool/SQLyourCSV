package com.codecool.service;

import com.codecool.converter.FileReader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@Component
public class CsvFileReader extends FileReader {

    @Override
    public List<String[]> readData(String file) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader("src/test/resources/" + file))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void writeData(String file,List<List<Object>> values) {

    }
}

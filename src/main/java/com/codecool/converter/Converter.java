package com.codecool.converter;

import com.codecool.model.Row;
import com.codecool.model.Table;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {
    private FileReader fileReader;

    @Autowired
    public Converter(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public Table convert(String filepath) {
        List<String[]> data = fileReader.readData(filepath);
        if (!checkIfDataIsCorrect(data)) {
            throw new WrongDataStructureException("Wrong data structure");
        }
        List<String> columnNames = Arrays.asList(data.get(0));
        List<Row> rows = data.stream()
                .skip(1)
                .map(row -> new Row(
                        columnNames.stream()
                        .collect(Collectors.toMap(columnName -> columnName, columnName -> row[columnNames.indexOf(columnName)]))
                        )
                ).collect(Collectors.toList());

        return new Table(columnNames, rows);
    }

    private boolean checkIfDataIsCorrect(List<String[]> data) {
        if (data.isEmpty()) {
            return false;
        }
        int columnSize = data.get(0).length;

        return data.stream().noneMatch(line -> line.length != columnSize);
    }

}
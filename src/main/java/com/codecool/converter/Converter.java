package com.codecool.converter;

import com.codecool.exception.WrongDataStructureException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class Converter {
    private FileReader fileReader;

    @Autowired
    public Converter(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public Table convert(String filepath) {
        List<String[]> data = null;
        try {
            data = fileReader.readData(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void convertBeforeWriting(Table table, String file) {
        List<List<Object>> values = new ArrayList<>();
        values.add(table.getColumnNames().stream().map(Object.class::cast).collect(Collectors.toList()));
        values.addAll(table.getRows().stream().map(n->n.getValuesFromRow().stream().map(Object.class::cast).collect(Collectors.toList())).collect(Collectors.toList()));
        try {
            fileReader.writeData(file, values);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfDataIsCorrect(List<String[]> data) {
        if (data.isEmpty()) {
            return false;
        }
        int columnSize = data.get(0).length;

        return data.stream().noneMatch(line -> line.length != columnSize);
    }

}

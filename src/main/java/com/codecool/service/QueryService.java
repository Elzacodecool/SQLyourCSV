package com.codecool.service;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import com.codecool.model.Table;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class QueryService {
    public abstract Table executeQuery(String query);

    protected String getFileName(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("from");
        if (indexFrom < 0) {
            throw new WrongQueryFormatException("Missing FROM statement");
        } else if (indexFrom >= words.size()) {
            throw new WrongQueryFormatException("Missing filename");
        }

        String filename = words.get(indexFrom + 1);
        if (filename.isEmpty()) {
            throw new WrongQueryFormatException("Wrong filename");
        }

        return filename;
    }


    protected Row getUpdatedRowWithColumns(Row row, List<String> columns) {
        return new Row(
                columns.stream()
                        .collect(Collectors.toMap(column -> column, column -> row.getData().get(column)))
        );
    }
}

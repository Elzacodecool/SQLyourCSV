package com.codecool.service;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public abstract class QueryService {
    public abstract Table executeQuery(String query);

    protected String getFileName(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("from");
        if (indexFrom < 0) {
            throw new WrongQueryFormatException("Missing FROM statement");
        } else if (indexFrom >= words.size() - 1) {
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

    protected List<String> getListColumns(String query) {
        return Arrays.stream(query.split(" "))
                .filter(word -> query.indexOf(word) > query.indexOf("select"))
                .filter(word -> query.indexOf(word) < query.indexOf("from"))
                .map(word -> word.replace(",", ""))
                .collect(Collectors.toList());
    }
}

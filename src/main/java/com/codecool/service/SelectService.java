package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SelectService implements QueryService {
    private Converter converter;

    @Autowired
    public SelectService(Converter converter) {
        this.converter = converter;
    }

    @Override
    public Table executeQuery(String query) {
        String filename = getFileName(query);
        Table table = converter.convert("src/main/resources/" + filename);
        List<String> columnsToDisplay = getListColumns(query);
        Predicate<Row> predicate = getPredicate(query);

        List<Row> newRows = table.getRows().stream()
                .filter(predicate)
                .map(row -> getUpdatedRowWithColumns(row, columnsToDisplay))
                .collect(Collectors.toList());

        return new Table(columnsToDisplay, newRows);
    }

    private Row getUpdatedRowWithColumns(Row row, List<String> columns) {
        return new Row(
                columns.stream()
                        .collect(Collectors.toMap(column -> column, column -> row.getData().get(column)))
        );
    }
}

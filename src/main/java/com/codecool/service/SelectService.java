package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SelectService extends QueryService {
    private Converter converter;

    @Autowired
    public SelectService(Converter converter) {
        this.converter = converter;
    }

    @Override
    public Table executeQuery(String query) {
        String filename = getFilename(query);
        Table table = converter.convert("src/main/resources/" + filename);
        List<String> columnsToDisplay = getValidatedListColumns(query, table);
        Predicate<Row> predicate = getPredicate(query);

        List<Row> newRows = table.getRows().stream()
                .filter(predicate)
                .map(row -> getUpdatedRowWithColumns(row, columnsToDisplay))
                .collect(Collectors.toList());

        return new Table(columnsToDisplay, newRows);
    }

    String getFilename(String query) {
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

    Row getUpdatedRowWithColumns(Row row, List<String> columns) {
        return new Row(
                columns.stream()
                        .collect(Collectors.toMap(column -> column, column -> row.getData().get(column)))
        );
    }

    List<String> getValidatedListColumns(String query, Table table) {
        List<String> columns = getListColumns(query);

        if (columns.contains("*")) {
            return table.getColumnNames();
        }

        if (checkIfColumnsExistInTable(columns, table)) {
            return columns;
        }

        throw new WrongQueryFormatException("No column in table");
    }

    private List<String> getListColumns(String query) {
        return Arrays.stream(query.split(" "))
                .filter(word -> query.indexOf(word) > query.indexOf("select"))
                .filter(word -> query.indexOf(word) < query.indexOf("from"))
                .map(word -> word.replace(",", ""))
                .collect(Collectors.toList());
    }

    private boolean checkIfColumnsExistInTable(List<String> columns, Table table) {
        return table.getColumnNames().containsAll(columns);
    }


    Predicate<Row> getPredicate(String query) {
        return null;
    }

}

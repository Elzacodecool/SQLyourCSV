package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
    public Table executeQuery(String query) throws IOException, GeneralSecurityException {
        String customizedQuery = query.toLowerCase().replace(";", "");
        String filename = getFilename(query.replace(";", ""));
        Table table = converter.convert(filename);
        List<String> columnsToDisplay = getValidatedListColumns(customizedQuery, table);
        Predicate<Row> predicate = getPredicate(customizedQuery);

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
        if (query.contains("where")) {
            List<String> queryList = mapQueryToList(query);
            List<String> condition = queryList.stream()
                    .skip(queryList.indexOf("where") + 1)
                    .collect(Collectors.toList());
            return buildPredicate(condition);
        }

        return (row) -> true;
    }

    private Predicate<Row> buildPredicate(List<String> condition) {
        Predicate<Row> predicate;
        String columnName = condition.get(condition.size()-3);
        String operator = condition.get(condition.size()-2);
        String value = condition.get(condition.size()-1);

        switch (operator) {
            case "=":
                predicate = (row) -> row.getData().get(columnName).equals(value);
                break;
            case ">":
                predicate = (row) -> Integer.valueOf(row.getData().get(columnName).toString()) > Integer.valueOf(value);
                break;
            case "<":
                predicate = (row) -> Integer.valueOf(row.getData().get(columnName).toString()) < Integer.valueOf(value);
                break;
            case "<>":
                predicate = (row) -> !row.getData().get(columnName).equals(value);
                break;
            case "like":
                predicate = (row) -> row.getData().get(columnName) instanceof String &&
                        row.getData().get(columnName).equals(value.replace("\'", ""));
                break;
            default:
                return (row) -> false;
        }

        if (condition.size() > 3 && condition.get(condition.size()-4).equals("or")) {
            return predicate.or(buildPredicate(condition.subList(0, condition.size()-4)));
        } else if (condition.size() > 3 && condition.get(condition.size()-4).equals("and")) {
            return predicate.and(buildPredicate(condition.subList(0, condition.size()-4)));
        }
        return predicate;
    }

    private List<String> mapQueryToList(String text) {
        return Arrays.stream(Arrays.stream(text.split(" "))
                .map(word -> word.length() > 1 ? word.replace("=", " = ") : word)
                .map(word -> !word.contains("<>") && word.length() > 1 ? word.replace(">", " > ") : word)
                .map(word -> !word.contains("<>") && word.length() > 1 ? word.replace("<", " < ") : word)
                .map(word -> word.length() > 2 ? word.replace("<>", " <> ") : word)
                .collect(Collectors.joining(" "))
                .split(" "))
                .collect(Collectors.toList());
    }
}

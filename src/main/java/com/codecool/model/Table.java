package com.codecool.model;

import java.util.List;
import java.util.stream.Collectors;

public class Table {
    private List<String> columnNames;
    private List<Row> rows;

    public Table(List<String> columnNames, List<Row> rows) {
        this.columnNames = columnNames;
        this.rows = rows;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Row> getRows() {
        return rows;
    }

    public String toString() {
        String columnNamesToString = columnNames.stream()
                .map(name -> String.format("%"+ Row.COLUMN_WIDTH +"s", name))
                .collect(Collectors.joining(" | "));
        String rowsToString = rows.stream()
                .map(row -> row.toString(columnNames))
                .collect(Collectors.joining("\n"));
        return columnNamesToString + "\n" + rowsToString;
    }
}

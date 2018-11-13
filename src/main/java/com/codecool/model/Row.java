package com.codecool.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Row {
    public static int COLUMN_WIDTH = 10;
    private Map<String, Object> data;

    public Row(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String toString(List<String> columnNames) {
        return columnNames.stream()
                .map(column -> String.format("%"+ COLUMN_WIDTH +"s", data.get(column)))
                .collect(Collectors.joining(" | "));
    }

    public String toString() {
        return data.entrySet().stream()
                .map(entry -> String.format("%"+ COLUMN_WIDTH +"s", entry.getValue()))
                .collect(Collectors.joining(" | "));
    }

    public List<String> getValuesFromRow() {
        return data.entrySet().stream()
                .map(entry -> entry.getValue().toString())
                .collect(Collectors.toList());
    }


}

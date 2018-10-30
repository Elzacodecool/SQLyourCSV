package com.codecool.model.query;

public enum SQLAggregateFunctions {
    SUM("sum"),
    AVG("avg"),
    MIN("min"),
    MAX("max");

    private String name;

    SQLAggregateFunctions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

package com.codecool.model.query;

import java.util.List;
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

    public double calculate(List<Integer> numbers) {
        switch (this) {
            case SUM:
                return numbers.stream().mapToInt(Integer::intValue).sum();
            case AVG:
                return numbers.stream().mapToInt(Integer::intValue).average().orElse(0);
            case MIN:
                return numbers.stream().mapToInt(Integer::intValue).min().orElse(0);
            case MAX:
                return numbers.stream().mapToInt(Integer::intValue).max().orElse(0);
            default:
                return 0;
        }
    }
}

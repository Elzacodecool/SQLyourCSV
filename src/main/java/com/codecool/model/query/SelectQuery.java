package com.codecool.model;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class QueryInterpreter {

    private String query;

    private SQLCommand command;
    private List<String> columnNames;
    private Map<SQLAggregateFunctions, List<String>> functions;
    private List<String> fileName;
    private List<Predicate<Row>> joinConditions;
    private Predicate<Row> whereCondition;
    private String groupByColumn;


    public QueryInterpreter(String query) {
        this.query = query;
    }

    




}

package com.codecool.model.query;

import com.codecool.model.Row;

import java.util.function.Predicate;

public class DeleteQuery {
    private String query;

    private String fileName;
    private Predicate<Row> whereCondition;

    public DeleteQuery(String query, String fileName, Predicate<Row> whereCondition) {
        this.query = query;
        this.fileName = fileName;
        this.whereCondition = whereCondition;
    }

    public String getQuery() {
        return query;
    }

    public String getFileName() {
        return fileName;
    }

    public Predicate<Row> getWhereCondition() {
        return whereCondition;
    }
}

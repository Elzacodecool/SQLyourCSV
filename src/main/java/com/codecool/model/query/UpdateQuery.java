package com.codecool.model.query;

import com.codecool.model.Row;

import java.util.List;
import java.util.function.Predicate;

public class UpdateQuery {
    private String query;

    private String fileName;
    private List<List<String>> setCondition;
    private Predicate<Row> whereCondition;


    public UpdateQuery(String query, String fileName, List<List<String>> setCondition, Predicate<Row> whereCondition) {
        this.query = query;
        this.fileName = fileName;
        this.setCondition = setCondition;
        this.whereCondition = whereCondition;
    }

    public String getQuery() {
        return query;
    }

    public String getFileName() {
        return fileName;
    }

    public List<List<String>> getSetCondition() {
        return setCondition;
    }

    public Predicate<Row> getWhereCondition() {
        return whereCondition;
    }
}

package com.codecool.model.query;

import com.codecool.model.Row;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SelectQuery {
    private String query;

    private List<String> fileNames;
    private Predicate<Row> whereCondition;
    private List<String> columnNames;
    private Map<SQLAggregateFunctions, List<String>> functions;
    private List<List<String>> joinConditions;
    private String groupByColumn;
    private Predicate<Row> havingCondition;
    private boolean isValidate;


    public SelectQuery(String query, List<String> fileNames, Predicate<Row> whereCondition,
                       List<String> columnNames, Map<SQLAggregateFunctions, List<String>> functions,
                       List<List<String>> joinConditions, String groupByColumn, Predicate<Row> havingCondition,
                       boolean isValidate) {

        this.query = query;
        this.fileNames = fileNames;
        this.whereCondition = whereCondition;
        this.columnNames = columnNames;
        this.functions = functions;
        this.joinConditions = joinConditions;
        this.groupByColumn = groupByColumn;
        this.havingCondition = havingCondition;
        this.isValidate = isValidate;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public Predicate<Row> getWhereCondition() {
        return whereCondition;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public Map<SQLAggregateFunctions, List<String>> getFunctions() {
        return functions;
    }

    public List<List<String>> getJoinConditions() {
        return joinConditions;
    }

    public String getGroupByColumn() {
        return groupByColumn;
    }

    public Predicate<Row> getHavingCondition() {
        return havingCondition;
    }

    public boolean isValidate() {
        return isValidate;
    }

    public String getQuery() {
        return query;
    }
}

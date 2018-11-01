package com.codecool.model.query;

import com.codecool.interpreter.SelectQueryInterpreter;
import com.codecool.interpreter.SelectQueryValidator;
import com.codecool.model.Row;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SelectQuery {
    private SelectQueryInterpreter interpreter = new SelectQueryInterpreter();
    private SelectQueryValidator validator = new SelectQueryValidator();

    private String query;

    private List<String> fileNames;
    private Predicate<Row> whereCondition;
    private List<String> columnNames;
    private Map<SQLAggregateFunctions, List<String>> functions;
    private List<List<String>> joinConditions;
    private String groupByColumn;
    private Predicate<Row> havingCondition;
    private boolean isValidate;


    public SelectQuery(String query) {
        super();
        this.isValidate = validator.validateQuery(query);

        if(isValidate) {
            this.query = query.toLowerCase().replace(";", "");
            functions = interpreter.getFunctions(this.query);
            columnNames = interpreter.getColumnNames(this.query);
            fileNames = interpreter.getFilenames(this.query);
            groupByColumn = interpreter.getGroupBy(this.query);
            whereCondition = interpreter.getWherePredicate(this.query);
            havingCondition = interpreter.getHavingPredicate(query);
            joinConditions = interpreter.getJoinConditions(this.query);
        }
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

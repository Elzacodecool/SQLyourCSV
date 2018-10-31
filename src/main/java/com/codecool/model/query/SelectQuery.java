package com.codecool.model.query;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectQuery {

    private String query;

    private List<String> fileNames;
    private Predicate<Row> whereCondition;
    private List<String> columnNames;
    private Map<SQLAggregateFunctions, List<String>> functions;
    private List<List<String>> joinConditions;
    private String groupByColumn;
    private boolean isValidate;


    public SelectQuery(String query) {

        super();
        this.isValidate = validateQuery(query);

        if(isValidate) {
            this.query = query.toLowerCase().replace(";", "");
            functions = getFunctions(this.query);
            columnNames = getColumnNames(this.query);
            fileNames = getFilenames(this.query);
            groupByColumn = getGroupBy(this.query);
            whereCondition = getPredicate(this.query);
            joinConditions = getJoinConditions(this.query);
        }



    }

    private boolean validateQuery(String query) { {
        List<String> queryList = mapQueryToList(query.toLowerCase());
        int index = queryList.indexOf("where");
        List<String> queryListBeforeWHERE;
        List<String> queryListAfterWHERE;

        if(index != -1) {
            queryListBeforeWHERE = queryList.subList(0, index);
            queryListAfterWHERE = queryList.subList(index+1, queryList.size());
        } else {
            queryListBeforeWHERE = queryList;
        }

        boolean isValidated = true;

        if(queryList.lastIndexOf("select") != 0) {
            isValidated = false;
        } else if (!queryListBeforeWHERE.contains("from")) {
            isValidated = false;
        } else if(queryList.contains("join") && queryList.lastIndexOf("from") > queryList.lastIndexOf("join")) {
            isValidated = false;
        } else if(Collections.frequency(queryListBeforeWHERE, "from") > 1 ||
                  Collections.frequency(queryListBeforeWHERE, "select") > 1 ||
                  Collections.frequency(queryListBeforeWHERE, "where") > 0) {
            isValidated = false;
        } else if(Collections.frequency(queryListBeforeWHERE, "join") !=
                  Collections.frequency(queryListBeforeWHERE, "on")) {
            isValidated = false;
        } else if (queryList.indexOf("from") - queryList.indexOf("select") == 1) {
            isValidated = false;
        } else if (queryListBeforeWHERE.stream()
                                       .reduce((x, y) -> x.toString() + y.toString())
                                       .orElse(null).contains("joinon")) {
            isValidated = false;
        } else if(queryListBeforeWHERE.indexOf("join") - queryListBeforeWHERE.indexOf("from") != 1) {
            isValidated = false;
        }
        return isValidated;


    }

    }

    private List<String> getFilenames(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("from");

        String fileName = words.get(indexFrom + 1);


        return Stream.concat(Arrays.asList(fileName).stream(), getJoinFileNames(words).stream())
                     .collect(Collectors.toList());
    }

    private List<String> getJoinFileNames(List<String> query) {
        int indexFrom = query.indexOf("join");


        if (indexFrom > -1 && indexFrom < query.size() - 1) {
            String fileName = query.get(indexFrom + 1);
            return Stream.concat(Arrays.asList(fileName).stream()
                                ,getJoinFileNames(query.subList(indexFrom+1, query.size())).stream())
                    .collect(Collectors.toList());

        } else {
            return new ArrayList<>();
        }
    }

    private Map<SQLAggregateFunctions, List<String>> getFunctions(String query) {
        List<String> columns = getListColumns(query);

        return Arrays.stream(SQLAggregateFunctions.values())
                     .collect(Collectors.toMap
                            (f -> f, f -> columns.stream()
                                                 .filter(c -> c.contains(f.getName()))
                                                 .collect(Collectors.toList()))
                            );
    }

    private List<String> getColumnNames(String query) {
        List<String> columns = getListColumns(query);


        return columns.stream()
                      .filter(c -> Arrays.stream(SQLAggregateFunctions.values())
                                   .noneMatch(f -> c.contains(f.getName())))
                      .collect(Collectors.toList());

    }

    private List<String> getListColumns(String query) {
        String columnsPart = query.substring(query.indexOf("select"), query.indexOf("from"));
        return Arrays.stream(columnsPart.split(" "))
                .filter(word -> query.indexOf(word) > query.indexOf("select"))
                .filter(word -> query.indexOf(word) < query.indexOf("from"))
                .map(word -> word.replace(",", ""))
                .collect(Collectors.toList());
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

    private String getGroupBy(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("group");
        if (indexFrom < 0) {
            return null;
        }

        return words.get(indexFrom + 2);
    }



    private Predicate<Row> getPredicate(String query) {
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
                predicate = (row) -> row.getData().get(columnName).toString().equals(value);
                break;
            case ">":
                predicate = (row) -> Integer.valueOf(row.getData().get(columnName).toString()) > Integer.valueOf(value);
                break;
            case "<":
                predicate = (row) -> Integer.valueOf(row.getData().get(columnName).toString()) < Integer.valueOf(value);
                break;
            case "<>":
                predicate = (row) -> !row.getData().get(columnName).toString().equals(value);
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

    private List<List<String>> getJoinConditions(String query) {
        if(query.contains("on")) {
            List<String> queryList = mapQueryToList(query);
            List<String> condition = queryList.stream()
                    .skip(queryList.indexOf("on") + 1)
                    .collect(Collectors.toList());
            return buildJoinCondition(condition);
        }
        return null;

    }

    private List<List<String>> buildJoinCondition(List<String> condition) {

        int index = condition.indexOf("on");
        String[] columnSet = {condition.get(0), condition.get(2)};
        if(index < 0) {
            return Arrays.asList(Arrays.asList(columnSet));
        } else {
            return Stream.concat(Arrays.asList(Arrays.asList(columnSet)).stream(),
                    buildJoinCondition(condition.subList(index+1, condition.size())).stream())
                    .collect(Collectors.toList());
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

    public Set<String> getAllColumns() {
        List<String> functionColumns = getFunctions().values()
                                                     .stream()
                                                     .flatMap(List::stream)
                                                     .collect(Collectors.toList());
        return Stream.concat(getColumnNames().stream(), functionColumns.stream())
                     .collect(Collectors.toSet());
    }

    public boolean isValidate() {
        return isValidate;
    }
}

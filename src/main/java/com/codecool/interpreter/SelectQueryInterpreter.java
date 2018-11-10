package com.codecool.interpreter;

import com.codecool.model.Row;
import com.codecool.model.query.SQLAggregateFunctions;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectQueryInterpreter {
    public SelectQueryInterpreter() {}

    public List<String> getFilenames(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("from");

        String fileName = words.get(indexFrom + 1);


        return Stream.concat(Stream.of(fileName), getJoinFileNames(words).stream())
                     .collect(Collectors.toList());
    }

    private List<String> getJoinFileNames(List<String> query) {
        int indexFrom = query.indexOf("join");


        if (indexFrom > -1 && indexFrom < query.size() - 1) {
            String fileName = query.get(indexFrom + 1);
            return Stream.concat(Stream.of(fileName)
                                ,getJoinFileNames(query.subList(indexFrom+1, query.size())).stream())
                    .collect(Collectors.toList());

        } else {
            return new ArrayList<>();
        }
    }

    public Map<SQLAggregateFunctions, List<String>> getFunctions(String query) {
        List<String> columns = getListColumns(query);

        return Arrays.stream(SQLAggregateFunctions.values())
                     .collect(Collectors.toMap
                            (f -> f, f -> columns.stream()
                                                 .filter(c -> c.contains(f.getName()))
                                                 .collect(Collectors.toList()))
                            );
    }

    public List<String> getColumnNames(String query) {
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

    public static List<String> mapQueryToList(String text) {
        return Arrays.stream(Arrays.stream(text.split(" "))
                .map(word -> word.length() > 1 ? word.replace("=", " = ") : word)
                .map(word -> !word.contains("<>") && word.length() > 1 ? word.replace(">", " > ") : word)
                .map(word -> !word.contains("<>") && word.length() > 1 ? word.replace("<", " < ") : word)
                .map(word -> word.length() > 2 ? word.replace("<>", " <> ") : word)
                .collect(Collectors.joining(" "))
                .split(" "))
                .collect(Collectors.toList());
    }

    public String getGroupBy(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("group");
        if (indexFrom < 0) {
            return null;
        }

        return words.get(indexFrom + 2);
    }

    public Predicate<Row> getWherePredicate(String query) {
        if (query.contains("group by")) {
            return getPredicate(query.substring(0, query.indexOf("group by")), "where");
        }
        return getPredicate(query, "where");
    }

    public Predicate<Row> getHavingPredicate(String query) {
        return getPredicate(query, "having");
    }

    private Predicate<Row> getPredicate(String query, String conditionWord) {
        if (query.contains(conditionWord)) {
            List<String> queryList = mapQueryToList(query);
            List<String> condition = queryList.stream()
                    .skip(queryList.indexOf(conditionWord) + 1)
                    .collect(Collectors.toList());
            return buildPredicate(condition);
        }

        return (row) -> true;
    }

    private Predicate<Row> buildPredicate(List<String> condition) {
        Predicate<Row> predicate;
        String columnName = condition.get(condition.size()-3);
        String operator = condition.get(condition.size()-2);
        String value = condition.get(condition.size()-1).replace("\'", "");


        switch (operator) {
            case "=":
                predicate = (row) -> row.getData().get(columnName).toString().equals(value);
                break;
            case ">":
                predicate = (row) -> Float.valueOf(row.getData().get(columnName).toString()) > Float.valueOf(value);
                break;
            case "<":
                predicate = (row) -> Float.valueOf(row.getData().get(columnName).toString()) < Float.valueOf(value);
                break;
            case "<>":
                predicate = (row) -> !row.getData().get(columnName).toString().equals(value);
                break;
            case "like":
                predicate = (row) -> row.getData().get(columnName) instanceof String &&
                        like(row.getData().get(columnName).toString(), value);
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

    private boolean like(String string, String value) {
        String expression = value.replace(".", "\\.")
                .replace("_", ".")
                .replace("%", ".*");
        return string.matches(expression);
    }

    public List<List<String>> getJoinConditions(String query) {
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
        if (index < 0) {
            return Collections.singletonList(Arrays.asList(columnSet));
        } else {
            return Stream.concat(Stream.of(Arrays.asList(columnSet)),
                    buildJoinCondition(condition.subList(index + 1, condition.size())).stream())
                    .collect(Collectors.toList());
        }
    }
}

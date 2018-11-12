package com.codecool.interpreter;

import com.codecool.model.Row;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryInterpreter {


    Predicate<Row> getPredicate(String query, String conditionWord) {
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

    static List<String> mapQueryToList(String text) {
        return Arrays.stream(Arrays.stream(text.split(" "))
                .map(word -> word.length() > 1 ? word.replace("=", " = ") : word)
                .map(word -> !word.contains("<>") && word.length() > 1 ? word.replace(">", " > ") : word)
                .map(word -> !word.contains("<>") && word.length() > 1 ? word.replace("<", " < ") : word)
                .map(word -> word.length() > 2 ? word.replace("<>", " <> ") : word)
                .map(word ->  word.replace(",", " ,"))
                .collect(Collectors.joining(" "))
                .split(" "))
                .collect(Collectors.toList());
    }


    List<List<String>> buildCondition(List<String> condition, String splitter) {

        int index = condition.indexOf(splitter);
        String[] columnSet = {condition.get(0), condition.get(2)};
        if (index < 0) {
            return Collections.singletonList(Arrays.asList(columnSet));
        } else {
            return Stream.concat(Stream.of(Arrays.asList(columnSet)),
                    buildCondition(condition.subList(index + 1, condition.size()), splitter).stream())
                    .collect(Collectors.toList());
        }
    }

    List<String> concatListsString(List<String> list1, List<String> list2) {
        return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
    }
}

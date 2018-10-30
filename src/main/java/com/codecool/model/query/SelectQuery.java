package com.codecool.model.query;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collector;
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


    public SelectQuery(String query) {

        super();

        this.query = query.toLowerCase().replace(";", "");
        functions = getFunctions(this.query);
        columnNames = getColumnNames(this.query);
        fileNames = getFilenames(this.query);
        groupByColumn = getGroupBy(this.query);
        whereCondition = getPredicate(this.query);



    }

    private List<String> getFilenames(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("from");
        if (indexFrom < 0) {
            throw new WrongQueryFormatException("Missing FROM statement");
        } else if (indexFrom >= words.size() - 1) {
            throw new WrongQueryFormatException("Missing filename");
        }

        String fileName = words.get(indexFrom + 1);


        return Stream.concat(Arrays.asList(fileName).stream(), getJoinFileNames(query).stream())
                     .collect(Collectors.toList());
    }

    private List<String> getJoinFileNames(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("join");


        if (indexFrom > -1 && indexFrom < words.size() - 1) {
            String fileName = words.get(indexFrom + 1);
            return Stream.concat(Arrays.asList(fileName).stream(), getJoinFileNames(query.substring(indexFrom+1)).stream())
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
        return Arrays.stream(query.split(" "))
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
            throw new WrongQueryFormatException("Missing GROUP BY statement");
        } else if (indexFrom >= words.size() - 1) {
            throw new WrongQueryFormatException("Missing columnName");
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
                predicate = (row) -> row.getData().get(columnName).equals(value);
                break;
            case ">":
                predicate = (row) -> Integer.valueOf(row.getData().get(columnName).toString()) > Integer.valueOf(value);
                break;
            case "<":
                predicate = (row) -> Integer.valueOf(row.getData().get(columnName).toString()) < Integer.valueOf(value);
                break;
            case "<>":
                predicate = (row) -> !row.getData().get(columnName).equals(value);
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

    private List<Predicate<Row>> getJoinConditions(String query) {

    }







}

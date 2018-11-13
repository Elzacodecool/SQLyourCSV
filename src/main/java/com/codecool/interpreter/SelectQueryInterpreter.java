package com.codecool.interpreter;

import com.codecool.model.Row;
import com.codecool.model.query.SQLAggregateFunctions;
import com.codecool.model.query.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SelectQueryInterpreter extends QueryInterpreter {
    private SelectQueryValidator validator;

    @Autowired
    public SelectQueryInterpreter(SelectQueryValidator validator) {
        this.validator = validator;
    }

    public List<String> getFilenames(String query) {
        List<String> words = Arrays.asList(query.split(" "));

        return Stream.concat(Stream.of(getFilename(query)), getJoinFileNames(words).stream())
                     .collect(Collectors.toList());
    }

    private String getFilename(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("from");

        return words.get(indexFrom + 1);
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

    public List<List<String>> getJoinConditions(String query) {
        String wordCondition = "on";
        if(query.contains(wordCondition)) {
            List<String> queryList = mapQueryToList(query);
            List<String> condition = queryList.stream()
                    .skip(queryList.indexOf(wordCondition) + 1)
                    .collect(Collectors.toList());
            return buildCondition(condition, wordCondition);
        }
        return null;
    }

    public SelectQuery getSelectQuery(String selectQuery) {
        String query = selectQuery.replace(";", "");
        return new SelectQuery(
                selectQuery,
                getFilenames(query),
                getWherePredicate(query),
                getColumnNames(query),
                getFunctions(query),
                getJoinConditions(query),
                getGroupBy(query),
                getHavingPredicate(query),
                validator.validateQuery(query)
        );
    }
}

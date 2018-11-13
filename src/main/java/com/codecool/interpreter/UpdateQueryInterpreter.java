package com.codecool.interpreter;

import com.codecool.model.Row;
import com.codecool.model.query.UpdateQuery;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class UpdateQueryInterpreter extends QueryInterpreter {
    public String getFilename(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("update");

        return words.get(indexFrom + 1);
    }

    public Predicate<Row> getWherePredicate(String query) {
        return getPredicate(query, "where");
    }

    public List<List<String>> getSetCondition(String query) {
        List<String> words = mapQueryToList(query);
        int setIndex = words.indexOf("set");
        List<String> condition = words.stream()
                .filter(word -> words.indexOf(word) > setIndex)
                .collect(Collectors.toList());
        return buildCondition(condition, ",");
    }

    public UpdateQuery getUpdateQuery(String updateQuery) {
        String query = updateQuery.replace(";", "");

        return new UpdateQuery(
                updateQuery,
                getFilename(query),
                getSetCondition(query),
                getWherePredicate(query)
        );
    }
}

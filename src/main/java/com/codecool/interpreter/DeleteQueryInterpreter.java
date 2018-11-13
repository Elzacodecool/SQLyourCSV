package com.codecool.interpreter;

import com.codecool.model.Row;
import com.codecool.model.query.DeleteQuery;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class DeleteQueryInterpreter extends QueryInterpreter {
    public String getFilename(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("from");

        return words.get(indexFrom + 1);
    }

    public Predicate<Row> getWherePredicate(String query) {
        return getPredicate(query, "where");
    }

    public DeleteQuery getDeleteQuery(String deleteQuery) {
        String query = deleteQuery.replace(";", "");

        return new DeleteQuery(
                deleteQuery,
                getFilename(query),
                getWherePredicate(query)
        );
    }
}

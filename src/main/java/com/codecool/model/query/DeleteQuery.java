package com.codecool.model.query;

import com.codecool.interpreter.DeleteQueryInterpreter;
import com.codecool.model.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class DeleteQuery {
    private DeleteQueryInterpreter interpreter;
    private String query;

    private String fileName;
    private Predicate<Row> whereCondition;

    @Autowired
    public DeleteQuery(DeleteQueryInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void setQuery(String query) {
        this.query = query.replace(";", "");
        fileName = interpreter.getFilename(this.query);
        whereCondition = interpreter.getWherePredicate(this.query);
    }

    public String getQuery() {
        return query;
    }

    public String getFileName() {
        return fileName;
    }

    public Predicate<Row> getWhereCondition() {
        return whereCondition;
    }
}

package com.codecool.model.query;

import com.codecool.interpreter.UpdateQueryInterpreter;
import com.codecool.model.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class UpdateQuery {
    private final UpdateQueryInterpreter interpreter;
    private String query;

    private String fileName;
    private List<List<String>> setCondition;
    private Predicate<Row> whereCondition;


    @Autowired
    public UpdateQuery(UpdateQueryInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void setQuery(String query) {
        this.query = query.replace(";", "");
        fileName = interpreter.getFilename(this.query);
        setCondition = interpreter.getSetCondition(this.query);
        whereCondition = interpreter.getWherePredicate(this.query);
    }

    public String getQuery() {
        return query;
    }

    public String getFileName() {
        return fileName;
    }

    public List<List<String>> getSetCondition() {
        return setCondition;
    }

    public Predicate<Row> getWhereCondition() {
        return whereCondition;
    }
}

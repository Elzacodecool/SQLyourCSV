package com.codecool.interpreter;


import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class SelectQueryValidator {
    public SelectQueryValidator() {}

    public boolean validateQuery(String query) {
        List<String> queryList = SelectQueryInterpreter.mapQueryToList(query);
        int index = queryList.indexOf("where");
        List<String> queryListBeforeWHERE;
        List<String> queryListAfterWHERE;

        if(index != -1) {
            queryListBeforeWHERE = queryList.subList(0, index);
            queryListAfterWHERE = queryList.subList(index+1, queryList.size());
        } else {
            queryListBeforeWHERE = queryList;
        }

        if(queryList.lastIndexOf("select") != 0) {
            return false;
        } else if (!queryListBeforeWHERE.contains("from")) {
            return false;
        } else if(queryList.contains("join") && queryList.lastIndexOf("from") > queryList.lastIndexOf("join")) {
            return false;
        } else if(Collections.frequency(queryListBeforeWHERE, "from") > 1 ||
                  Collections.frequency(queryListBeforeWHERE, "select") > 1 ||
                  Collections.frequency(queryListBeforeWHERE, "where") > 0) {
            return false;
        } else if(Collections.frequency(queryListBeforeWHERE, "join") !=
                  Collections.frequency(queryListBeforeWHERE, "on")) {
            return false;
        } else if (queryList.indexOf("from") - queryList.indexOf("select") == 1) {
            return false;
        } else if (queryListBeforeWHERE.stream()
                                       .reduce((x, y) -> x + y)
                                       .orElse(null).contains("joinon")) {
            return false;
        } else if(queryListBeforeWHERE.contains("join") && queryListBeforeWHERE.indexOf("join") - queryListBeforeWHERE.indexOf("from") != 2) {
            return false;
        } else if(queryListBeforeWHERE.contains("and") || queryListBeforeWHERE.contains("or")) {
            return false;
        }
        return true;
    }
}

package com.codecool.model;

import com.codecool.service.SelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class QueryInterpreter {
    private String query;



    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}

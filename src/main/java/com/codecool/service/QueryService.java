package com.codecool.service;

import com.codecool.model.Row;
import com.codecool.model.Table;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public abstract class QueryService {
    public abstract Table executeQuery(String query);
    abstract String getFilename(String query);
    abstract Predicate<Row> getPredicate(String query);
}

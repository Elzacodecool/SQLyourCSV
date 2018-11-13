package com.codecool.service;

import com.codecool.model.Table;
import org.springframework.stereotype.Service;


@Service
public abstract class QueryService {
    public abstract Table executeQuery(String query);
}

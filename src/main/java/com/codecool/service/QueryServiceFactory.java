package com.codecool.service;

import org.springframework.stereotype.Service;

@Service
public class QueryServiceFactory {

    public QueryService getQueryService(String query) {
        String command = query.split(" ")[0].toLowerCase();

        switch (command){
            case "select":
                return new SelectService();
            case "update":
                return new UpdateService();
            case "delete":
                return new DeleteService();
            default:
                return null;
        }
    }
}

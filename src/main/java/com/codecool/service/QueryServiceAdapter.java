package com.codecool.service;

import com.codecool.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryServiceAdapter {

    private SelectService selectService;
    private UpdateService updateService;
    private DeleteService deleteService;

    @Autowired
    public QueryServiceAdapter(SelectService selectService, UpdateService updateService, DeleteService deleteService) {
        this.selectService = selectService;
        this.updateService = updateService;
        this.deleteService = deleteService;
    }

    public Table executeQuery(String query) {
        String command = query.split(" ")[0].toLowerCase();

        switch (command){
            case "select":
                return selectService.executeQuery(query);
            case "update":
                return updateService.executeQuery(query);
            case "delete":
                return deleteService.executeQuery(query);
            default:
                return null;
        }
    }
}

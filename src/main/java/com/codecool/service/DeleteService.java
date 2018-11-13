package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.model.Row;
import com.codecool.model.Table;
import com.codecool.model.query.DeleteQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeleteService extends QueryService {
    private Converter converter;
    private DeleteQuery deleteQuery;

    @Autowired
    public DeleteService(Converter converter, DeleteQuery deleteQuery) {
        this.converter = converter;
        this.deleteQuery = deleteQuery;
    }

    public DeleteService() {
    }

    @Override
    public Table executeQuery(String query) {
        deleteQuery.setQuery(query);
        Table table = converter.convert(deleteQuery.getFileName());
        Table updatedTable = delete(table);
        converter.convertBeforeWriting(updatedTable, deleteQuery.getFileName());
        return updatedTable;
    }

    private Table delete(Table table) {
        return new Table(table.getColumnNames(), delete(table.getRows()));
    }

    private List<Row> delete(List<Row> rows) {
        List<Row> rowsToDelete = rows.stream()
                .filter(deleteQuery.getWhereCondition())
                .collect(Collectors.toList());
        return rows.stream()
                .filter(row -> !rowsToDelete.contains(row))
                .collect(Collectors.toList());
    }
}

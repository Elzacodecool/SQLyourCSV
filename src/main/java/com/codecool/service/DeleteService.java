package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.interpreter.DeleteQueryInterpreter;
import com.codecool.model.Row;
import com.codecool.model.Table;
import com.codecool.model.query.DeleteQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class DeleteService extends QueryService {
    private Converter converter;
    private DeleteQueryInterpreter interpreter;

    @Autowired
    public DeleteService(Converter converter, DeleteQueryInterpreter interpreter) {
        this.converter = converter;
        this.interpreter = interpreter;
    }

    public DeleteService() {
    }

    @Override
    public Table executeQuery(String query) {
        DeleteQuery deleteQuery = interpreter.getDeleteQuery(query);

        Table table = converter.convert(deleteQuery.getFileName());
        Table updatedTable = delete(table, deleteQuery.getWhereCondition());
        converter.convertBeforeWriting(updatedTable, deleteQuery.getFileName());
        return updatedTable;
    }

    private Table delete(Table table, Predicate<Row> whereCondition) {
        return new Table(table.getColumnNames(), delete(table.getRows(), whereCondition));
    }

    private List<Row> delete(List<Row> rows, Predicate<Row> whereCondition) {
        List<Row> rowsToDelete = rows.stream()
                .filter(whereCondition)
                .collect(Collectors.toList());
        return rows.stream()
                .filter(row -> !rowsToDelete.contains(row))
                .collect(Collectors.toList());
    }
}

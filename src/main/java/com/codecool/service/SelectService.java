package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import com.codecool.model.query.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SelectService {
    private Converter converter;

    @Autowired
    public SelectService(Converter converter) {
        this.converter = converter;
    }


    public SelectService() {
    }

    public Table executeQuery(String query) {

        SelectQuery selectQuery = new SelectQuery(query);




        return null;

    }

//    private Table joinTables(Table table1, Table table2, List<String> condition) {
//
//        List<String> columns = Stream.concat(table1.getColumnNames().stream(), table2.getColumnNames().stream())
//                                     .collect(Collectors.toList());
//
//        table1.getRows().stream().
//    }

//    Row getIncreasedRowWithColumns(Row row1, Row row2, List<String> columns) {
//        Map<String, Object> rotData = row1.getData().merge(row2.getData());
//        return new Row(columns.stream().collect(Collectors.toMap(column -> column, column->)))
//    }


    Row getUpdatedRowWithColumns(Row row, List<String> columns) {
        return new Row(
                columns.stream()
                        .collect(Collectors.toMap(column -> column, column -> row.getData().get(column)))
        );
    }

//    List<String> getValidatedListColumns(SelectQuery selectQuery, Table table) {
//        List<String> columns = selectQuery.getAllColumns();
//

//        if (columns.contains("*")) {
//            return table.getColumnNames();
//        }
//
//        if (checkIfColumnsExistInTable(columns, table)) {
//            return columns;
//        }
//
//        throw new WrongQueryFormatException("No column in table");
//    }

    private boolean checkIfColumnsExistInTable(List<String> columns, Table table) {
        return table.getColumnNames().containsAll(columns);
    }



}

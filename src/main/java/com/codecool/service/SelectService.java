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
import java.util.stream.IntStream;
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
        Table table = joinTables(selectQuery.getFileNames(), selectQuery.getJoinConditions());

        return table;
    }


    private Table joinTables(List<String> fileNames, List<List<String>> conditions) {
        Table firstTable = converter.convert(fileNames.get(0));
        if(fileNames.size() == 1) {
            return firstTable;
        }

        List<Table> joinTables = fileNames.stream()
                    .skip(1)
                    .map(filename -> converter.convert(filename))
                    .collect(Collectors.toList());
        Map<Table, List<String>> joinTableWithCondition = IntStream.range(0, fileNames.size() - 1)
                .boxed()
                .collect(Collectors.toMap(
                        joinTables::get,
                        conditions::get
                ));

        return joinTables.stream()
                .reduce(firstTable, (joinedTable, table) -> joinTables(joinedTable, table, joinTableWithCondition.get(table)));
    }


    private Table joinTables(Table table1, Table table2, List<String> condition) {

        List<String> columns = Stream.concat(table2.getColumnNames().stream(), table1.getColumnNames().stream())
                                     .collect(Collectors.toList());

        String firstColumn = condition.get(0);
        String secondColumn = condition.get(1);

        List<Row> rows = table1.getRows().stream()
                .map(row -> mergeRows(row,
                        table2.getRows().stream()
                                .filter(row2 -> row.getData().get(firstColumn).toString().equals(row2.getData().get(secondColumn).toString()))
                                .findFirst().orElse(null)
                ))
                .collect(Collectors.toList());

        return new Table(columns, rows);
    }

    Row mergeRows(Row row1, Row row2) {
        Map<String, Object> rowData = Stream.of(row2.getData(), row1.getData()).flatMap(m -> m.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new Row(rowData);
    }


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

package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.exception.WrongQueryFormatException;
import com.codecool.interpreter.SelectQueryInterpreter;
import com.codecool.model.Row;
import com.codecool.model.Table;
import com.codecool.model.query.SQLAggregateFunctions;
import com.codecool.model.query.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class SelectService extends QueryService {
    private Converter converter;
    private SelectQueryInterpreter interpreter;

    @Autowired
    public SelectService(Converter converter, SelectQueryInterpreter interpreter) {
        this.converter = converter;
        this.interpreter = interpreter;
    }


    public SelectService() {
    }

    @Override
    public Table executeQuery(String query) {
        SelectQuery selectQuery = interpreter.getSelectQuery(query);

        if(!selectQuery.isValidate()) {
            throw new WrongQueryFormatException("wrong Query format");
        }
        Table joinedTable = joinTables(selectQuery.getFileNames(), selectQuery.getJoinConditions());
        Table tableAfterWhere = executeCondition(joinedTable, selectQuery.getWhereCondition());
        if (selectQuery.getGroupByColumn() == null) {
            return getTableWithColumns(tableAfterWhere,
                    selectQuery.getColumnNames(), selectQuery.getFunctions());
        } else {
            return executeCondition(
                    getTableWithColumns(
                            groupBy(tableAfterWhere, selectQuery.getGroupByColumn()),
                            selectQuery.getColumnNames(), selectQuery.getFunctions()
                    ),
                    selectQuery.getHavingCondition()
            );
        }
    }

    private Table getTableWithColumns(Table table,
                                      List<String> columnNames,
                                      Map<SQLAggregateFunctions, List<String>> functions) {

        if (!columnNames.isEmpty()) {
            return getTableWithColumns(table, columnNames);
        } else {
            return getTableWithColumns(table, functions);
        }
    }

    private Table getTableWithColumns(Table table,
                                      List<String> columnNames) {

        List<String> columns = addColumnsIfNeeded(columnNames, table.getColumnNames());
        List<Row> rows = table.getRows().stream()
                .map(row -> getUpdatedRowWithColumns(row, columns))
                .collect(Collectors.toList());

        return new Table(columns, rows);
    }

    private List<String> addColumnsIfNeeded(List<String> columns, List<String> columnsToAdd) {
        if (columns.contains("*")) {
            int index = columns.indexOf("*");
            return concatListsString(
                    concatListsString(
                            columns.subList(0, index),
                            columnsToAdd),
                    columns.subList(index + 1, columns.size())
            );
        }
        return columns;
    }

    private Table getTableWithColumns(Table table, Map<SQLAggregateFunctions, List<String>> functions) {
        Row row = getRowWithFunctions(table.getRows(), functions);
        List<String> columnNames = getColumnNames(row);

        return new Table(columnNames, Collections.singletonList(row));
    }

    private Table getTableWithColumns(List<Table> tables,
                                      List<String> columns,
                                      Map<SQLAggregateFunctions, List<String>> functions) {
        if (columns.isEmpty()) {
            return getTableWithColumns(tables, functions);
        } else {
            return getTableWithColumns(tables, columns.get(0), functions);
        }
    }


    private Table getTableWithColumns(List<Table> tables,
                                      Map<SQLAggregateFunctions, List<String>> functions) {
        List<Row> rows = tables.stream()
                .map(table -> getRowWithFunctions(table.getRows(), functions))
                .collect(Collectors.toList());

        List<String> columnsToTable = rows.isEmpty()
                ? getColumnNamesFunctions(functions)
                : getColumnNames(rows.get(0));

        return new Table(columnsToTable, rows);
    }

    private List<String> getColumnNames(Row row) {
        return new ArrayList<>(row.getData().keySet());
    }

    private Table getTableWithColumns(List<Table> tables,
                                      String column,
                                      Map<SQLAggregateFunctions, List<String>> functions) {
        List<Row> rows = tables.stream()
                .map(table -> getRowWithFunctions(table.getRows(), functions, column))
                .collect(Collectors.toList());

        List<String> columnsToTable = rows.isEmpty()
                ? concatListsString(Collections.singletonList(column), getColumnNamesFunctions(functions))
                : new ArrayList<>(rows.get(0).getData().keySet());

        return new Table(columnsToTable, rows);
    }

    private List<String> concatListsString(List<String> list1, List<String> list2) {
        return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
    }

    private List<String> getColumnNamesFunctions(Map<SQLAggregateFunctions, List<String>> functions) {
        return functions.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Table> groupBy(Table table, String groupByColumn) {
        Set<Object> valuesFromColumn = table.getRows().stream()
                .map(row -> row.getData().get(groupByColumn))
                .collect(Collectors.toSet());

        return valuesFromColumn.stream()
                .map(value ->
                        table.getRows().stream()
                            .filter(row -> row.getData().get(groupByColumn).toString().equals(value.toString()))
                            .collect(Collectors.toList())
                        )
                .map(rows -> new Table(table.getColumnNames(), rows))
                .collect(Collectors.toList());
    }

    private Table executeCondition(Table table, Predicate<Row> condition) {
        return new Table(table.getColumnNames(), table.getRows().stream()
                                                                .filter(condition)
                                                                .collect(Collectors.toList()));
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
        String firstColumn = condition.get(0);
        String secondColumn = condition.get(1);

        List<Row> rows = table1.getRows().stream()
                .map(row -> mergeRows(row,
                        table2.getRows().stream()
                                .filter(row2 -> row.getData().get(firstColumn).toString().equals(row2.getData().get(secondColumn).toString()))
                                .findFirst().orElse(null)
                ))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> columns = rows.isEmpty()
                ? concatListsString(table1.getColumnNames(), table2.getColumnNames())
                : getColumnNames(rows.get(0));

        return new Table(columns, rows);
    }

    private Row mergeRows(Row row1, Row row2) {
        if (row1 == null | row2 == null) {
            return null;
        }

        Map<String, Object> rowData = Stream.of(row2.getData(), row1.getData()).flatMap(m -> m.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new Row(rowData);
    }


    private Row getUpdatedRowWithColumns(Row row, List<String> columns) {
        return new Row(
                columns.stream()
                        .collect(Collectors.toMap(column -> column, column -> row.getData().get(column)))
        );
    }

    private Row getRowWithFunctions(List<Row> rows, Map<SQLAggregateFunctions, List<String>> functions, String column) {
        return new Row (
                concatMaps(
                        getRowWithFunctions(rows, functions).getData(),
                        Collections.singletonMap(column, rows.get(0).getData().get(column))
                )
        );
    }

    private Map<String, Object> concatMaps(Map<String, Object> map1, Map<String, Object> map2) {
        return Stream.of(map1, map2).flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Row getRowWithFunctions(List<Row> rows, Map<SQLAggregateFunctions, List<String>> functions) {
        Function<String, List<Integer>> valuesFromColumn = columnName -> rows.stream()
                .map(row -> row.getData().get(columnName).toString())
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        BiFunction<SQLAggregateFunctions, String, Double> calculateFunction = (function, name) ->
                function.calculate(valuesFromColumn.apply(name.split("[()]")[1]));

        Stream<Map<String, Object>> mapStream = functions.keySet().stream()
                                    .map(function ->
                                            functions.get(function).stream()
                                            .collect(Collectors.toMap(
                                                  name -> name,
                                                  name -> (Object) calculateFunction.apply(function, name)
                                            ))
                                    );

        Map<String, Object> map = mapStream.flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new Row(map);
    }
}

package com.codecool.service;

import com.codecool.converter.Converter;
import com.codecool.model.Row;
import com.codecool.model.Table;
import com.codecool.model.query.UpdateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UpdateService extends QueryService {
    private Converter converter;
    private UpdateQuery updateQuery;

    @Autowired
    public UpdateService(Converter converter, UpdateQuery updateQuery) {
        this.converter = converter;
        this.updateQuery = updateQuery;
    }

    public UpdateService() {
    }

    @Override
    public Table executeQuery(String query) {
        updateQuery.setQuery(query);

        Table table = converter.convert(updateQuery.getFileName());
        return update(table);
    }

    private Table update(Table table) {
        return new Table(table.getColumnNames(), update(table.getRows()));
    }

    private List<Row> update(List<Row> rows) {
        List<Row> rowsToUpdate = rows.stream()
                .filter(updateQuery.getWhereCondition())
                .collect(Collectors.toList());
        return rows.stream()
                .map(row -> rowsToUpdate.contains(row) ? update(row) : row)
                .collect(Collectors.toList());
    }

    private Row update(Row row) {
        Map<String, String> conditionMap = updateQuery.getSetCondition().stream()
                .collect(Collectors.toMap(
                        l -> l.get(0), l -> l.get(1).replace("'", "")
                ));
        Map<String, Object> dataAfterUpdate = Stream.of(conditionMap, row.getData())
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (x, y) -> x
                        )
                )
                ;
        return new Row(dataAfterUpdate);
    }
}

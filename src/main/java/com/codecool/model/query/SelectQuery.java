package com.codecool.model.query;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectQuery {

    private String query;

    private List<String> fileName;
    private Predicate<Row> whereCondition;
    private List<String> columnNames;
    private Map<SQLAggregateFunctions, List<String>> functions;
    private List<Predicate<Row>> joinConditions;
    private String groupByColumn;


    public SelectQuery(String query) {
        super();
        this.query = query;
        functions = getFunctions(query);
        columnNames = getColumnNames(query);


    }

    private List<String> getFilename(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("from");
        if (indexFrom < 0) {
            throw new WrongQueryFormatException("Missing FROM statement");
        } else if (indexFrom >= words.size() - 1) {
            throw new WrongQueryFormatException("Missing filename");
        }

        String fileName = words.get(indexFrom + 1);


        return Stream.concat(Arrays.asList(fileName).stream(), getJoinFileNames(query).stream())
                     .collect(Collectors.toList());
    }

    private List<String> getJoinFileNames(String query) {
        List<String> words = Arrays.asList(query.split(" "));
        int indexFrom = words.indexOf("join");


        if (indexFrom > -1 && indexFrom < words.size() - 1) {
            String fileName = words.get(indexFrom + 1);
            return Stream.concat(Arrays.asList(fileName).stream(), getJoinFileNames(query.substring(indexFrom+1)).stream())
                    .collect(Collectors.toList());

        } else {
            return new ArrayList<>();
        }
    }

    private Map<SQLAggregateFunctions, List<String>> getFunctions(String query) {
        List<String> columns = getListColumns(query);

        return Arrays.stream(SQLAggregateFunctions.values())
                     .collect(Collectors.toMap
                            (f -> f, f -> columns.stream()
                                                 .filter(c -> c.contains(f.getName()))
                                                 .collect(Collectors.toList()))
                            );
    }

    private List<String> getColumnNames(String query) {
        List<String> columns = getListColumns(query);


        return columns.stream()
                      .filter(c -> Arrays.stream(SQLAggregateFunctions.values())
                                   .noneMatch(f -> c.contains(f.getName())))
                      .collect(Collectors.toList());

    }

    private List<String> getListColumns(String query) {
        return Arrays.stream(query.split(" "))
                .filter(word -> query.indexOf(word) > query.indexOf("select"))
                .filter(word -> query.indexOf(word) < query.indexOf("from"))
                .map(word -> word.replace(",", ""))
                .collect(Collectors.toList());
    }








}

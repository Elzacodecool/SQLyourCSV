package com.codecool.service;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SelectServiceTest {

    @Autowired
    SelectService service;

    @Test
    public void testExecuteQuery_withEquals() {
        String query = "select id, first_name, age from table.csv where age=20;";

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", 20);
        Row row1 = new Row(map1);

        List<String> columnNames = Arrays.asList("id", "first_name", "age");
        List<Row> rows = Collections.singletonList(row1);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_withGreater() {
        String query = "select id, first_name, age from table.csv where age > 30;";

        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", 3);
        map3.put("first_name", "marian");
        map3.put("age", 90);
        Row row3 = new Row(map3);

        List<String> columnNames = Arrays.asList("id", "first_name", "age");
        List<Row> rows = Collections.singletonList(row3);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_withSmaller() {
        String query = "select * from table.csv where age < 30;";

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", 20);
        Row row1 = new Row(map1);

        List<String> columnNames = Arrays.asList("id", "first_name", "age");
        List<Row> rows = Collections.singletonList(row1);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());
    }

        @Test
    public void testExecuteQuery_withNotEquals() {
        String query = "select * from table.csv where age <> 20;";

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 2);
        map2.put("first_name", "tomek");
        map2.put("age", 30);
        Row row2 = new Row(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", 3);
        map3.put("first_name", "marian");
        map3.put("age", 90);
        Row row3 = new Row(map3);

        List<String> columnNames = Arrays.asList("id", "first_name", "age");
        List<Row> rows = Arrays.asList(row2, row3);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_withLike() {
        String query = "select * from table.csv where first_name like 'ala';";

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", 20);
        Row row1 = new Row(map1);


        List<String> columnNames = Arrays.asList("id", "first_name", "age");
        List<Row> rows = Collections.singletonList(row1);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_withFewConditions() {
        String query = "select id, first_name, age from table.csv where id = 1 or first_name like 'marian' and age > 20";


        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 3);
        map1.put("first_name", "marian");
        map1.put("age", 90);
        Row row1 = new Row(map1);


        List<String> columnNames = Arrays.asList("id", "first_name", "age");
        List<Row> rows = Collections.singletonList(row1);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_withFewConditions2() {
        String query = "select * from table.csv where id = 1 and first_name like 'marian' or age > 20";
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 2);
        map1.put("first_name", "tomek");
        map1.put("age", 30);
        Row row1 = new Row(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 3);
        map2.put("first_name", "marian");
        map2.put("age", 90);
        Row row2 = new Row(map2);

        List<String> columnNames = Arrays.asList("id", "first_name", "age");
        List<Row> rows = Arrays.asList(row1, row2);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());

    }

    @Test
    public void testExecuteQuery_JoinTables() {
        String query = "select * from table.csv join jointable.csv on id = id_surname";


        Map<String, Object> map1 = new HashMap<>();
        map1.put("id_surname", 1);
        map1.put("surname", "surname1");
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", "20");
        Row row1 = new Row(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id_surname", 2);
        map2.put("surname", "surname2");
        map2.put("id", 2);
        map2.put("first_name", "tomek");
        map2.put("age", "30");
        Row row2 = new Row(map2);


        Map<String, Object> map3 = new HashMap<>();
        map3.put("id_surname", 3);
        map3.put("surname", "surname3");
        map3.put("id", 3);
        map3.put("first_name", "marian");
        map3.put("age", "90");
        Row row3 = new Row(map3);

        List<String> columnNames = Arrays.asList("id_surname", "surname", "id", "first_name", "age");
        List<Row> rows = Arrays.asList(row1, row2, row3);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());


    }

    @Test
    public void testExecuteQuery_withoutJoin() {
        String query = "select * from table.csv";

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", 20);
        Row row1 = new Row(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 2);
        map2.put("first_name", "tomek");
        map2.put("age", 30);
        Row row2 = new Row(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", 3);
        map3.put("first_name", "marian");
        map3.put("age", 90);
        Row row3 = new Row(map3);

        List<String> columnNames = Arrays.asList("id", "first_name", "age");
        List<Row> rows = Arrays.asList(row1, row2, row3);
        Table expectedTable = new Table(columnNames, rows);

        assertEquals(expectedTable.toString(), service.executeQuery(query).toString());
    }

    @Test
    public void test() {
        String query = "select sum(age), avg(age), min(age), max(age), sum(id), min(id), max(id) from table.csv where age > 20";
        String query2 = "select * from table.csv";
        String query3 = "select sum(age) from table.csv where age > 0";

        System.out.println(service.executeQuery(query3));
    }


}


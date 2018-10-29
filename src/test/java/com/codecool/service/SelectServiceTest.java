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
    SelectService service =  new SelectService();

    @Test
    public void testGetFilenameByQuery() {
        String query = "select * from abc.csv";
        assertEquals("abc.csv", service.getFilename(query));
    }
    @Test
    public void testGetFilenameByQuery_withoutFrom() {
        String query = "select * abc.csv";
        assertThrows(WrongQueryFormatException.class, () -> service.getFilename(query));
    }
    @Test
    public void testGetFilenameByQuery_withoutFilename() {
        String query = "select * from";
        assertThrows(WrongQueryFormatException.class, () -> service.getFilename(query));
    }

    @Test
    public void testupdateRowWithColumns() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("first_name", "ala");
        map.put("age", 20);
        Row row = new Row(map);

        List<String> columns = Arrays.asList("id", "first_name");


        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        Row expectedRow = new Row(map1);

        assertEquals(expectedRow.toString(), service.getUpdatedRowWithColumns(row, columns).toString());
    }

    @Test
    public void testValidatedListColumnsFromQuery() {
        String query = "select id, name, age from abc.csv";
        List<String> columns = Arrays.asList("id", "name", "surname", "age");
        Table table = new Table(columns, new ArrayList<>());

        List<String> expectedColumns = Arrays.asList("id", "name", "age");

        assertEquals(expectedColumns, service.getValidatedListColumns(query, table));
    }

    @Test
    public void testValidatedListColumnsFromQuery_allColumns() {
        String query = "select * from abc.csv";
        List<String> columns = Arrays.asList("id", "name", "surname", "age");
        Table table = new Table(columns, new ArrayList<>());

        List<String> expectedColumns = Arrays.asList("id", "name", "surname", "age");

        assertEquals(expectedColumns, service.getValidatedListColumns(query, table));
    }

    @Test
    public void testValidatedListColumnsFromQuery_wrongColumnName() {
        String query = "select login from abc.csv";
        List<String> columns = Arrays.asList("id", "name", "surname", "age");
        Table table = new Table(columns, new ArrayList<>());

        assertThrows(WrongQueryFormatException.class, () -> service.getValidatedListColumns(query, table));
    }

    @Test
    public void testExecuteQuery_withoutCondition() {
        String query = "select * from table.csv;";

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
    public void testExecuteQuery_withEquals() {
        String query = "select * from table.csv where age=20;";

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
        String query = "select * from table.csv where age > 30;";

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
}
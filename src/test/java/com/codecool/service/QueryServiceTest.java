package com.codecool.service;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceConfig.class)
class SelectServiceTest {
    @Autowired
    private SelectService service;

    @Test
    void testGetFilenameByQuery() {
        String query = "select * from abc.csv";
        assertEquals("abc.csv", service.getFilename(query));
    }
    @Test
    void testGetFilenameByQuery_withoutFrom() {
        String query = "select * abc.csv";
        assertThrows(WrongQueryFormatException.class, () -> service.getFilename(query));
    }
    @Test
    void testGetFilenameByQuery_withoutFilename() {
        String query = "select * from";
        assertThrows(WrongQueryFormatException.class, () -> service.getFilename(query));
    }

    @Test
    void testupdateRowWithColumns() {
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
    void testValidatedListColumnsFromQuery() {
        String query = "select id, name, age from abc.csv";
        List<String> columns = Arrays.asList("id", "name", "surname", "age");
        Table table = new Table(columns, new ArrayList<>());

        List<String> expectedColumns = Arrays.asList("id", "name", "age");

        assertEquals(expectedColumns, service.getValidatedListColumns(query, table));
    }

    @Test
    void testValidatedListColumnsFromQuery_allColumns() {
        String query = "select * from abc.csv";
        List<String> columns = Arrays.asList("id", "name", "surname", "age");
        Table table = new Table(columns, new ArrayList<>());

        List<String> expectedColumns = Arrays.asList("id", "name", "surname", "age");

        assertEquals(expectedColumns, service.getValidatedListColumns(query, table));
    }

    @Test
    void testValidatedListColumnsFromQuery_wrongColumnName() {
        String query = "select login from abc.csv";
        List<String> columns = Arrays.asList("id", "name", "surname", "age");
        Table table = new Table(columns, new ArrayList<>());

        assertThrows(WrongQueryFormatException.class, () -> service.getValidatedListColumns(query, table));
    }
}
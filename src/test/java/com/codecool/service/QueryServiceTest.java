package com.codecool.service;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceConfig.class)
class QueryServiceTest {
    @Autowired
    private QueryService queryService;

    @Test
    void testGetFilenameByQuery() {
        String query = "select * from abc.csv";
        assertEquals("abc.csv", queryService.getFileName(query));
    }
    @Test
    void testGetFilenameByQuery_withoutFrom() {
        String query = "select * abc.csv";
        assertThrows(WrongQueryFormatException.class, () -> queryService.getFileName(query));
    }
    @Test
    void testGetFilenameByQuery_withoutFilename() {
        String query = "select * from";
        assertThrows(WrongQueryFormatException.class, () -> queryService.getFileName(query));
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

        assertEquals(expectedRow.toString(), queryService.getUpdatedRowWithColumns(row, columns).toString());
    }
}
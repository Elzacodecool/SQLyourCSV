package com.codecool;

import com.codecool.converter.Converter;
import com.codecool.exception.WrongDataStructureException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConverterTest {

    @Autowired
    private Converter converter;

    @Test
    public void testConvertCsvToTable() throws IOException, GeneralSecurityException {
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

        Table resultTable = converter.convert("table.csv");

        assertEquals(expectedTable.getColumnNames(), resultTable.getColumnNames());
        assertEquals(expectedTable.toString(), resultTable.toString());
    }

    @Test
    public void testConverter_withEmptyFile() {
        assertThrows(WrongDataStructureException.class, () -> converter.convert("empty_file.csv"));
    }

    @Test
    public void testConverter_withDifferentColumnsSize() {
        assertThrows(WrongDataStructureException.class, () -> converter.convert("wrong_structure.csv"));
    }
}
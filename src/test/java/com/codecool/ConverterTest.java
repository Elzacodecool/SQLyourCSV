package com.codecool;

import com.codecool.converter.Converter;
import com.codecool.converter.FileReader;
import com.codecool.exception.WrongDataStructureException;
import com.codecool.model.Row;
import com.codecool.model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {
    private Converter converter;

    @BeforeEach
    void setUp() {
        converter = new Converter(new FileReader());
    }

    @Test
    void testConvertCcvToTable() {
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

        Table resultTable = converter.convert("src/test/resources/table.csv");

        assertEquals(expectedTable.getColumnNames(), resultTable.getColumnNames());
        assertEquals(expectedTable.toString(), resultTable.toString());
    }

    @Test
    void testConverter_withEmptyFile() {
        assertThrows(WrongDataStructureException.class, () -> converter.convert("src/test/resources/empty_file.csv"));
    }

    @Test
    void testConverter_withDifferentColumnsSize() {
        assertThrows(WrongDataStructureException.class, () -> converter.convert("src/test/resources/wrong_structure.csv"));
    }
}
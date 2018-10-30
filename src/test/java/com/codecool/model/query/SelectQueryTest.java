package com.codecool.model.query;

import com.codecool.exception.WrongQueryFormatException;
import com.codecool.model.Row;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SelectQueryTest {

    private SelectQuery selectQuery;

    @Test
    public void testFileName() {
        String query = "select * from table";
        selectQuery = new SelectQuery(query);

        assertEquals("table", selectQuery.getFileNames().get(0));
    }
    @Test
    public void testFilenameByQuery_withoutFromStatament() {
        String query = "select * table";
        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }
    @Test
    public void testFilename_withoutFileName() {
        String query = "select * from";
        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }

    @Test
    public void testListColumns() {
        String query = "select max(id), id, name, min(count), age from table";
        selectQuery = new SelectQuery(query);

        List<String> expectedColumns = Arrays.asList("id", "name", "age");

        assertEquals(expectedColumns, selectQuery.getColumnNames());
    }

   @Test
    public void testFunctionsMap() {
        String query = "select max(id), id, name, min(count), min(age), age from table";
        selectQuery = new SelectQuery(query);

        assertEquals(Collections.singletonList("id"), selectQuery.getFunctions().get(SQLAggregateFunctions.MAX));
        assertEquals(Arrays.asList("count", "age"), selectQuery.getFunctions().get(SQLAggregateFunctions.MIN));
    }

    @Test
    public void testWhereConditionPredicate() {
        String query = "select * from table where id = 1";
        selectQuery = new SelectQuery(query);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", 20);
        Row row1 = new Row(map1);

        List<Row> resultRows = getExampleRows().stream().filter(
                selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        List<Row> expectedRows = Collections.singletonList(row1);

        assertEquals(expectedRows.size(), resultRows.size());
        assertEquals(expectedRows.toString(), resultRows.get(0).toString());
    }

    private List<Row> getExampleRows() {
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

        return Arrays.asList(row1, row2, row3);
    }
}
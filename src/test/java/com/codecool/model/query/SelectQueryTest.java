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
    public void testFilenameByQuery_withoutFromStatement() {
        String query = "select * table";
        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }
    @Test
    public void testFilename_withoutFileName() {
        String query = "select * from";
        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }
    @Test
    public void testFileNames_withJoins() {
        String query = "select * from table join table2 on id=id2 join table3 on id=id2";
        selectQuery = new SelectQuery(query);

        assertEquals(Arrays.asList("table", "table2", "table3"), selectQuery.getFileNames());
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
    public void testWhereConditionPredicate_equalsOperator() {
        String query = "select * from table where id = 1";
        selectQuery = new SelectQuery(query);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", 20);
        Row row1 = new Row(map1);

        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        List<Row> expectedRows = Collections.singletonList(row1);

        assertEquals(expectedRows.toString(), resultRows.toString());
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

    @Test
    public void testWhereConditionPredicate_withoutCondition() {
        String query = "select * from table.csv;";
        selectQuery = new SelectQuery(query);

        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        assertEquals(getExampleRows().toString(), resultRows.toString());
    }

    @Test
    public void testWhereConditionPredicate_withGreaterOperator() {
        String query = "select * from table.csv where age > 30;";
        selectQuery = new SelectQuery(query);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", 3);
        map3.put("first_name", "marian");
        map3.put("age", 90);
        Row row3 = new Row(map3);

        List<Row> expectedRows = Collections.singletonList(row3);
        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        assertEquals(expectedRows.toString(), resultRows.toString());
    }

    @Test
    public void testWhereConditionStatement_withSmallerOperator() {
        String query = "select * from table.csv where age < 30;";
        selectQuery = new SelectQuery(query);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", 20);
        Row row1 = new Row(map1);


        List<Row> expectedRows = Collections.singletonList(row1);
        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        assertEquals(expectedRows.toString(), resultRows.toString());
    }

    @Test
    public void testWhereConditionPredicate_notEqualsOperator() {
        String query = "select * from table.csv where age <> 20;";
        selectQuery = new SelectQuery(query);

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

        List<Row> expectedRows = Arrays.asList(row2, row3);
        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        assertEquals(expectedRows.toString(), resultRows.toString());
    }

    @Test
    public void testWhereConditionPredicate_withLikeOperator() {
        String query = "select * from table.csv where first_name like 'ala';";
        selectQuery = new SelectQuery(query);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("first_name", "ala");
        map1.put("age", 20);
        Row row1 = new Row(map1);

        List<Row> expectedRows = Collections.singletonList(row1);
        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        assertEquals(expectedRows.toString(), resultRows.toString());
    }

    @Test
    public void testWhereConditionPredicate_wrongOperator() {
        String query = "select * from table.csv where age ! 20;";
        selectQuery = new SelectQuery(query);

        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        assertTrue(resultRows.isEmpty());
    }

    @Test
    public void testWhereConditionPredicate_withFewConditions() {
        String query = "select * from table.csv where id = 1 or first_name like 'marian' and age > 20";
        selectQuery = new SelectQuery(query);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 3);
        map1.put("first_name", "marian");
        map1.put("age", 90);
        Row row1 = new Row(map1);

        List<Row> expectedRows = Collections.singletonList(row1);
        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        assertEquals(expectedRows.toString(), resultRows.toString());
    }

    @Test
    public void testWhereConditionPredicate_withFewConditions2() {
        String query = "select * from table.csv where id = 1 and first_name like 'marian' or age > 20";
        selectQuery = new SelectQuery(query);

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

        List<Row> expectedRows = Arrays.asList(row1, row2);
        List<Row> resultRows = getExampleRows().stream()
                .filter(selectQuery.getWhereCondition())
                .collect(Collectors.toList());
        assertEquals(expectedRows.toString(), resultRows.toString());
    }

    @Test
    public void testJoinConditions() {
        String query = "select * from table join table2 on id=id2 join table3 on id=id3";
        selectQuery = new SelectQuery(query);

        List<List<String>> expectedConditions = Arrays.asList(Arrays.asList("id", "id2"), Arrays.asList("id", "id3"));
        assertEquals(expectedConditions, selectQuery.getJoinConditions());
    }
    @Test
    public void testJoinConditions_null() {
        String query = "select * from table";
        selectQuery = new SelectQuery(query);

        assertNull(selectQuery.getJoinConditions());
    }

    @Test
    public void testJoinCondition_wrongFormat() {
        String query = "select * from table join table2 on id != id2;";

        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }


    @Test
    public void testGroupBy() {
        String query = "select * from table group by name;";
        selectQuery = new SelectQuery(query);

        assertEquals("name", selectQuery.getGroupByColumn());
    }
    @Test
    public void testGroupBy_null() {
        String query = "select * from table;";
        selectQuery = new SelectQuery(query);

        assertNull(selectQuery.getGroupByColumn());
    }
    @Test
    public void testGroupBy_missingColumnName() {
        String query = "select * from table group by;";

        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }

    @Test
    public void testGetAllColumns() {
        String query = "select max(id), id, name, min(count), age from table";
        selectQuery = new SelectQuery(query);

        Set<String> expectedColumns = new HashSet<>(Arrays.asList("id", "name",  "age", "count"));

        assertEquals(expectedColumns, selectQuery.getAllColumns());
    }
}
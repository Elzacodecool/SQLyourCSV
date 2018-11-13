package com.codecool.interpreter;

import com.codecool.model.Row;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateQueryInterpreterTest {
    @Autowired
    private UpdateQueryInterpreter interpreter;

    @Test
    public void testFileName() {
        String query = "update table set column=column_name, column2 = another_column_name";

        assertEquals("table", interpreter.getFilename(query));
    }

    @Test
    public void testSetCondition() {
        String query = "update table set column=column_name, column2 = another_column_name";

        List<List<String>> expectedConditions = Arrays.asList(
                Arrays.asList("column", "column_name"),
                Arrays.asList("column2", "another_column_name")
        );
        assertEquals(expectedConditions, interpreter.getSetCondition(query));
    }

    @Test
    public void testWhereConditionPredicate() {
        String query = "update table set column=column_name where id = 1 or first_name like 'marian' and age > 20";

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 3);
        map1.put("first_name", "marian");
        map1.put("age", 90);
        Row row1 = new Row(map1);

        List<Row> expectedRows = Collections.singletonList(row1);
        List<Row> resultRows = SelectQueryInterpreterTest.getExampleRows().stream()
                .filter(interpreter.getWherePredicate(query))
                .collect(Collectors.toList());
        assertEquals(expectedRows.toString(), resultRows.toString());
    }
}

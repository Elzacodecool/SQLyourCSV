package com.codecool.interpreter;

import com.codecool.model.Row;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteQueryInterpreterTest {
    @Autowired
    private DeleteQueryInterpreter interpreter;

    @Test
    public void testFileName() {
        String query = "delete from table";

        assertEquals("table", interpreter.getFilename(query));
    }

    @Test
    public void testWhereConditionPredicate() {
        String query = "delete from table where id = 1 or first_name like 'marian' and age > 20";

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

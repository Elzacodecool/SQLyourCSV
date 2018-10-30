package com.codecool.model.query;

import com.codecool.exception.WrongQueryFormatException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SelectQueryTest {

    private SelectQuery selectQuery;

    @Test
    public void testFileName() {
        String query = "select * from abc.csv";
        selectQuery = new SelectQuery(query);

        assertEquals("abc.csv", selectQuery.getFileNames().get(0));
    }
    @Test
    public void testFilenameByQuery_withoutFromStatament() {
        String query = "select * abc.csv";
        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }
    @Test
    public void testFilename_withoutFileName() {
        String query = "select * from";
        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }

    @Test
    public void testListColumns() {
        String query = "select max(id), id, name, min(count), age from abc.csv";
        selectQuery = new SelectQuery(query);

        List<String> expectedColumns = Arrays.asList("id", "name", "age");

        assertEquals(expectedColumns, selectQuery.getColumnNames());
    }

   @Test
    public void testFunctionsMap() {
        String query = "select max(id), id, name, min(count), min(age), age from abc.csv";
        selectQuery = new SelectQuery(query);

        assertEquals(Collections.singletonList("id"), selectQuery.getFunctions().get(SQLAggregateFunctions.MAX));
        assertEquals(Arrays.asList("count", "age"), selectQuery.getFunctions().get(SQLAggregateFunctions.MIN));
    }

}
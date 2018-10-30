package com.codecool.model.query;

import com.codecool.exception.WrongQueryFormatException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SelectQueryTest {

    private SelectQuery selectQuery;

    @Test
    public void testGetFileName() {
        String query = "select * from abc.csv";
        selectQuery = new SelectQuery(query);

        assertEquals("abc.csv", selectQuery.getFileNames().get(0));
    }
    @Test
    public void testGetFilenameByQuery_withoutFrom() {
        String query = "select * abc.csv";
        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }
    @Test
    public void testGetFilenameByQuery_withoutFilename() {
        String query = "select * from";
        assertThrows(WrongQueryFormatException.class, () -> new SelectQuery(query));
    }

}
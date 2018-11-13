package com.codecool.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateServiceTest {
    @Autowired
    private UpdateService service;

    @Test
    public void testExecuteQuery() {
        String query = "update table.csv set age=20";

        String expected = "        id | first_name |        age\n" +
                          "         1 |        ala |         20\n" +
                          "         2 |      tomek |         20\n" +
                          "         3 |     marian |         20";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_withWhereCondition() {
        String query = "update table.csv set age=20 where age < 50";

        String expected = "        id | first_name |        age\n" +
                          "         1 |        ala |         20\n" +
                          "         2 |      tomek |         20\n" +
                          "         3 |     marian |         90";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_complex() {
        String query = "update table.csv set age=20, first_name='asia' where age < 50 and age > 25";

        String expected = "        id | first_name |        age\n" +
                          "         1 |        ala |         20\n" +
                          "         2 |       asia |         20\n" +
                          "         3 |     marian |         90";

        assertEquals(expected, service.executeQuery(query).toString());
    }
}

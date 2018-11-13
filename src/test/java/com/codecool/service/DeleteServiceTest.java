package com.codecool.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteServiceTest {
    @Autowired
    private DeleteService service;

    @Test
    public void testExecuteQuery() {
        String query = "delete from table.csv";

        String expected = "        id | first_name |        age\n";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_withWhereCondition() {
        String query = "delete from table.csv where id > 2";

        String expected = "        id | first_name |        age\n" +
                          "         1 |        ala |         20\n" +
                          "         2 |      tomek |         30";

        assertEquals(expected, service.executeQuery(query).toString());
    }

}

package com.codecool.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SelectServiceTest {

    @Autowired
    SelectService service;

    @Test
    public void testExecuteQuery_allColumns() {
        String query = "select * from table.csv";

        String expected = "        id | first_name |        age\n" +
                          "         1 |        ala |         20\n" +
                          "         2 |      tomek |         30\n" +
                          "         3 |     marian |         90";

        assertEquals(expected, service.executeQuery(query).toString());
    }

   @Test
    public void testExecuteQuery_join() {
        String query = "select * from table.csv " +
                                 "join jointable.csv on id=id_surname " +
                                 "join jointable2.csv on surname = surname2";

        String expected = "profession | id_surname |   surname2 |    surname |         id | first_name |        age\n" +
                          "      cook |          1 |   surname1 |   surname1 |          1 |        ala |         20\n" +
                          "    artist |          2 |   surname2 |   surname2 |          2 |      tomek |         30";

        assertEquals(expected, service.executeQuery(query).toString());
    }



}


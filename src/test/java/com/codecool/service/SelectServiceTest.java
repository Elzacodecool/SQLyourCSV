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





}


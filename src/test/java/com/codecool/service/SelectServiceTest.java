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
    private SelectService service;


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
    public void testExecuteQuery_columnNames() {
        String query = "select id, first_name from table.csv";

        String expected = "        id | first_name\n" +
                          "         1 |        ala\n" +
                          "         2 |      tomek\n" +
                          "         3 |     marian";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_functions() {
        String query = "select sum(age) from table.csv;";

        String expected = "  sum(age)\n" +
                          "     140.0";

        assertEquals(expected, service.executeQuery(query).toString());
    }

   @Test
    public void testExecuteQuery_join() {
        String query = "select * from table.csv " +
                                 "join jointable.csv on id=id_surname " +
                                 "join jointable2.csv on surname = surname2";

        String expected = "id_surname | profession |   surname2 |    surname |         id | first_name |        age\n" +
                          "         1 |       cook |   surname1 |   surname1 |          1 |        ala |         20\n" +
                          "         2 |     artist |   surname2 |   surname2 |          2 |      tomek |         30";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_join_withoutResults() {
        String query = "select * from table.csv " +
                                 "join jointable.csv on id=surname";

        String expected = "        id | first_name |        age | id_surname |    surname\n";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_where() {
        String query = "select * from table.csv where age < 50";

        String expected = "        id | first_name |        age\n" +
                          "         1 |        ala |         20\n" +
                          "         2 |      tomek |         30";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_groupBy() {
        String query = "select gender, avg(age) from groupbytable.csv group by gender;";
        String expected = "    gender |   avg(age)\n" +
                          "         f |       30.0\n" +
                          "         m |       38.0";

        assertEquals(expected, service.executeQuery(query).toString());
    }
    @Test
    public void testExecuteQuery_groupBy_withHaving() {
        String query = "select gender, avg(age) from groupbytable.csv group by gender having avg(age) < 35";
        String expected = "    gender |   avg(age)\n" +
                          "         f |       30.0";

        assertEquals(expected, service.executeQuery(query).toString());
    }


    @Test
    public void testExecuteQuery_groupBy_emptyTable() {
        String query = "select gender, avg(age) from groupbytable.csv where gender=x group by gender;";
        String expected = "    gender |   avg(age)\n";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_groupBy_onlyFunctions() {
        String query = "select avg(age) from groupbytable.csv group by gender;";
        String expected = "  avg(age)\n" +
                          "      30.0\n" +
                          "      38.0";

        assertEquals(expected, service.executeQuery(query).toString());
    }

    @Test
    public void testExecuteQuery_groupBy_onlyFunctions_emptyTable() {
        String query = "select avg(age) from groupbytable.csv where gender=x group by gender;";
        String expected = "  avg(age)\n";

        assertEquals(expected, service.executeQuery(query).toString());
    }
}


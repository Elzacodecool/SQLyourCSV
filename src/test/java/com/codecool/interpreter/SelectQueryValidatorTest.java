package com.codecool.interpreter;

import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SelectQueryValidatorTest {
    @Autowired
    private SelectQueryValidator validator;

    private String[] correctQueries = {"select * from table",
                                       "select a,b,c,d from table",
                                       "select a,b,c from table join table2 on id=inner_id",
                                       "select * from table join table2 on a=b"};

    private String[] incorrectQueries = {"dkajdsa",
                                         "select from tabela",
                                         "select abc from table join table2",
                                         "select cc table from join table2",
                                         "select * table join on from table2",
                                         "select abc from table from table2 join table3",
                                         "select * from table abc join table2 on id = id2",
                                         "select * from table join table2 on id = id2 and id = id",
                                         "select sum(abc), a, v from table join table2 on id = id2 and id = id"};

    @Test
    public void testCorrectQueries() {
        Arrays.stream(correctQueries)
                .map(correctQuery -> validator.validateQuery(correctQuery))
                .forEach(Assertions::assertTrue);
    }

    @Test
    public void testIncorrectQueries() {
        Arrays.stream(incorrectQueries)
                .map(incorrectQuery -> validator.validateQuery(incorrectQuery))
                .forEach(Assertions::assertFalse);
    }
}
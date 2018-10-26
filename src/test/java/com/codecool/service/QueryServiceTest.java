package com.codecool.service;

import com.codecool.exception.WrongQueryFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceConfig.class)
class QueryServiceTest {
    @Autowired
    private QueryService queryService;

    @Test
    void testGetFilenameByQuery() {
        String query = "select * from abc.csv";
        assertEquals("abc.csv", queryService.getFileName(query));
    }

    @Test
    void testGetFilenameByQuery_withoutFrom() {
        String query = "select * abc.csv";
        assertThrows(WrongQueryFormatException.class, () -> queryService.getFileName(query));
    }

    @Test
    void testGetFilenameByQuery_withoutFilename() {
        String query = "select * from";
        assertThrows(WrongQueryFormatException.class, () -> queryService.getFileName(query));
    }

}
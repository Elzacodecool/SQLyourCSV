package com.codecool.converter;

import com.codecool.googleSheets.SheetsServiceUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileReader {
    private static Sheets sheetsService;

    @Autowired
    public FileReader() {
        try {
            sheetsService = SheetsServiceUtil.getSheetsService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }


    List<String[]> readData(String file) {
       List<String[]> data;

        ValueRange body = null;
        try {
            body = sheetsService.spreadsheets().values()
                    .get(file, "A1:Z10000")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = body.getValues().stream().map(list -> list.stream().map(element -> (String)element).toArray(String[]::new)).collect(Collectors.toList());
        return data;
    }
}

package com.codecool.converter;

import com.codecool.googleSheets.GoogleAuthorizeUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileReader {

    public static List<String[]> readData(String file) throws IOException, GeneralSecurityException{
        System.out.println("-----------------filename:" + file);
        final String spreadsheetId = file;
        final String range = "A1:Z10000";
        Sheets service = GoogleAuthorizeUtil.getSheetsService();

        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        return response.getValues().stream().map(list -> list.stream().map(element -> (String)element).toArray(String[]::new)).collect(Collectors.toList());
    }
}



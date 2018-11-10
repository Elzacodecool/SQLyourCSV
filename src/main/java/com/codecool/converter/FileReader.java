package com.codecool.converter;

import com.codecool.googleSheets.GoogleAuthorizeUtil;
import com.codecool.model.Table;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileReader {

    public static List<String[]> readData(String file) throws IOException {
        final String spreadsheetId = file;
        final String range = "A1:Z10000";
        Sheets service = GoogleAuthorizeUtil.getSheetsService();

        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        return response.getValues().stream().map(list -> list.stream().map(element -> (String)element).toArray(String[]::new)).collect(Collectors.toList());
    }

    public static void writeData(String file, Table table) throws IOException {
        List<List<Object>> values = new ArrayList();
        final String spreadsheetId = file;
        final String range = "A1:Z10000";
        final  String valueInputOption = "RAW";

        Sheets service = GoogleAuthorizeUtil.getSheetsService();

        values.add(table.getColumnNames().stream().map(Object.class::cast).collect(Collectors.toList()));
        values.add(table.getRows().stream().map(n->n.getValuesFromRow().stream().map(Object.class::cast).collect(Collectors.toList())).collect(Collectors.toList()));

        ValueRange body = new ValueRange()
                .setValues(values);

        UpdateValuesResponse result =
                service.spreadsheets().values().update(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption)
                        .execute();
        System.out.printf("%d cells updated.", result.getUpdatedCells());
    }
}



package com.codecool.converter;

import com.codecool.googleSheets.GoogleAuthorizeUtil;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileReader {

    public List<String[]> readData(String file) throws IOException {
        final String spreadsheetId = convertNameToGoogleSheetId(file);
        final String range = "A1:Z10000";
        Sheets service = GoogleAuthorizeUtil.getSheetsService();

        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        return response.getValues().stream().map(list -> list.stream().map(element -> (String)element).toArray(String[]::new)).collect(Collectors.toList());
    }

    public void writeData(String file,List<List<Object>> values) throws IOException {

        final String spreadsheetId = convertNameToGoogleSheetId(file);
        final String range = "A1:Z1000";
        final  String valueInputOption = "RAW";

        Sheets service = GoogleAuthorizeUtil.getSheetsService();


        clearSpreadsheet(spreadsheetId, range);

        ValueRange body = new ValueRange()
                .setValues(values);


        UpdateValuesResponse result =
                service.spreadsheets().values().update(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption)
                        .execute();

        System.out.printf("%d cells updated.", result.getUpdatedCells());
    }

    private String convertNameToGoogleSheetId(String fileName) throws IOException {
        Drive drive = GoogleAuthorizeUtil.getDriveService();
        List<File> result = new ArrayList<>();
        Drive.Files.List request = drive.files().list();

        do {
            try {
                FileList files = request.execute();
                result.addAll(files.getFiles());
                request.setPageToken(files.getNextPageToken());
                for (File file : result) {
                    if(file.getName().equals(fileName)) {
                        return file.getId();
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);

        return null;
    }

    private void clearSpreadsheet(String spreadsheetId, String range) {
        Sheets service;
        ClearValuesRequest requestBody = new ClearValuesRequest();

        try {
            service = GoogleAuthorizeUtil.getSheetsService();
            service.spreadsheets().values().clear(spreadsheetId,range,requestBody).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



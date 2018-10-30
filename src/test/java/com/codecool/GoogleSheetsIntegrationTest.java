package com.codecool;

import com.codecool.googleSheets.SheetsServiceUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import java.util.Arrays;
import java.util.List;

public class GoogleSheetsIntegrationTest {
    private static Sheets sheetsService;
    private static String SPREADSHEET_ID = "10vLQR-v2XdrsrJQPVMwmdeUYO35-r7aQOiTy6xz9zk8";

    @BeforeClass
    public static void setup() throws GeneralSecurityException, IOException {
        sheetsService = SheetsServiceUtil.getSheetsService();
    }

    @Test
    public void whenWriteSheet_thenReadSheetOk() throws IOException {
        ValueRange body = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, "A1:Z10000")
                .execute();
        List<List<Object>> values = body.getValues();
    }
}

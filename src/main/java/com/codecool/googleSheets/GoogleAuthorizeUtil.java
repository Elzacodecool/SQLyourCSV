package com.codecool.googleSheets;

import com.codecool.converter.FileReader;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleAuthorizeUtil {

    private static final String APPLICATION_NAME = "SQLyourCSV";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static GoogleClientSecrets clientSecrets;
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY, SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static GoogleAuthorizationCodeFlow flow;
    private static  NetHttpTransport httpTransport;

    @Autowired
    public GoogleAuthorizeUtil() throws IOException, GeneralSecurityException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = FileReader.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        this.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        this.flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .build();

        flow.getCredentialDataStore().delete("user");
    }

    public static Sheets getSheetsService() throws IOException {
        return new Sheets.Builder(httpTransport, JSON_FACTORY,flow.loadCredential("user"))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static  Drive getDriveService() throws IOException {
        return new Drive.Builder(httpTransport, JSON_FACTORY, flow.loadCredential("user"))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public GoogleAuthorizationCodeFlow getFlow() {
        return flow;
    }
}

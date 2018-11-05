package com.codecool.controlers;

import com.codecool.converter.FileReader;
import com.codecool.model.QueryInterpreter;
import com.codecool.service.SelectService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping()
public class QueryController {

    @Autowired
    SelectService service;

    @Autowired
    FileReader  fileReader;

    @GetMapping("/query")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayQuery(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralSecurityException {
        System.out.println("___________________Here");
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), fileReader.getClientSecrets(), scopes)
                .setDataStoreFactory(new MemoryDataStoreFactory())
                .setAccessType("offline").build();
        Credential credential = flow.loadCredential("user");
        if(credential == null) {
            String url = flow.newAuthorizationUrl().setState("xyz")
                    .setRedirectUri("http://localhost:8080/callback").build();
            response.sendRedirect(url);

         //  String code =  flow.newTokenRequest(response.getHeader("code"));

        }

        model.addAttribute("query", new QueryInterpreter());
        return "getQuery";

    }

    @GetMapping("/callback")
    public void getToken(HttpServletRequest request, HttpServletResponse response) {

    }

    @PostMapping("/query")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayResult(@ModelAttribute("query") QueryInterpreter query, Model model ) {
        try {
            model.addAttribute("queryTable", this.service.executeQuery(query.getQuery()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return "response";


    }

}

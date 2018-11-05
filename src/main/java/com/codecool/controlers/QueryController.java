package com.codecool.controlers;

import com.codecool.converter.FileReader;
import com.codecool.model.QueryInterpreter;
import com.codecool.service.SelectService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Controller
@RequestMapping()
public class QueryController {
    private GoogleAuthorizationCodeFlow flow;

    @Autowired
    SelectService service;


    @GetMapping("/query")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void displayQuery(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralSecurityException {
        this.flow = FileReader.getFlow();

        Credential credential = flow.loadCredential("user");
        if(credential == null) {
            String url = flow.newAuthorizationUrl().setState("xyz")
                    .setRedirectUri("http://localhost:8080/callback").build();
            response.sendRedirect(url);
        } else {
            System.out.println("----------------- new Credential in Query: " + credential);
            System.out.println("----------------------FlowAuth in /query:" + flow.getClientAuthentication());
            response.sendRedirect("http://localhost:8080/queryP");
        }
    }
    @GetMapping("/queryP")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayQuery(Model model) throws IOException, GeneralSecurityException {
            model.addAttribute("query", new QueryInterpreter());
        System.out.println("----------------------FlowAuth in /query:" + flow.getClientAuthentication());
            System.out.println("---------------Before return from display QueryP");
            return "getQuery";
    }

    @GetMapping("/callback")
    public void getToken(HttpServletRequest request, HttpServletResponse response) throws IOException, GeneralSecurityException {
        String code = request.getParameter("code");

        GoogleAuthorizationCodeTokenRequest query = flow.newTokenRequest(code)
                .setRedirectUri("http://localhost:8080/callback")
                .setGrantType("authorization_code")
                .setCode(code)
                .set("response_type", "code")
                .setClientAuthentication(flow.getClientAuthentication());
        TokenResponse tokenResponse = query.execute();
        System.out.println("-------------------my token: " + tokenResponse.getAccessToken());
        flow.createAndStoreCredential(tokenResponse,"user");
        Credential credential = flow.loadCredential("user");
        System.out.println("---------------------Credential in callback:    " + credential);
        response.sendRedirect("http://localhost:8080/queryP");
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

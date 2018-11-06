package com.codecool.controlers;

import com.codecool.googleSheets.GoogleAuthorizeUtil;
import com.codecool.model.query.SelectQuery;
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

@Controller
@RequestMapping()
public class QueryController {
    private GoogleAuthorizationCodeFlow flow;

    @Autowired
    SelectService service;


    @GetMapping("/query")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void displayQuery(HttpServletResponse response) throws IOException {
        this.flow = GoogleAuthorizeUtil.getFlow();

        Credential credential = flow.loadCredential("user");
        if(credential == null) {
            String url = flow.newAuthorizationUrl().setState("xyz")
                    .setRedirectUri("http://localhost:8080/callback").build();
            response.sendRedirect(url);
        } else {
            response.sendRedirect("http://localhost:8080/queryP");
        }
    }
    @GetMapping("/queryP")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayQuery(Model model) {
            model.addAttribute("query", new SelectQuery());
            return "getQuery";
    }

    @GetMapping("/callback")
    public void getToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");

        GoogleAuthorizationCodeTokenRequest query = flow.newTokenRequest(code)
                .setRedirectUri("http://localhost:8080/callback")
                .setGrantType("authorization_code")
                .setCode(code)
                .set("response_type", "code")
                .setClientAuthentication(flow.getClientAuthentication());
        TokenResponse tokenResponse = query.execute();
        flow.createAndStoreCredential(tokenResponse,"user");
        response.sendRedirect("http://localhost:8080/queryP");
    }

    @PostMapping("/query")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayResult(@ModelAttribute("query") SelectQuery query, Model model ) {
        model.addAttribute("queryTable", this.service.executeQuery(query.getQuery()));
        return "response";
    }

}

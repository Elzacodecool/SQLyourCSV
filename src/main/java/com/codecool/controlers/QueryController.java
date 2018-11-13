package com.codecool.controlers;

import com.codecool.googleSheets.GoogleAuthorizeUtil;
import com.codecool.service.QueryServiceAdapter;
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

    private final QueryServiceAdapter serviceAdapter;

    @Autowired
    public QueryController(QueryServiceAdapter service) {
        this.serviceAdapter = service;
    }


    @GetMapping("/query")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void displayQuery(HttpServletResponse response) throws IOException, GeneralSecurityException {
        this.flow = new GoogleAuthorizeUtil().getFlow();


        Credential credential = this.flow.loadCredential("user");
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
    public String displayQuery() {
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
    public String displayResult(@RequestParam(value = "query") String query, Model model ) {
        model.addAttribute("queryTable", this.serviceAdapter.executeQuery(query));
        return "response";
    }

}

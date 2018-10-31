package com.codecool.controlers;

import com.codecool.converter.Converter;
import com.codecool.model.QueryInterpreter;
import com.codecool.service.SelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Controller
@RequestMapping("/query")
public class QueryController {

    @Autowired
    SelectService service;

    @GetMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayQuery(Model model) {
        model.addAttribute("query", new QueryInterpreter());
       return "getQuery";
    }

    @PostMapping()
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

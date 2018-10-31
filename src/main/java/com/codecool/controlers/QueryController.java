package com.codecool.controlers;

import com.codecool.converter.Converter;
import com.codecool.model.QueryInterpreter;
import com.codecool.service.QueryServiceFactory;
import com.codecool.service.SelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/query")
public class QueryController {

    @Autowired
    private QueryServiceFactory queryServiceFactory;

    @GetMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayQuery(Model model) {
        model.addAttribute("query", new QueryInterpreter());
       return "getQuery";
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayResult(@ModelAttribute("query") QueryInterpreter query, Model model ) {
        String queryString = query.getQuery();
        model.addAttribute("queryTable", queryServiceFactory.getQueryService(queryString).executeQuery(queryString));
        return "response";
    }

}

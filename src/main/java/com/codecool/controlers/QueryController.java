package com.codecool.controlers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/query")
public class QueryController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayQuery(Model model) {
       return "getQuery";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String displayResult(Model model) {
        return "response";
    }

}

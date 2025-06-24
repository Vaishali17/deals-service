package com.eatclub.deals.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class DealController {

    @GetMapping("/deals")
    public String getDealsbyTimeOfDay(@RequestParam(required = false)String timeOfDay){
        return "Deals";
    }
}

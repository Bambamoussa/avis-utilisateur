package com.bamba.avis.controller;


import com.bamba.avis.model.Avis;
import com.bamba.avis.service.AvisService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("avis")
@RestController
public class AvisController {
    private AvisService avisService;

    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public  void create(@RequestBody Avis avis){
        this.avisService.create(avis);
    }
}

package com.bamba.avis.controller;


import com.bamba.avis.dto.AuthentificationDTO;
import com.bamba.avis.dto.UtilisateurDto;
import com.bamba.avis.model.Avis;
import com.bamba.avis.model.Utilisateur;
import com.bamba.avis.security.JwtService;
import com.bamba.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurController {

    private UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;

    private JwtService jwtService;


    @PostMapping(path = "inscription")
    public  void inscription(@RequestBody Utilisateur utilisateur){
     this.utilisateurService.inscription(utilisateur);
    }

    @PostMapping(path = "deconnexion")
    public  void deconnexion(){
        this.jwtService.deconnexion();
    }


    @PostMapping(path = "refreshtoken")
    public  @ResponseBody Map<String,String> refreshToken(@RequestBody Map<String,String> refreshTokenRequest){

      return  this.jwtService.refreshToken(refreshTokenRequest);
    }

    @PostMapping(path = "modifierMotPasse")
    public  void  modifierMotDePasse(@RequestBody Map<String,String> param){

         this.utilisateurService.modifierMotDePasse(param);
    }

    @PostMapping(path = "nouveauMotPasse")
    public  void  nouveauMotDePasse(@RequestBody UtilisateurDto utilisateurDto){

        this.utilisateurService.nouveauMotDePasse(utilisateurDto);
    }



    @PostMapping(path = "connexion")
    public Map<String, String> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
        return this.jwtService.generate(authentificationDTO.username());
    }
}

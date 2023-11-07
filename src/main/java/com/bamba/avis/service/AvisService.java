package com.bamba.avis.service;

import com.bamba.avis.model.Avis;
import com.bamba.avis.model.Utilisateur;
import com.bamba.avis.repository.AvisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AvisService {

    private AvisRepository avisRepository;

    @Autowired
    public AvisService(AvisRepository avisRepository) {
        this.avisRepository = avisRepository;
    }

    public  void create(Avis avis){

        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         avis.setUtilisateur(utilisateur);
        this.avisRepository.save(avis);
    }
}

package com.bamba.avis.service;

import com.bamba.avis.model.Utilisateur;
import com.bamba.avis.model.Validation;
import com.bamba.avis.repository.ValidationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@Transactional
public class ValidationService {
    private ValidationRepository validationRepository;
    private  NotificationService notificationService;

    @Autowired
    public ValidationService(ValidationRepository validationRepository, NotificationService notificationService) {
        this.validationRepository = validationRepository;
        this.notificationService = notificationService;
    }


    public  void enregistrer (Utilisateur utilisateur){
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        validation.setCreation(creation);
        Instant expiration = creation.plus(10,MINUTES);
        validation.setExpiration(expiration);
        Random random = new  Random();
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d", randomInteger);
        validation.setCode(code);
        this.validationRepository.save(validation);
       // this.notificationService.envoyer(validation);

    }

    public Validation lireEnFonctionDuCode(String code) {
        return this.validationRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Votre code est invalide"));
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void nettoyerTable() {
        final Instant now = Instant.now();

        this.validationRepository.deleteAllByExpirationBefore(now);
    }
}

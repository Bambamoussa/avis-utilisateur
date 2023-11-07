package com.bamba.avis.service;

import com.bamba.avis.model.Validation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;


@Service
public class NotificationService    {

   private JavaMailSender javaMailSender;

   @Autowired
    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void envoyer(Validation validation){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("moussafrance1998@gmail.com");
        mailMessage.setTo(validation.getUtilisateur().getEmail());
        mailMessage.setSubject("Votre code d'activation");
      String text=  String.format("Bonjour %s, <br /> votre code d activation va bientot expirer ",
                validation.getUtilisateur().getNom(),
                validation.getCode()
                );
        mailMessage.setText(text);

        javaMailSender.send(mailMessage);
    }

}

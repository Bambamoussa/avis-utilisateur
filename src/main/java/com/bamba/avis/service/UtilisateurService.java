package com.bamba.avis.service;

import com.bamba.avis.dto.UtilisateurDto;
import com.bamba.avis.model.Role;
import com.bamba.avis.model.TypeRole;
import com.bamba.avis.model.Utilisateur;
import com.bamba.avis.model.Validation;
import com.bamba.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UtilisateurService implements UserDetailsService {

    private UtilisateurRepository utilisateurRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private  ValidationService validationService;


    public  void inscription(Utilisateur utilisateur){
        if(!utilisateur.getEmail().contains("@")){
            throw  new RuntimeException("mot de passe invalide");
        }
        if(!utilisateur.getEmail().contains(".")){
            throw  new RuntimeException("mot de passe invalide");
        }

        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(utilisateur.getEmail());

        if(utilisateurOptional.isPresent()){
            throw  new RuntimeException("ce utilisateur existe deja");
        }

        String mdpCrypte =this.passwordEncoder.encode(utilisateur.getMdp());
        utilisateur.setMdp(mdpCrypte);
        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);
       utilisateur =  this.utilisateurRepository.save(utilisateur);
       this.validationService.enregistrer(utilisateur);

    }

    @Override
    public Utilisateur loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository
                .findByEmail(username)
                .orElseThrow(() -> new  UsernameNotFoundException("Aucun utilisateur ne corespond à cet identifiant"));
    }

    public void modifierMotDePasse(Map<String, String> param) {
        Utilisateur utilisateur = this.loadUserByUsername(param.get("email"));
        this.validationService.enregistrer(utilisateur);

    }

    public void nouveauMotDePasse(UtilisateurDto utilisateurDto) {
        Utilisateur utilisateur = this.loadUserByUsername(utilisateurDto.email());
     final Validation validation= this.validationService.lireEnFonctionDuCode(utilisateurDto.code());
        if(validation.getUtilisateur().getEmail().equals(utilisateur.getEmail())) {
            String mdpCrypte = this.passwordEncoder.encode(utilisateurDto.password());
            utilisateur.setMdp(mdpCrypte);
            this.utilisateurRepository.save(utilisateur);
        }

    }
}

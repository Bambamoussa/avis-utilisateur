package com.bamba.avis.repository;

import com.bamba.avis.model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur,Integer> {
    Optional<Utilisateur> findByEmail(String email);

}

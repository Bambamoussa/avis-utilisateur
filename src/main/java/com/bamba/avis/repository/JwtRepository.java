package com.bamba.avis.repository;

import com.bamba.avis.model.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<Jwt,Integer> {
    Optional<Jwt> findByValeur(String valeur);

    Optional<Jwt> findByValeurAndDesactiveAndExpire(String valeur, boolean desactive, boolean expire);

    @Query("FROM Jwt j WHERE j.expire = :expire AND j.desactive = :desactive AND j.utilisateur.email = :email")
    Optional<Jwt> findByUtilisateurValidToken(String email, boolean desactive, boolean expire);


    @Query("FROM Jwt j WHERE  j.utilisateur.email = :email")
    Stream<Jwt> findByUtilisateur(String email );

    @Query("FROM Jwt j WHERE  j.refreshToken.valeur = :valeur")
    Optional<Jwt> findByRefreshToken(String valeur );


    void deleteAllByExpireAndDesactive(boolean expire, boolean desactive);

}

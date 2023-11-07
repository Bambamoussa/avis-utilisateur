package com.bamba.avis.security;

import com.bamba.avis.model.Jwt;
import com.bamba.avis.model.RefreshToken;
import com.bamba.avis.model.Utilisateur;
import com.bamba.avis.repository.JwtRepository;
import com.bamba.avis.service.UtilisateurService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@AllArgsConstructor
@Transactional
@Slf4j
@Service
public class JwtService {
    public static final String REFRESH = "refresh";
    public static final String TOKEN_INVALID = "token invalid";
    private final String ENCRIPTION_KEY = "6aa6c5332af516c77c55e458f055f5551be8539eff28bebdd8ebec88eef2ab4b";
    private UtilisateurService utilisateurService;

    private JwtRepository jwtRepository;

    public Jwt tokenByValue(String token) {
       return this.jwtRepository.findByValeur(token).orElseThrow(() -> new UsernameNotFoundException("token inconnu"));
    }

    public void disableToken(Utilisateur utilisateur) {
        final List<Jwt> jwtList = this.jwtRepository.findByUtilisateur(utilisateur.getEmail()).map(
                jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpire(true);
                    return jwt;
                }
        ).collect(Collectors.toList());

        this.jwtRepository.saveAll(jwtList);

             }
    public Map<String, String> generate(String username) {
        Utilisateur utilisateur = this.utilisateurService.loadUserByUsername(username);
        disableToken(utilisateur);
         final Map<String,String> jwtMap = new HashMap<> (this.generateJwt(utilisateur));

        RefreshToken refreshToken = RefreshToken.builder().
                valeur(UUID.randomUUID().toString())
        .expire(false).creation(Instant.now()).expiration(Instant.now().plusMillis(30*60*1000)).build();

     final Jwt jwt =  Jwt.builder()
             .valeur(jwtMap.get("bearer"))
             .desactive(false)
             .expire(false)
             .utilisateur(utilisateur)
             .refreshToken(refreshToken)
             .build();

     this.jwtRepository.save(jwt);
     jwtMap.put(REFRESH, refreshToken.getValeur());
         return jwtMap;
    }

    public String extractUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30* 60 * 1000;

        final Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, utilisateur.getEmail()
        );

        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of("bearer", bearer);
    }

    private Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(ENCRIPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }


    public void deconnexion() {

        final Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

     Jwt jwt =   this.jwtRepository.
             findByUtilisateurValidToken(utilisateur.getEmail(),false,false).
             orElseThrow(() -> new UsernameNotFoundException(TOKEN_INVALID));
     jwt.setDesactive(true);
     jwt.setExpire(true);
     this.jwtRepository.save(jwt);
    }


    @Scheduled(cron = "0 */1 * * * *")
    public  void removeUselessJwt(){
        this.jwtRepository.deleteAllByExpireAndDesactive(true,true);
    }

    public Map<String, String> refreshToken(Map<String, String> refreshTokenRequest) {
      final Jwt jwt =  this.jwtRepository.findByRefreshToken(refreshTokenRequest.get(REFRESH)).orElseThrow(() -> new UsernameNotFoundException("token invalid"));
      if(jwt.getRefreshToken().isExpire()|| jwt.getRefreshToken().getExpiration().isBefore(Instant.now()) ){
          throw  new RuntimeException(TOKEN_INVALID);
      }
        Map<String,String> token  =this.generate(jwt.getUtilisateur().getEmail());
      this.disableToken(jwt.getUtilisateur());
      return token;

    }
}

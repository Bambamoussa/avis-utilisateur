package com.bamba.avis.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "validation")
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Instant expiration;

    private  Instant activation;

    private Instant creation;

    private  String code;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.DETACH})
    private  Utilisateur utilisateur;
}

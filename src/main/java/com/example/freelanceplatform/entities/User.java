package com.example.freelanceplatform.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity                          // ← dit à Hibernate : cette classe = une table
@Table(name = "users")           // ← nom de la table dans MySQL
public class User {

    @Id                          // ← clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← auto-incrément
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String photo;
    private String bio;
    private String domaine;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_competences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "competence")
    private List<String> competences;

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getDomaine() { return domaine; }
    public void setDomaine(String domaine) { this.domaine = domaine; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<String> getCompetences() { return competences; }
    public void setCompetences(List<String> competences) { this.competences = competences; }

    public String getPhoto() {
        return photo;
    }public void setPhoto(String photo) {
        this.photo = photo;
    }
}
package com.example.freelanceplatform.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private Boolean lu;

    private LocalDateTime dateEnvoi;

    // Relation : message envoyé par un user
    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private User expediteur;

    // Relation : message appartient à une discussion
    @ManyToOne
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Boolean getLu() { return lu; }
    public void setLu(Boolean lu) { this.lu = lu; }
    public String getDateEnvoiFormatted() {
        return dateEnvoi != null
                ? dateEnvoi.format(DateTimeFormatter.ofPattern("HH:mm"))
                : "";
    }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime d) { this.dateEnvoi = d; }

    public User getExpediteur() { return expediteur; }
    public void setExpediteur(User expediteur) { this.expediteur = expediteur; }

    public Discussion getDiscussion() { return discussion; }
    public void setDiscussion(Discussion discussion) { this.discussion = discussion; }
}

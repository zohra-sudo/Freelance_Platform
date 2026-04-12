package com.example.freelanceplatform.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "discussions")
public class Discussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sujet;

    private LocalDateTime dateCreation;

    // Messages de la discussion
    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    // ✅ EAGER obligatoire pour éviter LazyInitializationException
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "discussion_participants",
            joinColumns = @JoinColumn(name = "discussion_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public List<User> getParticipants() { return participants; }
    public void setParticipants(List<User> participants) { this.participants = participants; }
}
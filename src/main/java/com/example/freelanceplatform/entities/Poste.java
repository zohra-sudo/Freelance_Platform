package com.example.freelanceplatform.entities;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "postes")
public class Poste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private Integer delaiEstimatif;
    private Double budget;
    private String statut;

    @Temporal(TemporalType.DATE)
    private Date datePublication;

    @ElementCollection
    @CollectionTable(name = "poste_competences", joinColumns = @JoinColumn(name = "poste_id"))
    @Column(name = "competence")
    private List<String> competencesRequises;

    // Relation : un User (client) publie plusieurs postes
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDelaiEstimatif() { return delaiEstimatif; }
    public void setDelaiEstimatif(Integer delaiEstimatif) { this.delaiEstimatif = delaiEstimatif; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Date getDatePublication() { return datePublication; }
    public void setDatePublication(Date datePublication) { this.datePublication = datePublication; }

    public List<String> getCompetencesRequises() { return competencesRequises; }
    public void setCompetencesRequises(List<String> c) { this.competencesRequises = c; }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
}

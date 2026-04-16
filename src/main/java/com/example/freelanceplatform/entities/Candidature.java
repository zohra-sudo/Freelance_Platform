package com.example.freelanceplatform.entities;


import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "candidatures")
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageMotivation;
    private Integer propositionDelai;
    private String statut;
    private String phone;
    private String portfolioLink;
    @Temporal(TemporalType.DATE)
    private Date datePostulation;

    // Relation : un freelance postule
    @ManyToOne
    @JoinColumn(name = "freelance_id", nullable = false)
    private User freelance;

    // Relation : candidature liée à un poste
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @Column(name = "cv_file")
    private String cvFile;
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessageMotivation() { return messageMotivation; }
    public void setMessageMotivation(String m) { this.messageMotivation = m; }

    public Integer getPropositionDelai() { return propositionDelai; }
    public void setPropositionDelai(Integer p) { this.propositionDelai = p; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Date getDatePostulation() { return datePostulation; }
    public void setDatePostulation(Date d) { this.datePostulation = d; }

    public User getFreelance() { return freelance; }
    public void setFreelance(User freelance) { this.freelance = freelance; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getCvFile() {
        return cvFile;
    }

    public void setCvFile(String cvFile) {
        this.cvFile = cvFile;
    }

    public String getPortfolioLink() {
        return portfolioLink;
    }

    public void setPortfolioLink(String portfolioLink) {
        this.portfolioLink = portfolioLink;
    }

}

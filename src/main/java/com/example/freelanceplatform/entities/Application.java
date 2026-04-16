package com.example.freelanceplatform.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(length = 500)
    private String portfolioLink;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String cvFileName;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }

    public Application() {}

    // Getters
    public Long getId() { return id; }
    public Project getProject() { return project; }
    public User getFreelancer() { return freelancer; }
    public String getMessage() { return message; }
    public String getPortfolioLink() { return portfolioLink; }
    public String getPhone() { return phone; }
    public String getCvFileName() { return cvFileName; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setProject(Project project) { this.project = project; }
    public void setFreelancer(User freelancer) { this.freelancer = freelancer; }
    public void setMessage(String message) { this.message = message; }
    public void setPortfolioLink(String portfolioLink) { this.portfolioLink = portfolioLink; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCvFileName(String cvFileName) { this.cvFileName = cvFileName; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
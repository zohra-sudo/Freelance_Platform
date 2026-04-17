package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Candidature; // CHANGÉ
import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.dao.CandidatureDAO; // CHANGÉ
import com.example.freelanceplatform.service.ProjectService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

import java.io.Serializable;
import java.util.Date; // AJOUTÉ
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ApplicationBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationBean.class.getName());

    private Long projectId;
    private Project project;

    // ON UTILISE DÉSORMAIS CANDIDATURE (L'entité que le recruteur voit)
    private Candidature candidature = new Candidature();

    private Part cvFile;
    private boolean candidatureEnvoyee = false;

    @Inject
    private ProjectService projectService;

    @Inject
    private CandidatureDAO candidatureDAO; // ON UTILISE LE DAO DE CANDIDATURE

    @Inject
    private AuthBean authBean;

    public void loadProject() {
        if (projectId != null) {
            project = projectService.findById(projectId);
            if (project != null) {
                candidature.setProject(project); // LIÉ À CANDIDATURE
            }
        }
        candidatureEnvoyee = false;
    }

    public String submitApplication() {
        System.out.println("DEBUG: submitApplication appelé !");
        if (!authBean.isConnecte()) {
            return "login?faces-redirect=true";
        }

        User currentUser = authBean.getUserConnecte();

        // Sécurité : recharger le projet si besoin
        if (project == null && projectId != null) {
            project = projectService.findById(projectId);
        }

        if (project == null) return null;

        try {
            // ON REMPLIT L'OBJET CANDIDATURE
            candidature.setProject(project);
            candidature.setFreelance(currentUser);
            candidature.setStatut("PENDING");
            candidature.setDatePostulation(new Date());

            // On sauvegarde en base de données
            candidatureDAO.save(candidature);

            candidatureEnvoyee = true;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "✓ Candidature envoyée !", null));

            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la soumission", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erreur lors de l'envoi : " + e.getMessage(), null));
            return null;  // candidatureEnvoyee reste false → le formulaire reste visible avec le message d'erreur
        }
    }

    // GETTERS & SETTERS MIS À JOUR
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public Candidature getCandidature() { return candidature; } // CHANGÉ
    public void setCandidature(Candidature candidature) { this.candidature = candidature; } // CHANGÉ
    public Part getCvFile() { return cvFile; }
    public void setCvFile(Part cvFile) { this.cvFile = cvFile; }
    public boolean isCandidatureEnvoyee() { return candidatureEnvoyee; }
    // Ajoute cette méthode dans ApplicationBean.java
    public void setCandidatureEnvoyee(boolean candidatureEnvoyee) {
        this.candidatureEnvoyee = candidatureEnvoyee;
    }
}
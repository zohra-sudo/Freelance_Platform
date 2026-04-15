package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Application;
import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.ApplicationService;
import com.example.freelanceplatform.service.ProjectService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ApplicationBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationBean.class.getName());

    private Long projectId;
    private Project project;
    private Application application = new Application();
    private Part cvFile;
    private boolean candidatureEnvoyee = false;  // NOUVEAU : flag pour savoir si candidature envoyée

    @Inject
    private ProjectService projectService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private AuthBean authBean;

    public void loadProject() {
        System.out.println("=== loadProject() ===");
        System.out.println("projectId reçu = " + projectId);

        if (projectId != null) {
            project = projectService.findById(projectId);
            System.out.println("Projet trouvé = " + (project != null ? project.getTitle() : "null"));
            if (project != null) {
                application.setProject(project);
            }
        }

        // Réinitialiser le flag si on charge un nouveau projet
        candidatureEnvoyee = false;
    }

    public String submitApplication() {
        System.out.println("=== submitApplication() appelé ===");
        System.out.println("projectId reçu dans submit = " + projectId);
        System.out.println("project = " + (project != null ? project.getTitle() : "null"));

        if (!authBean.isConnecte()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vous devez être connecté pour postuler", null));
            return "login?faces-redirect=true";
        }

        User currentUser = authBean.getUserConnecte();

        // Si project est null, essayer de le recharger
        if (project == null && projectId != null) {
            project = projectService.findById(projectId);
            System.out.println("Projet rechargé = " + (project != null ? project.getTitle() : "null"));
            if (project != null) {
                application.setProject(project);
            }
        }

        if (project == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Projet introuvable ou ID invalide", null));
            return null;
        }

        // Vérifier que l'utilisateur ne postule pas à son propre projet
        if (project.getAuthor().getId().equals(currentUser.getId())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vous ne pouvez pas postuler à votre propre projet", null));
            return null;
        }

        // Vérifier candidature existante
        if (applicationService.hasApplied(project.getId(), currentUser.getId())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Vous avez déjà postulé à ce projet", null));
            return null;
        }

        try {
            application.setProject(project);
            application.setFreelancer(currentUser);
            application.setStatus("PENDING");

            // Upload CV
            if (cvFile != null && cvFile.getSize() > 0) {
                String fileName = System.currentTimeMillis() + "_" + Paths.get(cvFile.getSubmittedFileName()).getFileName();
                String uploadDir = System.getProperty("java.io.tmpdir") + "/freelance_uploads/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream input = cvFile.getInputStream()) {
                    Files.copy(input, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                    application.setCvFileName(fileName);
                }
            }

            applicationService.save(application);

            // NOUVEAU : Marquer la candidature comme envoyée
            candidatureEnvoyee = true;

            // Ajouter le message de succès
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "✓ Candidature envoyée avec succès !",
                            "Le propriétaire du projet va examiner votre profil."));

            // Retourner null pour rester sur la même page et afficher le message
            return null;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi de la candidature", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage(), null));
            return null;
        }
    }

    // ===== GETTERS & SETTERS =====

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Part getCvFile() {
        return cvFile;
    }

    public void setCvFile(Part cvFile) {
        this.cvFile = cvFile;
    }

    public boolean isCandidatureEnvoyee() {
        return candidatureEnvoyee;
    }

    public void setCandidatureEnvoyee(boolean candidatureEnvoyee) {
        this.candidatureEnvoyee = candidatureEnvoyee;
    }
}
package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Project;
import com.example.freelanceplatform.service.ProjectService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class EditProjectBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long projectId;
    private Project projectEnEdition;

    @Inject
    private ProjectService projectService;

    @Inject
    private AuthBean authBean;

    public void loadProject() {
        System.out.println("=== EditProjectBean.loadProject() ===");
        System.out.println("projectId reçu = " + projectId);

        if (projectId != null && projectId > 0) {
            projectEnEdition = projectService.findById(projectId);
            System.out.println("Projet trouvé = " + (projectEnEdition != null ? projectEnEdition.getTitle() : "null"));
        } else {
            projectEnEdition = new Project();
            if (authBean != null && authBean.getUserConnecte() != null) {
                projectEnEdition.setAuthor(authBean.getUserConnecte());
            }
            System.out.println("Nouveau projet créé");
        }

        if (projectEnEdition == null) {
            projectEnEdition = new Project();
            if (authBean != null && authBean.getUserConnecte() != null) {
                projectEnEdition.setAuthor(authBean.getUserConnecte());
            }
        }
    }

    public String sauvegarderProjet() {
        System.out.println("=== EditProjectBean.sauvegarderProjet() ===");
        System.out.println("projectEnEdition = " + projectEnEdition);

        // Vérifier l'utilisateur
        if (authBean == null || !authBean.isConnecte()) {
            System.out.println("ERREUR: Utilisateur non connecté");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Vous devez être connecté", null));
            return null;
        }

        // Vérifier le projet
        if (projectEnEdition == null) {
            System.out.println("ERREUR: projectEnEdition est NULL");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur: projet introuvable", null));
            return null;
        }

        // Vérifier les champs obligatoires
        if (projectEnEdition.getTitle() == null || projectEnEdition.getTitle().trim().isEmpty()) {
            System.out.println("ERREUR: Titre manquant");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Le titre est obligatoire", null));
            return null;
        }

        if (projectEnEdition.getBudget() == null) {
            System.out.println("ERREUR: Budget manquant");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Le budget est obligatoire", null));
            return null;
        }

        // Associer l'auteur
        projectEnEdition.setAuthor(authBean.getUserConnecte());

        try {
            if (projectEnEdition.getId() == null) {
                // Nouveau projet
                System.out.println("Ajout d'un nouveau projet: " + projectEnEdition.getTitle());
                projectService.addProject(projectEnEdition);
                System.out.println("Projet ajouté avec ID: " + projectEnEdition.getId());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Projet ajouté avec succès", null));
            } else {
                // Modification
                System.out.println("Modification du projet ID: " + projectEnEdition.getId());
                projectService.updateProject(projectEnEdition);
                System.out.println("Projet modifié avec succès");
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Projet modifié avec succès", null));
            }

            // Redirection vers le profil
            String redirectUrl = "profil.xhtml?id=" + authBean.getUserConnecte().getId() + "&faces-redirect=true";
            System.out.println("Redirection vers: " + redirectUrl);
            return redirectUrl;

        } catch (Exception e) {
            System.out.println("ERREUR lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur: " + e.getMessage(), null));
            return null;
        }
    }

    public String annulerEdition() {
        if (authBean != null && authBean.getUserConnecte() != null) {
            return "profil.xhtml?id=" + authBean.getUserConnecte().getId() + "&faces-redirect=true";
        }
        return "index.xhtml?faces-redirect=true";
    }

    // ========== GETTERS & SETTERS ==========

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Project getProjectEnEdition() {
        return projectEnEdition;
    }

    public void setProjectEnEdition(Project projectEnEdition) {
        this.projectEnEdition = projectEnEdition;
    }

    public String testAction() {
        System.out.println("=== TEST ACTION EXECUTÉE ===");
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Test réussi !", null));
        return null;
    }
}
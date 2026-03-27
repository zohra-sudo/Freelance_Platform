package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named           // ← accessible depuis les pages JSF
@SessionScoped   // ← garde les données pendant toute la session
public class AuthBean implements Serializable {

    @Inject
    private UserService userService;

    // Champs du formulaire
    private String email;
    private String motDePasse;
    private String messageErreur;
    private User userConnecte;

    // Champs inscription
    private User nouveauUser = new User();

    // ===== CONNEXION =====
    public String connecter() {
        userConnecte = userService.connecter(email, motDePasse);
        if (userConnecte != null) {
            messageErreur = null;
            return "index?faces-redirect=true";
        }
        messageErreur = "Email ou mot de passe incorrect !";
        return null;
    }
    // ===== INSCRIPTION =====

    public String inscrire() {
        boolean ok = userService.inscrire(nouveauUser);
        if (ok) {
            messageErreur = null;
            nouveauUser = new User();
            return "login?faces-redirect=true";
        }
        messageErreur = "Cet email est déjà utilisé !";
        return null;
    }
    // ===== DÉCONNEXION =====
    public String deconnecter() {
        try {
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();

            facesContext.getExternalContext().invalidateSession();

            String contextPath = facesContext.getExternalContext().getRequestContextPath();
            facesContext.getExternalContext().redirect(contextPath + "/login.xhtml");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    // Vérifier si connecté
    public boolean isConnecte() {
        return userConnecte != null;
    }

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getMessageErreur() { return messageErreur; }
    public User getUserConnecte() { return userConnecte; }
    public void setUserConnecte(User u) { this.userConnecte = u; }

    public User getNouveauUser() { return nouveauUser; }
    public void setNouveauUser(User u) { this.nouveauUser = u; }
}

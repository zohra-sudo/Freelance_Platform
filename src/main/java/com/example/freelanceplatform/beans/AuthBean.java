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
            return "accueil?faces-redirect=true";  // ← redirige vers accueil
        }
        messageErreur = "Email ou mot de passe incorrect !";
        return null;
    }

    // ===== INSCRIPTION =====
    public String inscrire() {
        boolean ok = userService.inscrire(nouveauUser);
        if (ok) {
            return "login?faces-redirect=true";  // ← redirige vers login
        }
        messageErreur = "Cet email est déjà utilisé !";
        return null;
    }

    // ===== DÉCONNEXION =====
    public String deconnecter() {
        userConnecte = null;
        return "login?faces-redirect=true";
    }

    // Vérifier si connecté
    public boolean isConnecte() {
        return userConnecte != null;
    }
    public void setUserConnecte(User userConnecte) {
        this.userConnecte = userConnecte;
    }

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getMessageErreur() { return messageErreur; }
    public User getUserConnecte() { return userConnecte; }
    public User getNouveauUser() { return nouveauUser; }
    public void setNouveauUser(User u) { this.nouveauUser = u; }
}

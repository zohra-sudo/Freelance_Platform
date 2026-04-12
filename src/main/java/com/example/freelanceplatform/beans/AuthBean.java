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

    // Champs pour la réinitialisation (Modifié pour Recovery Code)
    private String resetEmail;
    private String resetRecoveryCode;
    private String newMotDePasse;
    private User userToReset;

    // ===== CONNEXION =====
    public String connecter() {
        userConnecte = userService.connecter(email, motDePasse);
        if (userConnecte != null) {
            messageErreur = null;
            return "index?faces-redirect=true";
        }
        messageErreur = "Email or password incorrect!";
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
        messageErreur = "This email is already in use!";
        return null;
    }

    // ===== DÉCONNEXION =====
    public String deconnecter() {
        try {
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            facesContext.getExternalContext().invalidateSession();
            return "login?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== RÉINITIALISATION MOT DE PASSE =====
    public String verifierCode() {
        if (resetEmail == null || resetRecoveryCode == null) return null;

        // On ajoute .trim() pour ignorer les espaces accidentels avant ou après le texte
        userToReset = userService.verifierCodeSecret(resetEmail.trim(), resetRecoveryCode.trim());

        if (userToReset != null) {
            messageErreur = null;
            return "updatePassword?faces-redirect=true";
        }
        messageErreur = "Email or Recovery Code incorrect!";
        return null;
    }

    public String validerNouveauMotDePasse() {
        if (userToReset != null && newMotDePasse != null && !newMotDePasse.isEmpty()) {
            userToReset.setMotDePasse(newMotDePasse);
            userService.update(userToReset);

            // Nettoyage des champs
            userToReset = null;
            resetEmail = null;
            resetRecoveryCode = null;
            newMotDePasse = null;
            messageErreur = null;
            return "login?faces-redirect=true";
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
    public void setMessageErreur(String messageErreur) { this.messageErreur = messageErreur; }

    public User getUserConnecte() { return userConnecte; }
    public void setUserConnecte(User u) { this.userConnecte = u; }

    public User getNouveauUser() { return nouveauUser; }
    public void setNouveauUser(User u) { this.nouveauUser = u; }

    public String getResetEmail() { return resetEmail; }
    public void setResetEmail(String resetEmail) { this.resetEmail = resetEmail; }

    public String getResetRecoveryCode() { return resetRecoveryCode; }
    public void setResetRecoveryCode(String resetRecoveryCode) { this.resetRecoveryCode = resetRecoveryCode; }

    public String getNewMotDePasse() { return newMotDePasse; }
    public void setNewMotDePasse(String newMotDePasse) { this.newMotDePasse = newMotDePasse; }
}
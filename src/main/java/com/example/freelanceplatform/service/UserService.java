package com.example.freelanceplatform.service;


import com.example.freelanceplatform.dao.UserDAO;
import com.example.freelanceplatform.entities.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.List;

@Stateless
public class UserService implements Serializable {

    @Inject
    private UserDAO userDAO;
    private static final long serialVersionUID = 1L;

    // Inscription
    public boolean inscrire(User user) {
        // Vérifier si email déjà utilisé
        if (userDAO.emailExists(user.getEmail())) {
            return false;  // email déjà pris
        }
        userDAO.save(user);
        return true;
    }
    public List<User> findAll() {
        return userDAO.findAll();
    }

    // Recherche par nom ou domaine
    public List<User> searchByNomOrDomaine(String query) {
        return userDAO.searchByNomOrDomaine(query);
    }

    // Connexion
    public User connecter(String email, String motDePasse) {
        User user = userDAO.findByEmail(email);
        if (user != null && user.getMotDePasse().equals(motDePasse)) {
            return user;  // connexion OK
        }
        return null;  // échec
    }

    // Modifier profil
    public void modifierProfil(User user) {
        userDAO.update(user);
    }

    // Trouver par ID
    public User findById(Long id) {
        return userDAO.findById(id);
    }
}

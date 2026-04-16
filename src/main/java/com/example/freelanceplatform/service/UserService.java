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

    public boolean inscrire(User user) {
        if (userDAO.emailExists(user.getEmail())) {
            return false;
        }
        userDAO.save(user);
        return true;
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }

    public List<User> searchByNomOrDomaine(String query) {
        return userDAO.searchByNomOrDomaine(query);
    }

    public User connecter(String email, String motDePasse) {
        User user = userDAO.findByEmail(email);
        if (user != null && user.getMotDePasse().equals(motDePasse)) {
            return user;
        }
        return null;
    }

    public User verifierCodeSecret(String email, String codeSecret) {
        return userDAO.verifierCodeSecret(email, codeSecret);
    }

    public void update(User user) {
        userDAO.update(user);
    }

    // ✅ CORRECTION : on retourne l'entité managée après merge
    public User modifierProfil(User user) {
        return userDAO.update(user);
    }

    public User findById(Long id) {
        return userDAO.findById(id);
    }
}
package com.example.freelanceplatform.dao;

import com.example.freelanceplatform.entities.User;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless   // ← EJB géré par WildFly
public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    // Trouver un user par email (pour la connexion)
    public User findByEmail(String email) {
        List<User> result = em.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    // Vérifier si email existe déjà (pour l'inscription)
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }
    public List<User> searchByNomOrDomaine(String query) {
        String q = "%" + query.toLowerCase() + "%";
        return em.createQuery(
                        "SELECT u FROM User u WHERE LOWER(u.nom) LIKE :q OR LOWER(u.domaine) LIKE :q",
                        User.class)
                .setParameter("q", q)
                .getResultList();
    }
}

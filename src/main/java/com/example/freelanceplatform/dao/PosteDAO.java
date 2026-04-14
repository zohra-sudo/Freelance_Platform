package com.example.freelanceplatform.dao;

import com.example.freelanceplatform.entities.Poste;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class PosteDAO extends GenericDAO<Poste> {

    public PosteDAO() {
        super(Poste.class);
    }

    // Tous les postes d'un client
    public List<Poste> findByClient(Long clientId) {
        return em.createQuery(
                        "SELECT p FROM Poste p WHERE p.client.id = :clientId", Poste.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    // Tous les postes disponibles
    public List<Poste> findPostesOuverts() {
        return em.createQuery(
                        "SELECT p FROM Poste p WHERE p.statut = 'OUVERT'", Poste.class)
                .getResultList();
    }

}

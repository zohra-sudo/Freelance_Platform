package com.example.freelanceplatform.dao;

import com.example.freelanceplatform.entities.Candidature;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class CandidatureDAO extends GenericDAO<Candidature> {
    public List<Candidature> findByRecruiter(Long userId) {
        return em.createQuery(
                        "SELECT c FROM Candidature c WHERE c.project.author.id = :userId",
                        Candidature.class)
                .setParameter("userId", userId)
                .getResultList();
    }
    public CandidatureDAO() {
        super(Candidature.class);
    }

    // Candidatures d'un freelance
    public List<Candidature> findByFreelance(Long freelanceId) {
        return em.createQuery(
                        "SELECT c FROM Candidature c WHERE c.freelance.id = :id", Candidature.class)
                .setParameter("id", freelanceId)
                .getResultList();
    }

    // Candidatures reçues pour un poste
    public List<Candidature> findByPoste(Long projectId) {
        return em.createQuery(
                        "SELECT c FROM Candidature c WHERE c.project.id = :id", Candidature.class)
                .setParameter("id", projectId)
                .getResultList();
    }
}

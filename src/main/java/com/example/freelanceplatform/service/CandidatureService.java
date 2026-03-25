package com.example.freelanceplatform.service;

import com.example.freelanceplatform.dao.CandidatureDAO;
import com.example.freelanceplatform.entities.Candidature;
import com.example.freelanceplatform.entities.Poste;
import com.example.freelanceplatform.entities.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Stateless
public class CandidatureService implements Serializable {

    @Inject
    private CandidatureDAO candidatureDAO;
    private static final long serialVersionUID = 1L;

    // Postuler à un poste
    public void postuler(Candidature candidature, User freelance, Poste poste) {
        candidature.setFreelance(freelance);
        candidature.setPoste(poste);
        candidature.setDatePostulation(new Date());
        candidature.setStatut("EN_ATTENTE");
        candidatureDAO.save(candidature);
    }

    // Voir candidatures reçues pour un poste
    public List<Candidature> getCandidaturesPoste(Long posteId) {
        return candidatureDAO.findByPoste(posteId);
    }

    // Voir mes candidatures (freelance)
    public List<Candidature> getMesCandidatures(Long freelanceId) {
        return candidatureDAO.findByFreelance(freelanceId);
    }

    // Sélectionner un candidat
    public void selectionnerCandidat(Long candidatureId) {
        Candidature c = candidatureDAO.findById(candidatureId);
        if (c != null) {
            c.setStatut("ACCEPTE");
            candidatureDAO.update(c);
        }
    }
}

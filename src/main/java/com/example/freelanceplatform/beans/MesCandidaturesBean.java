package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Candidature;
import com.example.freelanceplatform.dao.CandidatureDAO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class MesCandidaturesBean implements Serializable {

    @Inject
    private CandidatureDAO candidatureDAO;

    @Inject
    private AuthBean authBean;

    private List<Candidature> candidatures;

    @PostConstruct
    public void init() {
        if (authBean.isConnecte()) {
            // On utilise la méthode de ton DAO qui filtre par l'ID du freelance
            candidatures = candidatureDAO.findByFreelance(authBean.getUserConnecte().getId());
        }
    }
    public List<Candidature> getCandidatures() {
        return candidatures;
    }
}
package com.example.freelanceplatform.service;


import com.example.freelanceplatform.dao.PosteDAO;
import com.example.freelanceplatform.entities.Poste;
import com.example.freelanceplatform.entities.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Stateless
public class PosteService implements Serializable {

    @Inject
    private PosteDAO posteDAO;
    private static final long serialVersionUID = 1L;

    // Créer un poste
    public void creerPoste(Poste poste, User client) {
        poste.setClient(client);
        poste.setDatePublication(new Date());
        poste.setStatut("OUVERT");
        posteDAO.save(poste);
    }

    // Modifier un poste
    public void modifierPoste(Poste poste) {
        posteDAO.update(poste);
    }

    // Supprimer un poste
    public void supprimerPoste(Long id) {
        Poste poste = posteDAO.findById(id);
        if (poste != null) posteDAO.delete(poste);
    }

    // Tous les postes disponibles
    public List<Poste> getPostesOuverts() {
        return posteDAO.findPostesOuverts();
    }

    // Postes d'un client
    public List<Poste> getMesPostes(Long clientId) {
        return posteDAO.findByClient(clientId);
    }

    public Poste findById(Long id) {
        return posteDAO.findById(id);
    }
}

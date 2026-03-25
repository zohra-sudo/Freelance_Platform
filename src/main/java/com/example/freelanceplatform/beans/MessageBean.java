package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.entities.Discussion;
import com.example.freelanceplatform.entities.Message;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import com.example.freelanceplatform.dao.MessageDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Named
@RequestScoped
public class MessageBean implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private MessageDAO messageDAO;

    @Inject
    private AuthBean authBean;

    private Long destinataireId;
    private String sujet;
    private String contenu;

    public String envoyerMessage() {
        if (contenu == null || contenu.trim().isEmpty()) return null;
        if (destinataireId == null) return null;

        try {
            User destinataire = userService.findById(destinataireId);
            User expediteur   = authBean.getUserConnecte();

            if (destinataire == null) return null;

            // Crée une nouvelle Discussion
            Discussion discussion = new Discussion();
            discussion.setSujet(sujet != null ? sujet : "Nouveau message");
            discussion.setDateCreation(LocalDateTime.now());
            discussion.setParticipants(new ArrayList<>());
            discussion.getParticipants().add(destinataire);
            if (expediteur != null) discussion.getParticipants().add(expediteur);

            // Crée le Message
            Message message = new Message();
            message.setContenu(contenu.trim());
            message.setDateEnvoi(LocalDateTime.now());
            message.setLu(false);
            message.setExpediteur(expediteur);
            message.setDiscussion(discussion);

            // Sauvegarde
            messageDAO.save(discussion);
            messageDAO.saveMessage(message);

            // Reset
            sujet   = null;
            contenu = null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Reste sur la même page (AJAX)
    }

    // Getters & Setters
    public Long getDestinatireId() { return destinataireId; }
    public Long getDestinataireid() { return destinataireId; }
    public void setDestinataireid(Long id) { this.destinataireId = id; }

    public Long getDestinataireid2() { return destinataireId; }

    public Long getDestinataiid() { return destinataireId; }

    public Long getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Long destinataireId) { this.destinataireId = destinataireId; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
}

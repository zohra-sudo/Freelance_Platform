package com.example.freelanceplatform.beans;

import com.example.freelanceplatform.dao.MessageDAO;
import com.example.freelanceplatform.entities.Discussion;
import com.example.freelanceplatform.entities.Message;
import com.example.freelanceplatform.entities.User;
import com.example.freelanceplatform.service.UserService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class MessagerieBean implements Serializable {

    @EJB  private MessageDAO messageDAO;
    @EJB  private UserService userService;
    @Inject private AuthBean authBean;

    private Long destinataireId;
    private Long discussionId;
    private Discussion discussionActive;
    private List<Discussion> conversations;
    private List<Message> messages;
    private String nouveauMessage;

    private static final String[] COLORS = {
            "#C47D2B","#8B5CF6","#059669","#DC2626","#2563EB","#D97706"
    };

    public void init() {
        // ── Si non connecté → login ──
        if (!authBean.isConnecte()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("login.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        User moi = authBean.getUserConnecte();

        // ── Charge toutes les conversations ──
        conversations = messageDAO.findDiscussionsOf(moi.getId());

        // ── CAS 1 : vient d'un profil avec ?destinataire=X ──
        if (destinataireId != null) {
            User destinataire = userService.findById(destinataireId);
            if (destinataire != null && !destinataire.getId().equals(moi.getId())) {
                // Cherche si discussion existante
                Discussion existing = findExistingDiscussion(moi, destinataire);
                if (existing != null) {
                    discussionActive = existing;
                } else {
                    // Crée une nouvelle discussion
                    Discussion disc = new Discussion();
                    disc.setSujet("Conversation");
                    disc.setDateCreation(LocalDateTime.now());
                    List<User> participants = new ArrayList<>();
                    participants.add(moi);
                    participants.add(destinataire);
                    disc.setParticipants(participants);
                    messageDAO.saveDiscussion(disc);
                    discussionActive = disc;
                    // Recharge les conversations
                    conversations = messageDAO.findDiscussionsOf(moi.getId());
                }
            }
            destinataireId = null;
        }

        // ── CAS 2 : sélection depuis sidebar avec ?discussion=X ──
        if (discussionId != null) {
            if (conversations != null) {
                for (Discussion d : conversations) {
                    if (d.getId().equals(discussionId)) {
                        discussionActive = d;
                        break;
                    }
                }
            }
            discussionId = null;
        }

        // ── Charge les messages de la discussion active ──
        if (discussionActive != null) {
            messages = messageDAO.findByDiscussion(discussionActive.getId());
        }
    }

    // ── Envoyer un message ──
    public void envoyerMessage() {
        if (nouveauMessage == null || nouveauMessage.trim().isEmpty()) return;
        if (discussionActive == null || !authBean.isConnecte()) return;

        Message msg = new Message();
        msg.setContenu(nouveauMessage.trim());
        msg.setDateEnvoi(LocalDateTime.now());
        msg.setLu(false);
        msg.setExpediteur(authBean.getUserConnecte());
        msg.setDiscussion(discussionActive);
        messageDAO.saveMessage(msg);

        nouveauMessage = null;
        // Recharge les messages
        messages = messageDAO.findByDiscussion(discussionActive.getId());
        // Met à jour la liste des conversations
        conversations = messageDAO.findDiscussionsOf(authBean.getUserConnecte().getId());
    }

    // ── Trouve une discussion existante entre deux users ──
    private Discussion findExistingDiscussion(User u1, User u2) {
        if (conversations == null) return null;
        for (Discussion d : conversations) {
            if (d.getParticipants() == null) continue;
            boolean hasU2 = d.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(u2.getId()));
            if (hasU2) return d;
        }
        return null;
    }

    // ── Helper : trouve l'interlocuteur (l'autre personne) ──
    public User getInterlocutor(Discussion d) {
        if (d == null || !authBean.isConnecte()) return null;
        Long myId = authBean.getUserConnecte().getId();
        if (d.getParticipants() == null) return null;
        return d.getParticipants().stream()
                .filter(p -> !p.getId().equals(myId))
                .findFirst().orElse(null);
    }

    // ── Helpers sidebar ──
    public String getInterlocutorName(Discussion d) {
        User u = getInterlocutor(d);
        return u != null ? u.getNom() : "Unknown";
    }
    public String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getInterlocutorPhoto(Discussion d) {
        User u = getInterlocutor(d);
        return (u != null && u.getPhoto() != null && !u.getPhoto().isEmpty())
                ? u.getPhoto() : "";
    }

    public String getInterlocutorInitials(Discussion d) {
        User u = getInterlocutor(d);
        if (u == null || u.getNom() == null) return "?";
        String[] parts = u.getNom().trim().split("\\s+");
        return parts.length >= 2
                ? ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase()
                : String.valueOf(u.getNom().charAt(0)).toUpperCase();
    }

    public String getInterlocutorColor(Discussion d) {
        User u = getInterlocutor(d);
        if (u == null || u.getNom() == null) return COLORS[0];
        return COLORS[Math.abs(u.getNom().hashCode()) % COLORS.length];
    }

    public String getLastMessage(Discussion d) {
        List<Message> msgs = messageDAO.findByDiscussion(d.getId());
        if (msgs == null || msgs.isEmpty()) return "Start chatting...";
        String c = msgs.get(msgs.size() - 1).getContenu();
        return c.length() > 38 ? c.substring(0, 38) + "..." : c;
    }

    public String getLastMessageTime(Discussion d) {
        List<Message> msgs = messageDAO.findByDiscussion(d.getId());
        if (msgs == null || msgs.isEmpty()) return "";
        LocalDateTime t = msgs.get(msgs.size() - 1).getDateEnvoi();
        return t != null ? t.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }

    // ── Helpers conversation active ──
    public String getInterlocutorActiveName() {
        return getInterlocutorName(discussionActive);
    }
    public String getInterlocutorActivePhoto() {
        return getInterlocutorPhoto(discussionActive);
    }
    public String getInterlocutorActiveInitials() {
        return getInterlocutorInitials(discussionActive);
    }
    public String getInterlocutorActiveColor() {
        return getInterlocutorColor(discussionActive);
    }
    public Long getInterlocutorActiveId() {
        User u = getInterlocutor(discussionActive);
        return u != null ? u.getId() : null;
    }

    // ── Getters & Setters ──
    public Long getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Long v) { this.destinataireId = v; }

    public Long getDiscussionId() { return discussionId; }
    public void setDiscussionId(Long v) { this.discussionId = v; }

    public Discussion getDiscussionActive() { return discussionActive; }
    public List<Discussion> getConversations() { return conversations; }
    public List<Message> getMessages() { return messages; }

    public String getNouveauMessage() { return nouveauMessage; }
    public void setNouveauMessage(String v) { this.nouveauMessage = v; }
}
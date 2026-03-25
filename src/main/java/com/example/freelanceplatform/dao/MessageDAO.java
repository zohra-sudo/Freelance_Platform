package com.example.freelanceplatform.dao;


import com.example.freelanceplatform.entities.Discussion;
import com.example.freelanceplatform.entities.Message;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class MessageDAO extends GenericDAO<Message> {

    public MessageDAO() {
        super(Message.class);
    }

    // Messages d'une discussion
    public List<Message> findByDiscussion(Long discussionId) {
        return em.createQuery(
                        "SELECT m FROM Message m WHERE m.discussion.id = :id ORDER BY m.dateEnvoi", Message.class)
                .setParameter("id", discussionId)
                .getResultList();
    }


    // Sauvegarde une Discussion
    public void save(Discussion discussion) {
        em.persist(discussion);
    }

    // Sauvegarde un Message
    public void saveMessage(Message message) {
        em.persist(message);
    }
}

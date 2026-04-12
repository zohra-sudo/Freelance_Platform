package com.example.freelanceplatform.dao;

import com.example.freelanceplatform.entities.Discussion;
import com.example.freelanceplatform.entities.Message;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class MessageDAO {

    @PersistenceContext(unitName = "FreelancePU")
    private EntityManager em;

    public void saveDiscussion(Discussion discussion) {
        em.persist(discussion);
        em.flush();
    }

    public void saveMessage(Message message) {
        em.persist(message);
        em.flush();
    }

    public List<Message> findByDiscussion(Long discussionId) {
        return em.createQuery(
                        "SELECT m FROM Message m WHERE m.discussion.id = :id ORDER BY m.dateEnvoi ASC",
                        Message.class)
                .setParameter("id", discussionId)
                .getResultList();
    }

    public List<Discussion> findDiscussionsOf(Long userId) {
        return em.createQuery(
                        "SELECT DISTINCT d FROM Discussion d JOIN d.participants p " +
                                "WHERE p.id = :uid ORDER BY d.dateCreation DESC",
                        Discussion.class)
                .setParameter("uid", userId)
                .getResultList();
    }
}
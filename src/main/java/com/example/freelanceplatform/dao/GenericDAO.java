package com.example.freelanceplatform.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

public abstract class GenericDAO<T> {

    // EntityManager = l'outil qui communique avec MySQL via Hibernate
    @PersistenceContext(unitName = "FreelancePU")
    protected EntityManager em;

    private Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // Ajouter un enregistrement
    @Transactional
    public void save(T entity) {
        em.persist(entity);
    }

    // Modifier un enregistrement
    @Transactional
    public T update(T entity) {
        return em.merge(entity);
    }

    // Supprimer un enregistrement
    @Transactional
    public void delete(T entity) {
        em.remove(em.merge(entity));
    }

    // Trouver par ID
    public T findById(Long id) {
        return em.find(entityClass, id);
    }

    // Trouver tous les enregistrements
    public java.util.List<T> findAll() {
        return em.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass
        ).getResultList();
    }
}

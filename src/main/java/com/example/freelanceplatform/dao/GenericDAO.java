package com.example.freelanceplatform.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public abstract class GenericDAO<T> {

    @PersistenceContext(unitName = "FreelancePU")
    protected EntityManager em;

    private Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        em.persist(entity);
    }

    // ✅ On retourne l'entité managée
    public T update(T entity) {
        return em.merge(entity);
    }

    public void delete(T entity) {
        em.remove(em.merge(entity));
    }

    public T findById(Long id) {
        return em.find(entityClass, id);
    }

    public java.util.List<T> findAll() {
        return em.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass
        ).getResultList();
    }
}
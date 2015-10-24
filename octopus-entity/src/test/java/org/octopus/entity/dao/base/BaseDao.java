package org.octopus.entity.dao.base;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 *
 * Created by zzzhr on 2015-10-18.
 */
public interface BaseDao<K> extends Serializable {
    Class getEntityClass();

    DetachedCriteria getDetachedCriteria();

    @Transactional(readOnly = true)
    <T> T find(Class<T> clazz, Serializable id);

    @Transactional(readOnly = true)
    <T> Optional<T> findOptional(Class<T> clazz, Serializable id);

    @Transactional(readOnly = true)
    Optional<K> findOptional(Serializable id);

    @Transactional(readOnly = true)
    K find(Serializable id);

    @Transactional
    void delete(Serializable id);

    @Transactional
    <T> void delete(Class<T> clazz, Serializable id);

    @Transactional
    void delete(K entity);

    @Transactional
    <T> void deleteOther(T entity);

    @Transactional
    boolean exists(Serializable id);

    @Transactional
    boolean exists(Class clazz, Serializable id);

    @Transactional
    <T> T update(T entity);

    @Transactional
    <T> T save(T entity);

    @Transactional
    <T> T saveOrUpdate(T t);

    @Transactional
    void update(Serializable id, K e);
}

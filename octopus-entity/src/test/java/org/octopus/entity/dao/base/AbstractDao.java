package org.octopus.entity.dao.base;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.octopus.util.ReflectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * Base?
 * Created by zzzhr on 2015-10-17.
 */
@Slf4j
@Transactional
public abstract class AbstractDao<K> extends HibernateDaoSupport implements BaseDao<K> {
    private static final String VERSION_FLAG = "version";
    protected JdbcTemplate jdbcTemplate;
    protected HibernateTemplate hibernateTemplate;
    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    protected void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Autowired
    public void setDaoHibernateTemplate(HibernateTemplate hibernateTemplate) {
        super.setHibernateTemplate(hibernateTemplate);
        this.hibernateTemplate = hibernateTemplate;
    }
    protected final Class<K> clazz;
    protected Session getSession() {
        return hibernateTemplate.getSessionFactory().getCurrentSession();
    }
    protected AbstractDao(Class<K> clazz){
        this.clazz = clazz;
    }
    @Override
    public Class getEntityClass() {
        return clazz;
    }

    @Override
    public DetachedCriteria getDetachedCriteria() {
        return DetachedCriteria.forClass(getEntityClass());
    }

    @Override
    @Transactional(readOnly = true)

    /*
    * Single Query
    */
    public <T> T find(Class<T> clazz, Serializable id) {
        if(id == null){
            return null;
        }
        return getHibernateTemplate().get(clazz, id);
    }

    @Override
    @Transactional(readOnly = true)
    public  <T> Optional<T> findOptional(Class<T> clazz, Serializable id) {
        return Optional.ofNullable(find(clazz, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<K> findOptional(Serializable id){
        return Optional.ofNullable(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public K find(Serializable id){
        if(id == null){
            return null;
        }
        return getHibernateTemplate().get(clazz,id);
    }

    /*
    * Delete
    */

    @Override
    @Transactional
    public void delete(Serializable id) {
        if(id == null){
            return;
        }
        delete(clazz, id);
    }

    @Override
    @Transactional
    public  <T> void delete(Class<T> clazz, Serializable id) {
        T t = find(clazz, id);
        if(t == null){
            return;
        }
        hibernateTemplate.delete(t);
    }

    @Override
    @Transactional
    public void delete(K entity) {
        deleteOther(entity);
    }

    @Override
    @Transactional
    public <T> void deleteOther(T entity) {
        if(entity == null){
            return;
        }
        hibernateTemplate.delete(entity);
    }

    /*

    Exists

     */

    @Transactional
    @Override
    public boolean exists(Serializable id) {
        return findOptional(id).isPresent();
    }

    @Transactional
    @Override
    public boolean exists(Class clazz, Serializable id) {
        return findOptional(clazz,id).isPresent();
    }

    /*
    Update
     */

    @Override
    @Transactional
    public <T> T update(T entity) {
        hibernateTemplate.update(entity);
        return entity;
    }

    @Override
    @Transactional
    public <T> T save(T entity) {
        hibernateTemplate.save(entity);
        return entity;
    }

    @Override
    @Transactional
    public <T> T saveOrUpdate(T t) {
        hibernateTemplate.saveOrUpdate(t);
        return t;
    }

    @Override
    @Transactional
    public void update(Serializable id, K e) {
        K old = find(id);
        ReflectUtil.copyProperties(e, old, VERSION_FLAG);
        update(old);
    }

    /* /LIST QUERY/ */


}

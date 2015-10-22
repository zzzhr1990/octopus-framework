package org.octopus.entity.dao.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base?
 * Created by zzzhr on 2015-10-17.
 */
@Slf4j
@Transactional
public abstract class AbstractDao<K> extends HibernateDaoSupport implements BaseDao<K> {
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Autowired
    public void setDaoHibernateTemplate(HibernateTemplate hibernateTemplate) {
        super.setHibernateTemplate(hibernateTemplate);
    }
    protected final Class<K> clazz;
    protected AbstractDao(Class<K> clazz){
        this.clazz = clazz;
    }
}

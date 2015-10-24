package org.octopus.entity.model;

import javafx.beans.binding.MapBinding;
import org.octopus.util.ReflectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryCondition.
 * Define Order,Fields, etc
 * Define query condition in order to convert DC to hibernate.
 * Created by zzzhr on 2015-10-19.
 */
public abstract class QueryCondition<T> {

    protected final Class tClass;

    public QueryCondition(){
        tClass = ReflectUtil.getParameterizedClass(this.getClass());
    }

    protected OrderByField[] orderByFields;

    public OrderByField[] getOrderByFields() {
        return orderByFields;
    }

    public void setOrderByFields(OrderByField[] orderByFields) {
        this.orderByFields = orderByFields;
    }


}

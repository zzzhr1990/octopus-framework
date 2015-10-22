package org.octopus.entity.model;

/**
 * Define a order by field is desc or asc.
 * Created by zzzhr on 2015-10-19.
 */
public class OrderByField {
    private String field;
    private boolean desc;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public boolean issc() {
        return !desc;
    }

    public void setAsc(boolean asc) {
        this.desc = !asc;
    }
}

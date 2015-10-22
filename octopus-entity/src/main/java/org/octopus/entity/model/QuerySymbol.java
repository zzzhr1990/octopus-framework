package org.octopus.entity.model;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.InExpression;
import org.hibernate.criterion.SimpleExpression;

/**
 * （From FangZhiYuan's Anitama Framework）
 * Created by fangzy on 2015/3/30.
 */
public enum QuerySymbol {

    EQ(SimpleExpression.class, "="),
    IN(InExpression.class, null),
    NOT(SimpleExpression.class, "<>"),
    GT(SimpleExpression.class, ">"),
    GTE(SimpleExpression.class, ">="),
    LT(SimpleExpression.class, "<"),
    LTE(SimpleExpression.class, "<=");

    private final Class<? extends Criterion> expressionClass;

    private final String value;

    QuerySymbol(Class<? extends Criterion> expressionClass, String value) {
        this.expressionClass = expressionClass;
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public Class<? extends Criterion> expression() {
        return expressionClass;
    }
}

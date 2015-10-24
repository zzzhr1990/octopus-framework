package org.octopus.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * Spring Util
 * Created by zzzhr on 2015-10-17.
 */
@Component
public class SpringUtil implements ApplicationContextAware{
    private static final StandardEvaluationContext CONTEXT = new StandardEvaluationContext();

    private static volatile ApplicationContext ac;

    private static volatile String[] profiles;

    public static <T> T getBean(String name, Class<T> requiredType) {
        return ac.getBean(name, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return ac.getBean(requiredType);
    }

    public static <T> T parseSPEL(String expression, Class<T> clz) throws EvaluationException {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(expression);
        return exp.getValue(CONTEXT, clz);
    }

    public static boolean isProfile(String profile) {
        boolean res = false;
        if (profile != null) {
            for (String active : profiles) {
                if (active.equals(profile)) {
                    res = true;
                    break;
                }
            }
        }
        return res;
    }

    public static ApplicationContext getContext() {
        return ac;
    }

    /*
    public static ApplicationEventPublisher getSyncPublisher() {
        return getBean(TransactionalSyncNotifier.class);
    }
    */

    public static boolean isDebug() {
        return isProfile("dev");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (ac != null) {
            return;
        }
        synchronized (SpringUtil.class) {
            if (ac != null) {
                return;
            }
            ac = applicationContext;
            Environment env = applicationContext.getEnvironment();
            profiles = env.getActiveProfiles();
        }
    }
}

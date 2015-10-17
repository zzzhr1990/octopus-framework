package org.octopus.util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.persistence.Id;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REF
 * Created by zzzhr on 2015-10-17.
 */
@Slf4j
public class ReflectUtil {
    private static final Map<Class, Field[]> fieldsMap = new ConcurrentHashMap<>();

    private static final Map<String, Method> commonReadMap = new ConcurrentHashMap<>();

    public static <T> String getIdProperty(Class<T> clazz) {
        return getPropertyByAnnotation(clazz, Id.class);
    }

    public static <T> String getIdByHibernate(Class<T> clazz) {
        SessionFactory sessionFactory = SpringUtil.getBean(SessionFactory.class);
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(clazz);
        return classMetadata.getIdentifierPropertyName();
        //org.springframework.orm.hibernate5.support.
    }

    public static <T> String getPropertyByAnnotation(Class<T> clazz, Class<? extends Annotation> annClazz) {
        Field[] fields = getFields(clazz, true);
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annClazz);
            if (annotation != null) {
                return field.getName();
            }
        }
        return null;
    }

    private static <T> Field[] getFields(Class<T> clazz, boolean fetchSuper) {
        Field[] fields = fieldsMap.get(clazz);
        if (fields == null) {
            List<Field> fieldList = new ArrayList<>();
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            if (fetchSuper) {
                Class<? super T> superClass = clazz.getSuperclass();
                while (superClass != null) {
                    fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
                    superClass = superClass.getSuperclass();
                }
            }
            fields = fieldList.toArray(new Field[fieldList.size()]);
            fieldsMap.putIfAbsent(clazz, fields);
        }
        return fields;
    }


    public static <T> Map<String, Object> getNotNullProperties(T t) {
        Map<String, Object> valueMap = new ConcurrentHashMap<>();
        Class<?> tClass = t.getClass();
        Field[] fields = getFields(tClass, false);
        Arrays.stream(fields).parallel().forEach(e -> {
            String fieldName = e.getName();
            Method readMethod = commonReadMap.get(tClass.getName() + "#" + fieldName);
            if (readMethod == null) {
                PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(tClass, fieldName);
                if (pd == null) {
                    return;
                }
                readMethod = pd.getReadMethod();
                commonReadMap.putIfAbsent(tClass.getName() + "#" + fieldName, readMethod);
            }
            Object value = null;
            try {
                value = readMethod.invoke(t);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                log.error("An unexpected error occurred.", ex);
            }
            if (value != null) {
                valueMap.putIfAbsent(fieldName, value);
            }
        });
        return valueMap;
    }

    public static <T, R extends Annotation> Map<String, R> getPropertiesByAnnotation(Class<T> clazz, Class<R> annClazz) {
        Field[] fields = getFields(clazz, false);
        Map<String, R> valueMap = new ConcurrentHashMap<>();
        Arrays.stream(fields).parallel().forEach(e -> {
            R r = e.getAnnotation(annClazz);
            if (r != null) {
                valueMap.putIfAbsent(e.getName(), r);
            }
        });
        return valueMap;
    }

    public static <T> boolean hasProperty(String property, Class<T> clazz) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, property);
        return pd != null;
    }

    public static void copyProperties(Object source, Object target, String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (value == null) {
                                continue;
                            }
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    public static Class getParameterizedClass(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            return getParameterizedClass(clazz.getSuperclass());
        }
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        if (types.length == 0) {
            return Object.class;
        }
        if (types[0] instanceof Class) {
            return (Class) types[0];
        }
        return Object.class;
    }

    @SuppressWarnings("unchecked")
    public static <T> T instantiate(Class<? extends T> clazz, Object... values) {
        Constructor<? extends T>[] constructors = (Constructor<? extends T>[]) clazz.getDeclaredConstructors();
        T t = null;
        try {
            for (Constructor constructor : constructors) {
                constructor.setAccessible(true);
                if (constructor.getParameterCount() == 0 && values == null) {
                    t = (T) constructor.newInstance();
                    break;
                }
                if (constructor.getParameterCount() == values.length) {
                    t = (T) constructor.newInstance(values);
                    break;
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <M, T extends Annotation> Optional<T> getClassAnnotation(M m, Class<T> annClazz) {
        return Optional.ofNullable(m.getClass().getAnnotation(annClazz));
    }

    public static <E> void setPropertyByHibernate(E e, String field, Object object) {
        if(object == null){
            return;
        }
        SessionFactory sessionFactory = SpringUtil.getBean(SessionFactory.class);
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(e.getClass());
        classMetadata.setPropertyValue(e,field,object);
    }
}

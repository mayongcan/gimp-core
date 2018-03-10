package com.gimplatform.core.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.beust.jcommander.internal.Maps;

import net.sf.cglib.beans.BeanMap;

/**
 * bean工具类
 * @author zzd
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    private static final Logger logger = LogManager.getLogger(BeanUtils.class);

    public static <T> void mergeBean(T origin, T destination) {
        if (origin == null || destination == null)
            return;
        if (!origin.getClass().equals(destination.getClass()))
            return;

        Field[] fields = origin.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                fields[i].setAccessible(true);
                Object value = fields[i].get(origin);
                // final类函数不用重新赋值
                if (null != value && !Modifier.isFinal(fields[i].getModifiers())) {
                    fields[i].set(destination, value);
                }
                fields[i].setAccessible(false);
            } catch (Exception e) {
                if (fields[i].isAccessible())
                    fields[i].setAccessible(false);
                logger.error(e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> describe(Object obj) {
        if (obj instanceof Map)
            return (Map<String, Object>) obj;
        Map<String, Object> map = new HashMap<String, Object>();
        PropertyDescriptor[] descriptors = getPropertyDescriptors(obj.getClass());
        for (int i = 0; i < descriptors.length; i++) {
            String name = descriptors[i].getName();
            Method readMethod = descriptors[i].getReadMethod();
            if (readMethod != null) {
                try {
                    map.put(name, readMethod.invoke(obj, new Object[] {}));
                } catch (Exception e) {
                    logger.error("error get property value,name:" + name + " on bean:" + obj, e);
                }
            }
        }
        return map;
    }

    @SuppressWarnings("rawtypes")
    public static PropertyDescriptor[] getPropertyDescriptors(Class beanClass) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            return (new PropertyDescriptor[0]);
        }
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[0];
        }
        return descriptors;
    }

    @SuppressWarnings("rawtypes")
    public static PropertyDescriptor getPropertyDescriptors(Class beanClass, String name) {
        for (PropertyDescriptor pd : getPropertyDescriptors(beanClass)) {
            if (pd.getName().equals(name)) {
                return pd;
            }
        }
        return null;
    }

    public static void copyProperties(Object target, Object source) {
        copyPropertiesMethod(target, source, null);
    }

    @SuppressWarnings("rawtypes")
    public static void copyPropertiesMethod(Object target, Object source, String[] ignoreProperties) {
        if (target instanceof Map) {
            throw new UnsupportedOperationException("target is Map unsuported");
        }

        PropertyDescriptor[] targetPds = getPropertyDescriptors(target.getClass());
        List ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (int i = 0; i < targetPds.length; i++) {
            PropertyDescriptor targetPd = targetPds[i];
            if (targetPd.getWriteMethod() != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                try {
                    if (source instanceof Map) {
                        Map map = (Map) source;
                        if (map.containsKey(targetPd.getName())) {
                            Object value = map.get(targetPd.getName());
                            setProperty(target, targetPd, value);
                        }
                    } else {
                        PropertyDescriptor sourcePd = getPropertyDescriptors(source.getClass(), targetPd.getName());
                        if (sourcePd != null && sourcePd.getReadMethod() != null) {
                            Object value = getProperty(source, sourcePd);
                            setProperty(target, targetPd, value);
                        }
                    }
                } catch (Throwable ex) {
                    throw new IllegalArgumentException("Could not copy properties on:" + targetPd.getDisplayName(), ex);
                }
            }
        }
    }

    /**
     * 将对象装换为map
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将对象装换为map
     * @param bean
     * @return
     */
    public static <T> Map<String, String> beanToMapStr(T bean) {
        Map<String, String> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", StringUtils.toString(beanMap.get(key), ""));
            }
        }
        return map;
    }

    // /**
    // * 将map装换为javabean对象
    // * @param map
    // * @param bean
    // * @return
    // */
    // public static <T> T mapToBean(Map<String, Object> map,T bean) {
    // BeanMap beanMap = BeanMap.create(bean);
    // beanMap.putAll(map);
    // return bean;
    // }
    //
    // /**
    // * 将List<T>转换为List<Map<String, Object>>
    // * @param objList
    // * @return
    // * @throws JsonGenerationException
    // * @throws JsonMappingException
    // * @throws IOException
    // */
    // public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
    // List<Map<String, Object>> list = Lists.newArrayList();
    // if (objList != null && objList.size() > 0) {
    // Map<String, Object> map = null;
    // T bean = null;
    // for (int i = 0,size = objList.size(); i < size; i++) {
    // bean = objList.get(i);
    // map = beanToMap(bean);
    // list.add(map);
    // }
    // }
    // return list;
    // }
    //
    // /**
    // * 将List<Map<String,Object>>转换为List<T>
    // * @param maps
    // * @param clazz
    // * @return
    // * @throws InstantiationException
    // * @throws IllegalAccessException
    // */
    // public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps,Class<T> clazz) throws InstantiationException, IllegalAccessException {
    // List<T> list = Lists.newArrayList();
    // if (maps != null && maps.size() > 0) {
    // Map<String, Object> map = null;
    // T bean = null;
    // for (int i = 0,size = maps.size(); i < size; i++) {
    // map = maps.get(i);
    // bean = clazz.newInstance();
    // mapToBean(map, bean);
    // list.add(bean);
    // }
    // }
    // return list;
    // }

    private static Object getProperty(Object source, PropertyDescriptor sourcePd) throws IllegalAccessException, InvocationTargetException {
        Method readMethod = sourcePd.getReadMethod();
        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
            readMethod.setAccessible(true);
        }
        Object value = readMethod.invoke(source, new Object[0]);
        return value;
    }

    private static void setProperty(Object target, PropertyDescriptor targetPd, Object value) throws IllegalAccessException, InvocationTargetException {
        Method writeMethod = targetPd.getWriteMethod();
        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
            writeMethod.setAccessible(true);
        }
        writeMethod.invoke(target, new Object[] { convert(value, writeMethod.getParameterTypes()[0]) });
    }

    private static Object convert(Object value, Class<?> targetType) {
        if (value == null)
            return null;
        if (targetType == String.class) {
            return value.toString();
        } else {
            return convert(value.toString(), targetType);
        }
    }

    private static Object convert(String value, Class<?> targetType) {
        if (targetType == Byte.class || targetType == byte.class) {
            return new Byte(value);
        }
        if (targetType == Short.class || targetType == short.class) {
            return new Short(value);
        }
        if (targetType == Integer.class || targetType == int.class) {
            return new Integer(value);
        }
        if (targetType == Long.class || targetType == long.class) {
            return new Long(value);
        }
        if (targetType == Float.class || targetType == float.class) {
            return new Float(value);
        }
        if (targetType == Double.class || targetType == double.class) {
            return new Double(value);
        }
        if (targetType == BigDecimal.class) {
            return new BigDecimal(value);
        }
        if (targetType == BigInteger.class) {
            return BigInteger.valueOf(Long.parseLong(value));
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return new Boolean(value);
        }
        if (targetType == boolean.class) {
            return new Boolean(value);
        }
        if (targetType == char.class) {
            return value.charAt(0);
        }
        if (DateUtils.isDateType(targetType)) {
            return DateUtils.parseDate(value, targetType, "yyyyMMdd", "yyyy-MM-dd", "yyyyMMddHHmmSS", "yyyy-MM-dd HH:mm:ss", "HH:mm:ss");
        }

        throw new IllegalArgumentException("cannot convert value:" + value + " to targetType:" + targetType);
    }

    // /**
    // * bean to map
    // * @param ojt
    // * @return
    // */
    // public static Map<String, Object> beanToMap(Object ojt) {
    // Class<?> cls = ojt.getClass();
    // Field[] field = cls.getDeclaredFields();
    //
    // HashMap<String, Object> mapbean = new HashMap<String, Object>();
    // for(int i=0;i<field.length;i++){
    // Field f = field[i];
    // f.setAccessible(true);
    // try {
    // mapbean.put(f.getName(), f.get(cls));
    // } catch (IllegalArgumentException e) {
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // e.printStackTrace();
    // }
    // }
    // return mapbean;
    // }

    /**
     * HashMap转换成JavaBean
     * @param map
     * @param cls
     * @return
     */
    public static Object mapToBean(Map<?, ?> map, Class<?> cls) {
        Object obj = null;
        try {
            obj = cls.newInstance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        // 取出bean里的所有方法
        Method[] methods = cls.getMethods();
        for (int i = 0; i < methods.length; i++) {
            // 取方法名
            String method = methods[i].getName();
            // 取出方法的类型
            Class<?>[] cc = methods[i].getParameterTypes();
            if (cc.length != 1)
                continue;
            // 如果方法名没有以set开头的则退出本次for
            if (!method.startsWith("set"))
                continue;
            // 类型
            String type = cc[0].getSimpleName();
            try {
                //
                Object value = method.substring(3, 4).toLowerCase().concat(method.substring(4));
                // 如果map里有该key
                if (map.containsKey(value)) {
                    // 调用其底层方法
                    setValue(type, map.get(value), i, methods, obj);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return obj;
    }

    /**
     * 调用底层方法设置值
     * @param type
     * @param value
     * @param i
     * @param method
     * @param bean
     * @throws Exception
     */
    private static void setValue(String type, Object value, int i, Method[] method, Object bean) throws Exception {
        if (value != null && !value.equals("")) {
            try {
                if (type.equals("String")) {
                    // 第一个参数:从中调用基础方法的对象 第二个参数:用于方法调用的参数
                    method[i].invoke(bean, new Object[] { value });
                } else if (type.equals("int") || type.equals("Integer")) {
                    method[i].invoke(bean, new Object[] { new Integer("" + value) });
                } else if (type.equals("BigDecimal")) {
                    method[i].invoke(bean, new Object[] { new BigDecimal((String) value) });
                } else if (type.equals("long") || type.equals("Long")) {
                    method[i].invoke(bean, new Object[] { new Long("" + value) });
                } else if (type.equals("boolean") || type.equals("Boolean")) {
                    method[i].invoke(bean, new Object[] { Boolean.valueOf("" + value) });
                } else if (type.equals("Date")) {
                    Date date = null;
                    if (value.getClass().getName().equals("java.util.Date")) {
                        date = (Date) value;
                    } else {
                        // 根据文件内的格式不同修改，时间格式太多在此不做通用格式处理。
                        if (value.toString().length() == 19) {
                            String format = "yyyy-MM-dd HH:mm:ss";
                            date = parseDateTime("" + value, format);
                        } else if (value.toString().length() == 17) {
                            String format = "yyyy-MM-dd HHmmss";
                            date = parseDateTime("" + value, format);
                        } else if (value.toString().length() == 10) {
                            String format = "yyyy-MM-dd";
                            date = parseDateTime("" + value, format);
                        } else if (value.toString().length() == 8) {
                            String format = "yyyyMMdd";
                            date = parseDateTime("" + value, format);
                        } else if (value.toString().length() == 14) {
                            String format = "yyyyMMddHHmmss";
                            date = parseDateTime("" + value, format);
                        } else if (value.toString().length() == 6) {
                            String format = "HHmmss";
                            date = parseDateTime("" + value, format);
                        }
                    }
                    if (date != null) {
                        method[i].invoke(bean, new Object[] { date });
                    }
                } else if (type.equals("byte[]")) {
                    method[i].invoke(bean, new Object[] { new String(value + "").getBytes() });
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * 日期格式转换
     * @param dateValue
     * @param format
     * @return
     */
    private static Date parseDateTime(String dateValue, String format) {
        SimpleDateFormat obj = new SimpleDateFormat(format);
        try {
            return obj.parse(dateValue);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}

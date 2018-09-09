package com.gimplatform.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 对象操作工具类, 继承org.apache.commons.lang3.ObjectUtils类
 * @author zzd
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    /**
     * 注解到对象复制，只复制能匹配上的方法。
     * @param annotation
     * @param object
     */
    public static void annotationToObject(Object annotation, Object object) {
        if (annotation != null) {
            Class<?> annotationClass = annotation.getClass();
            if (null == object) {
                return;
            }
            Class<?> objectClass = object.getClass();
            for (Method m : objectClass.getMethods()) {
                if (StringUtils.startsWith(m.getName(), "set")) {
                    try {
                        String s = StringUtils.uncapitalize(StringUtils.substring(m.getName(), 3));
                        Object obj = annotationClass.getMethod(s).invoke(annotation);
                        if (obj != null && !"".equals(obj.toString())) {
                            m.invoke(object, obj);
                        }
                    } catch (Exception e) {
                        // 忽略所有设置失败方法
                    }
                }
            }
        }
    }

    /**
     * 序列化对象
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            if (object != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化对象
     * @param bytes
     * @return
     */
    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            if (bytes != null && bytes.length > 0) {
                bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    

    @SuppressWarnings("rawtypes")
    private static final List LEAVES = Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class,
            Integer.class, Long.class, Float.class, Double.class, Void.class,
            String.class,Date.class);
    private static String delimitor = "\n";
    /**
     * 递归对象打印
     * @param o
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String toStringRecursive(Object o){
        if (o == null)
            return "null"+delimitor;
        
        if (LEAVES.contains(o.getClass()))
            return o.toString()+delimitor;
        
        if(o.getClass().isArray()){
            Object[]  oa = (Object[]) o;
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            for(int i =0;i<oa.length;i++)
            sb.append(toStringRecursive(oa[i]));    
            sb.append("]"+delimitor);
            return sb.toString();
        }
        
        new ArrayList().getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getSimpleName()).append(": [");
        for (Field f : o.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()))
                continue;
            f.setAccessible(true);
            sb.append(f.getName()).append(": ");
            try {
                sb.append(toStringRecursive(f.get(o))).append(" ");
            } catch (IllegalArgumentException e) {              
                e.printStackTrace();
            } catch (IllegalAccessException e) {                
                e.printStackTrace();
            }
        }
        sb.append("]"+delimitor);
        return sb.toString();
    }
}

package com.senjyouhara.core.collections;


import com.senjyouhara.core.date.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * 获取map中值的工具类,自动进行类型转换
 *
 * @author DT_panda
 */
@Log4j2
public class MapUtils {

    public static String getString(String key, Map<String, Object> map) {
        if (map == null || key == null)
            throw new IllegalArgumentException();
        if (!map.containsKey(key))
            return null;
        Object value = map.get(key);
        if (value == null)
            return null;
        return value.toString();
    }

    public static Integer getInteger(String key, Map<String, Object> map) {
        if (map == null || key == null)
            throw new IllegalArgumentException();
        if (!map.containsKey(key))
            return null;
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Integer)
            return (Integer) value;
        if (value instanceof String)
            return Integer.valueOf((String) value);
        //Date 不支持变成为date类型
        if (value instanceof Date)
            throw new ClassCastException();
        if (value instanceof Number)
            return ((Number) value).intValue();
        throw new ClassCastException();
    }

    public static Long getLong(String key, Map<String, Object> map) {
        if (map == null || key == null)
            throw new IllegalArgumentException();
        if (!map.containsKey(key))
            return null;
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Long)
            return (Long) value;
        if (value instanceof Number)
            return ((Number) value).longValue();
        if (value instanceof String)
            return Long.valueOf((String) value);
        if (value instanceof Date) {
            return (((Date) value).getTime());
        }
        if (value instanceof java.sql.Time) {
            return ((java.sql.Time) value).getTime();
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).getTime();
        }

        throw new ClassCastException();
    }

    public static Double getDouble(String key, Map<String, Object> map) {
        if (map == null || key == null)
            throw new IllegalArgumentException();
        if (!map.containsKey(key))
            return null;
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Double)
            return (Double) value;
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        if (value instanceof String)
            return Double.valueOf((String) value);
        throw new ClassCastException();
    }

    public static BigDecimal getBigDecimal(String key, Map<String, Object> map) {
        if (map == null || key == null)
            throw new IllegalArgumentException();
        if (!map.containsKey(key))
            return null;
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof BigDecimal)
            return (BigDecimal) value;
        if (value instanceof Integer)
            return new BigDecimal((Integer) value);
        if (value instanceof Short)
            return new BigDecimal((Short) value);
        if (value instanceof Byte)
            return new BigDecimal((Byte) value);
        if (value instanceof Long)
            return new BigDecimal((Long) value);
        if (value instanceof Float)
            return new BigDecimal((Float) value);
        if (value instanceof Double)
            return new BigDecimal((Double) value);
        if (value instanceof Date) {
            return new BigDecimal(((Date) value).getTime());
        }
        if (value instanceof java.sql.Time) {
            return new BigDecimal(((java.sql.Time) value).getTime());
        }
        if (value instanceof Timestamp) {
            return new BigDecimal(((Timestamp) value).getTime());
        }
        if (value instanceof String) {
            if (!StringUtils.isEmpty((String) value))
                return new BigDecimal((String) value);
            else
                return null;
        }
        throw new ClassCastException();
    }

    /**
     * 将bean转化为map
     *
     * @param bean
     * @return
     */
    public static Map<String, Object> getMap(Object bean) {
        return beanToMap(bean);
    }

    /**
     * 将map中key为likeKey的value前后加上字符'%'，用于like查询
     *
     * @param map
     * @param likeKey
     */
    public static void toLikeValue(Map<String, Object> map, String... likeKey) {
        if (ArrayUtils.isEmpty(likeKey))
            return;
        for (String key : likeKey) {
            if (map.containsKey(key))
                map.put(key, "%" + map.get(key) + "%");
        }
    }

    /**
     * 获取日期
     *
     * @param key
     * @param map
     * @return
     */
    public static LocalDateTime getDate(String key, Map<String, Object> map) {
        if (map == null || key == null)
            throw new IllegalArgumentException();
        if (!map.containsKey(key))
            return null;
        Object value = map.get(key);
        if (value == null)
            return null;
        else {
            if (value instanceof Date) {
                return DateUtils.dateToLocalDate((Date) value);
            } else if (value instanceof LocalDateTime) {
                return (LocalDateTime) value;
            }
        }
        return null;
    }

    /**
     * 如果value不为空 ，则放到map中
     *
     * @param map
     * @param key
     * @param value
     */
    public static void putIfValueNotNull(Map<String, Object> map, String key, Object value) {
        Assert.notNull(map, "Parameter map cannot be null");
        Assert.hasText(key, "Parameter key cannot be empty");
        if (value != null)
            map.put(key, value);
    }

    /**
     * 如果value不为空 ，则放到map中
     *
     * @param map
     * @param key
     * @param value
     */
    public static void putIfValueNotEmpty(Map<String, Object> map, String key, String value) {
        Assert.notNull(map, "Parameter map cannot be null");
        Assert.hasText(key, "Parameter key cannot be empty");
        if (!StringUtils.isEmpty(value))
            map.put(key, value);
    }

    /**
     * 将map中指定的key的value值进行处理
     *
     * @param key
     * @param map
     * @param consumer
     */
    public static  void convertMapValuePattern(String key, Map<String, Object> map, BiConsumer<String, Map<String, Object>> consumer) {
        Assert.notNull(consumer, "Parameter consumer cannot be null");
        Assert.notNull(map, "Parameter map cannot be null");
        Assert.hasText(key, "Parameter key cannot be empty");
        consumer.accept(key, map);
    }

    /**
     * 将javabean实体类转为map类型，然后返回一个map类型的值
     *
     * @return Map
     */
    public static <T>  Map<String, Object> beanToMap(T beanObj) {
        Map<String, Object> params = new HashMap<String, Object>(0);
        if (beanObj == null) {
            throw new RuntimeException("beanObj is not null");
        }
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(beanObj);
            for (PropertyDescriptor descriptor : descriptors) {
                String name = descriptor.getName();
                if (!"class".equals(name)) {
                    params.put(name, propertyUtilsBean.getNestedProperty(beanObj, name));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 实体对象转成Map
     * @param obj 实体对象
     * @return
     */
    public  static <T> Map<String, T> objectToMap(Object obj) {
        Map<String, T> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), (T) field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Map转成实体对象
     * @param map map实体对象包含属性
     * @param clazz 实体对象类型
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        T obj = null;
        try {
            obj = clazz.newInstance();
            Class<?> claxx = clazz;
            Field[] fields = obj.getClass().getDeclaredFields();
            HashSet<Field> fieldList = new HashSet<>(Arrays.asList(fields));
            while (claxx !=null && !claxx.getName().toLowerCase().equals("java.lang.object")) {
                fieldList.addAll(Arrays.asList(claxx.getDeclaredFields()));
                claxx = claxx.getSuperclass(); //得到父类,然后赋给自己
            }

            for (Field field : fieldList) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String convertMap2Xml(Map<Object, Object> paraMap) {
        StringBuilder xmlStr = new StringBuilder();
        if (paraMap != null) {
            xmlStr.append("<xml>");
            Set<Object> keySet = paraMap.keySet();
            Iterator<Object> keyIte = keySet.iterator();
            while (keyIte.hasNext()) {
                String key = (String) keyIte.next();
                String val = String.valueOf(paraMap.get(key));
                xmlStr.append("<");
                xmlStr.append(key);
                xmlStr.append(">");
                xmlStr.append(val);
                xmlStr.append("</");
                xmlStr.append(key);
                xmlStr.append(">");
            }
            xmlStr.append("</xml>");
        }
        return xmlStr.toString();
    }
}


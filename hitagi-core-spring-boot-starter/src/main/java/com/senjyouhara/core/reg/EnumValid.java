package com.senjyouhara.core.reg;

import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;

@Log4j2
public class EnumValid {


    public static boolean valueIsEnumValue(String value,Class<? extends Enum> enumClass){
        return valueIsEnumValue(value,"value",enumClass);
    }

    /**
     *  是否为枚举value值
     * @param value
     * @param enumField
     * @param enumClass
     * @return
     */
    public static boolean valueIsEnumValue(String value,String enumField,Class<? extends Enum> enumClass){

        log.info("start:{},{},{}",value,enumField,enumClass);

            Object[] objects = enumClass.getEnumConstants();
            Field field = getFieldByName(enumClass, enumField);

            log.info("field:{}",field);

            if (isTrueEnumValue(objects, field, value)) {
                return true;
            }

        return false;
    }

    private static Field getFieldByName(Class<? extends Enum> enumClass, String fieldname) {
        Field field = null;
        try {
            field = enumClass.getDeclaredField(fieldname);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    private static boolean isTrueEnumValue(Object[] enumObjects, Field field, Object value) {
        String valueStr = null;
        if(value instanceof String || value instanceof Integer){
            valueStr = value.toString();
        } else {
            return false;
        }
        log.info("valueStr:{}",valueStr);

        for (Object enumObject : enumObjects) {
            String fieldStr = getFieldValue(enumObject, field);
            log.info("fieldStr:{}",fieldStr);
            if(valueStr.equals(fieldStr)){
                return true;
            }
        }
        return false;
    }

    private static String getFieldValue(Object instance, Field field){
        Object fieldValue = null;
        try {
            log.info("field:{},{}",instance,field);

            fieldValue = field.get(instance);
            log.info("fieldValue:{}",fieldValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValue.toString();
    }
}

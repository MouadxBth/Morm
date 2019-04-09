package me.espada.utils;

public class Utils {

    public static boolean isPrimitiveOrString(Class<?> type) {
        return type.isPrimitive()
                || type == Byte.class
                || type == Short.class
                || type == Integer.class
                || type == Long.class
                || type == Float.class
                || type == Double.class
                || type == Boolean.class
                || type == Character.class
                || type == String.class;
    }

}

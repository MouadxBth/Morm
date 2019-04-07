package me.espada.serialize;

public interface iSerializable {
     String serialize(Object in);
     Object deserialize(String in, Class<?> targetClass);
}

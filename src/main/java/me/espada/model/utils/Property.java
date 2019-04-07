package me.espada.model.utils;

import me.espada.serialize.iSerializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Property {

    private String name;

    private Method readMethod;

    private Method writeMethod;

    private Field field;

    private Class<?> dataType;

    private boolean isGenerated;

    private boolean isPrimaryKey;

    private boolean isEnumField;

    private Class<Enum> enumClass;

    private EnumType enumType;

    private Column columnAnnotation;

    private iSerializable serializer;

    private AnnotatedElement annotatdElement;

    public void setName(String name) {
        this.name = name;
    }

    public void setReadMethod(Method readMethod) {
        this.readMethod = readMethod;
    }

    public void setWriteMethod(Method writeMethod) {
        this.writeMethod = writeMethod;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public void setGenerated(boolean generated) {
        isGenerated = generated;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public void setEnumField(boolean enumField) {
        isEnumField = enumField;
    }

    public void setEnumClass(Class<Enum> enumClass) {
        this.enumClass = enumClass;
    }

    public void setEnumType(EnumType enumType) {
        this.enumType = enumType;
    }

    public void setColumnAnnotation(Column columnAnnotation) {
        this.columnAnnotation = columnAnnotation;
    }

    public void setSerializer(iSerializable serializer) {
        this.serializer = serializer;
    }

    public String getName() {
        return name;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isEnumField() {
        return isEnumField;
    }

    public Class<Enum> getEnumClass() {
        return enumClass;
    }

    public EnumType getEnumType() {
        return enumType;
    }

    public Column getColumnAnnotation() {
        return columnAnnotation;
    }

    public iSerializable getSerializer() {
        return serializer;
    }

    public AnnotatedElement getAnnotatdElement() {
        return annotatdElement;
    }

    public void setAnnotatdElement(AnnotatedElement annotatdElement) {
        this.annotatdElement = annotatdElement;
    }
}

package me.espada.standard;

import me.espada.model.ColumnOrder;
import me.espada.model.utils.Property;
import me.espada.model.utils.PropertyDescriptorMapper;
import me.espada.model.utils.PropertyFieldMapper;
import me.espada.serialize.iSerializer;
import me.espada.utils.GenericBuilder;

import javax.persistence.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StandardModelMaker {

    private StandardModel standardModel;

    public StandardModelMaker(Class<?> type) {
        standardModel = GenericBuilder.of(StandardModel::new).with(StandardModel::setType, type).build();
        if (Map.class.isAssignableFrom(type)) {
            //leave properties empty
        } else {
            List<Property> props = scanForProperties(type);

            ColumnOrder colOrder = type.getAnnotation(ColumnOrder.class);
            if (colOrder != null) {
                // reorder the properties
                String [] cols = colOrder.value();
                List<Property> reordered = new ArrayList<>();
                for (int i = 0; i < cols.length; i++) {
                    for (Property prop: props) {
                        if (prop.getName().equals(cols[i])) {
                            reordered.add(prop);
                            break;
                        }
                    }
                }
                // props not in the cols list are ignored
                props = reordered;
            }

            for (Property prop: props) {
                standardModel.getPropertyMap().put(prop.getName(), prop);
            }
        }

        Table annot = type.getAnnotation(Table.class);
        if (annot != null) {
            if (!annot.schema().isEmpty()) {
                standardModel.setTable(annot.schema() + "." + annot.name());
            }
            else {
                standardModel.setTable(annot.name());
            }
        } else {
            standardModel.setTable(type.getSimpleName());
        }
    }

    public StandardModel build() {
        standardModel.setProperties(scanForProperties(standardModel.getType()));

        return standardModel;
    }

    private List<Property> scanForProperties(Class<?> clazz) {

        PropertyFieldMapper propertyFieldMapper = new PropertyFieldMapper();
        PropertyDescriptorMapper propertyDescriptorMapper = new PropertyDescriptorMapper();

        List<Property> properties = Stream.of(clazz.getFields())
                .filter(this::isFieldValid)
                .map(propertyFieldMapper::map)
                .collect(Collectors.toList());

        properties.addAll(
                Stream.of(Objects.requireNonNull(getWrappedBeanInfo(clazz, Object.class)).getPropertyDescriptors())
                        .filter(this::isReadMethodValid)
                        .map(propertyDescriptorMapper::map)
                        .collect(Collectors.toList()));

        properties.forEach(this::applyAnnotations);

        return properties;
    }

    /** Applies the annotations on the field or getter method to the property.
     * @param property the target property
     */
    @SuppressWarnings("unchecked")
    private void applyAnnotations(Property property) {

        AnnotatedElement annotatedElement = property.getAnnotatdElement();
        Column column = annotatedElement.getAnnotation(Column.class);

        if (column != null) {
            String name = column.name().trim();
            if (name.length() > 0) {
                property.setName(name);
            }
            property.setColumnAnnotation(column);
        }

        if (annotatedElement.getAnnotation(Id.class) != null) {
            property.setPrimaryKey(true);
            standardModel.setPrimaryKeyName(property.getName());
        }

        if (annotatedElement.getAnnotation(GeneratedValue.class) != null) {
            standardModel.setGeneratedColumnName(property.getName());
            property.setGenerated(true);
        }

        if (property.getDataType().isEnum()) {
            property.setEnumField(true);
            property.setEnumClass((Class<Enum>) property.getDataType());
            /* We default to STRING enum type. Can be overriden with @Enumerated annotation */
            property.setEnumType(EnumType.STRING);
            if (annotatedElement.getAnnotation(Enumerated.class) != null) {
                property.setEnumType(annotatedElement.getAnnotation(Enumerated.class).value());
            }
        }

        iSerializer serializer = annotatedElement.getAnnotation(iSerializer.class);
        if (serializer != null) {
            try {
                property.setSerializer(serializer.value().getDeclaredConstructor().newInstance());
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private BeanInfo getWrappedBeanInfo(Class<?> beanClass, Class<?> stopClass) {
        try {
            return Introspector.getBeanInfo(beanClass, stopClass);
        } catch (IntrospectionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isReadMethodValid(PropertyDescriptor propertyDescriptor) {
        return !(isReadMethodNull(propertyDescriptor) || isReadMethodTransient(propertyDescriptor));
    }

    private boolean isReadMethodNull(PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.getReadMethod() == null;
    }

    private boolean isReadMethodTransient(PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.getReadMethod().getAnnotation(Transient.class) != null;
    }

    private boolean isFieldValid(Field field) {
        return isFieldPublic(field) && !(isFieldTransient(field) || isFieldStatic(field) || isFieldFinal(field));
    }

    private boolean isFieldTransient(Field field) {
        return field.getAnnotation(Transient.class) != null;
    }

    private boolean isFieldPublic(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }

    private boolean isFieldStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    private boolean isFieldFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }


}

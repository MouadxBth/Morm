package me.espada.model.utils;

import me.espada.utils.GenericBuilder;

import java.lang.reflect.Field;

public class PropertyFieldMapper {

    public Property map(Field field) {
        return GenericBuilder.of(Property::new)
                .with(Property::setName, field.getName())
                .with(Property::setField, field)
                .with(Property::setDataType, field.getType())
                .with(Property::setAnnotatdElement, field)
                .build();
    }


}

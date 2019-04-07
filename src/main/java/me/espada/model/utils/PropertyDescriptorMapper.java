package me.espada.model.utils;

import me.espada.utils.GenericBuilder;

import java.beans.PropertyDescriptor;

public class PropertyDescriptorMapper {

    public Property map(PropertyDescriptor propertyDescriptor) {
        return GenericBuilder.of(Property::new)
                .with(Property::setName, propertyDescriptor.getName())
                .with(Property::setReadMethod, propertyDescriptor.getReadMethod())
                .with(Property::setWriteMethod, propertyDescriptor.getWriteMethod())
                .with(Property::setDataType, propertyDescriptor.getPropertyType())
                .with(Property::setAnnotatdElement, propertyDescriptor.getReadMethod())
                .build();
    }


}

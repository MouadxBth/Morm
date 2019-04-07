package me.espada.model;

import me.espada.model.utils.Property;

public interface Model {

     Object getValue(Object model, String name);

     void putValue(Object model, String name, Object value);

     Property getGeneratedColumnProperty();

}

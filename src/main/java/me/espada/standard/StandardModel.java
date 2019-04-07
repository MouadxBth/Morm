package me.espada.standard;

import me.espada.model.Model;
import me.espada.model.utils.Property;

import javax.persistence.EnumType;
import java.util.LinkedHashMap;
import java.util.List;

public class StandardModel implements Model {

    private LinkedHashMap<String, Property> propertyMap = new LinkedHashMap<String, Property>();
    private String table;
    private String primaryKeyName;
    private String generatedColumnName;

    private String insertStatement;
    private int insertStatementArgumentsCount;
    private String [] insertColumnNames;

    private String upsertStatement;
    private int upsertStatementArgumentsCount;
    private String [] upsertColumnNames;

    private String updateStatement;
    private String[] updateColumnNames;
    private int updateStatementArgumentsCount;

    private String selectColumns;

    private List<Property> properties;

    private Class<?> type;




    @Override
    public Object getValue(Object model, String name) {
        Object value = null;
        try {
            Property property = propertyMap.get(name);

            if (property == null) {
                throw new RuntimeException("No such field: " + name);
            }


            if (property.getReadMethod() != null) {
                value = property.getReadMethod().invoke(model);

            } else if (property.getField() != null) {
                value = property.getField().get(property);
            }

            if (value != null) {
                if (property.getSerializer() != null) {
                    value =  property.getSerializer().serialize(value);

                } else if (property.isEnumField()) {
                    // handle enums according to selected enum type
                    if (property.getEnumType() == EnumType.ORDINAL) {
                        value = ((Enum) value).ordinal();
                    }
                    // EnumType.STRING and others (if present in the future)
                    else {
                        value = value.toString();
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    @Override
    public void putValue(Object model, String name, Object value) {

    }

    @Override
    public Property getGeneratedColumnProperty() {
        return propertyMap.get(getGeneratedColumnName());
    }

    public LinkedHashMap<String, Property> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(LinkedHashMap<String, Property> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    public String getGeneratedColumnName() {
        return generatedColumnName;
    }

    public void setGeneratedColumnName(String generatedColumnName) {
        this.generatedColumnName = generatedColumnName;
    }

    public String getInsertStatement() {
        return insertStatement;
    }

    public void setInsertStatement(String insertStatement) {
        this.insertStatement = insertStatement;
    }

    public int getInsertStatementArgumentsCount() {
        return insertStatementArgumentsCount;
    }

    public void setInsertStatementArgumentsCount(int insertStatementArgumentsCount) {
        this.insertStatementArgumentsCount = insertStatementArgumentsCount;
    }

    public String[] getInsertColumnNames() {
        return insertColumnNames;
    }

    public void setInsertColumnNames(String[] insertColumnNames) {
        this.insertColumnNames = insertColumnNames;
    }

    public String getUpsertStatement() {
        return upsertStatement;
    }

    public void setUpsertStatement(String upsertStatement) {
        this.upsertStatement = upsertStatement;
    }

    public int getUpsertStatementArgumentsCount() {
        return upsertStatementArgumentsCount;
    }

    public void setUpsertStatementArgumentsCount(int upsertStatementArgumentsCount) {
        this.upsertStatementArgumentsCount = upsertStatementArgumentsCount;
    }

    public String[] getUpsertColumnNames() {
        return upsertColumnNames;
    }

    public void setUpsertColumnNames(String[] upsertColumnNames) {
        this.upsertColumnNames = upsertColumnNames;
    }

    public String getUpdateStatement() {
        return updateStatement;
    }

    public void setUpdateStatement(String updateStatement) {
        this.updateStatement = updateStatement;
    }

    public String[] getUpdateColumnNames() {
        return updateColumnNames;
    }

    public void setUpdateColumnNames(String[] updateColumnNames) {
        this.updateColumnNames = updateColumnNames;
    }

    public int getUpdateStatementArgumentsCount() {
        return updateStatementArgumentsCount;
    }

    public void setUpdateStatementArgumentsCount(int updateStatementArgumentsCount) {
        this.updateStatementArgumentsCount = updateStatementArgumentsCount;
    }

    public String getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(String selectColumns) {
        this.selectColumns = selectColumns;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}

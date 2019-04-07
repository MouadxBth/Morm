package me.espada.standard;

import me.espada.Query;
import me.espada.model.StatementMaker;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class StandardStatementMaker implements StatementMaker {

    private ConcurrentHashMap<Class<?>, StandardModel> standardModelConcurrentHashMap = new ConcurrentHashMap<Class<?>, StandardModel>();

    @Override
    public StandardModel getModel(Class<?> target) {
        StandardModel standardModel = standardModelConcurrentHashMap.get(target);
        if(standardModel == null) {
            standardModel = new StandardModelMaker(target).build();
            standardModelConcurrentHashMap.put(target, standardModel);

            makeInsertStatement(standardModel);
            makeUpsertStatement(standardModel);
            makeUpdateStatement(standardModel);
            makeSelectColumns(standardModel);
        }

        return standardModel;
    }

    private void makeInsertStatement(StandardModel standardModel) {

    }

    private void makeUpsertStatement(StandardModel standardModel) {

    }

    private void makeUpdateStatement(StandardModel standardModel) {

        ArrayList<String> updateColumnNames = new ArrayList<String>();

        standardModel.getPropertyMap().values().stream()
                .filter(property -> !property.isPrimaryKey() && !property.isGenerated())
                .forEach(property -> updateColumnNames.add(property.getName()));

        standardModel.setUpdateColumnNames(updateColumnNames.toArray(new String[0]));

        standardModel.setUpdateStatementArgumentsCount(standardModel.getUpdateColumnNames().length + 1); // + 1 for the where arg

        StringBuilder updateStatement = new StringBuilder();
        updateStatement.append("UPDATE ").append(standardModel.getTable());
        updateStatement.append(" SET ");
        updateColumnNames.forEach(name -> updateStatement.append(name).append("=?").append(","));
        updateStatement.append(" WHERE ").append(standardModel.getPrimaryKeyName()).append("=?");

        standardModel.setUpdateStatement(updateStatement.toString());
    }

    private void makeSelectColumns(StandardModel standardModel) {

    }

    @Override
    public String getInsertStatement(Query query, Object row) {
        return null;
    }

    @Override
    public Object[] getInsertArguments(Query query, Object row) {
        return new Object[0];
    }

    @Override
    public String getUpdateStatement(Query query, Object row) {
        return null;
    }

    @Override
    public Object[] getUpdateArguments(Query query, Object row) {
        return new Object[0];
    }

    @Override
    public String getDeleteStatement(Query query, Object row) {
        return null;
    }

    @Override
    public Object[] getDeleteArguments(Query query, Object row) {
        return new Object[0];
    }

    @Override
    public String getUpsertStatement(Query query, Object row) {
        return null;
    }

    @Override
    public Object[] getUpsertArguments(Query query, Object row) {
        return new Object[0];
    }

    @Override
    public String getSelectStatement(Query query, Class<?> rowClass) {

        StandardModel standardModel = getModel(rowClass);
        String selectColumns = standardModel.getSelectColumns();
        String where = query.getWhere();
        String table = query.getTable();
        String orderBy = query.getOrderBy();

        if (table == null) {
            table = standardModel.getTable();
        }

        StringBuilder selectStatement = new StringBuilder("SELECT ").append(selectColumns);
        selectStatement.append(" FROM ").append(table);

        if (where != null) {
            selectStatement.append(" WHERE ").append(where);
        }
        if (orderBy != null) {
            selectStatement.append(" ORDER BY ").append(orderBy);
        }

        return selectStatement.toString();
    }

    @Override
    public String getCreateTableStatement(Class<?> clazz) {
        return null;
    }


    @Override
    public void populateGeneratedKey(ResultSet generatedKeys, Object insertRow) {

    }
}

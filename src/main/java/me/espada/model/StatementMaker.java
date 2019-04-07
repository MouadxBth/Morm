package me.espada.model;

import me.espada.Query;

import java.sql.ResultSet;

public interface StatementMaker  {

     String getInsertStatement(Query query, Object row);
     Object[] getInsertArguments(Query query, Object row);

     String getUpdateStatement(Query query, Object row);
     Object[] getUpdateArguments(Query query, Object row);

     String getDeleteStatement(Query query, Object row);
     Object[] getDeleteArguments(Query query, Object row);

     String getUpsertStatement(Query query, Object row);
     Object[] getUpsertArguments(Query query, Object row);

     String getSelectStatement(Query query, Class<?> rowClass);
     String getCreateTableStatement(Class<?> clazz);

     Model getModel(Class<?> rowClass);

     void populateGeneratedKey(ResultSet generatedKeys, Object insertRow);

}

package me.espada;

import me.espada.model.Model;
import me.espada.model.StatementMaker;
import me.espada.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Query {

    private Object insertRow;
    private String statement;

    private String table;
    private String where;
    private String orderBy;
    private Object[] arguments;
    private int rowsAffected;

    private Database database;
    private StatementMaker statementMaker;

    public Query(Database database) {
        this.database = database;
        this.statementMaker = database.getStatementMaker();
    }

    /**
     * Use to create a query using raw sql statements
     * it overrides any other methods like @{@link }
     * @param statement The statement string to use, may include ? parameters.
     * @param arguments The parameter values to use in the query.
     */
    @Deprecated
    public Query rawSql(String statement, Object... arguments) {
        this.statement = statement;
        this.arguments = arguments;
        return this;
    }

    /**
     * Use to create a query using raw sql statements
     * it overrides any other methods like @{@link }
     * @param statement The statement string to use, may include ? parameters.
     * @param arguments The parameter values to use in the query.
     */
    @Deprecated
    public Query rawSql(String statement, List<?> arguments) {
        this.statement = statement;
        this.arguments = arguments.toArray();
        return this;
    }

    /**
     * Add a where clause and some parameters to a query. Has no effect if
     * the .sql() method is used.
     * @param where Example: "name=?"
     * @param arguments The parameter values to use in the where, example: "Bob"
     */
    public Query where(String where, Object... arguments) {
        this.where = where;
        this.arguments = arguments;
        return this;
    }

    /**
     * Add an "orderBy" clause to a query.
     * @param orderBy the order
     * @return Query/this
     */
    public Query orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }


    /**
     * @param type
     * @param <T>
     * @return
     */
    public <T> List<T> find(Class<T> type) {
        this.statement = statementMaker.getSelectStatement(this, type);

        final Connection connection = getWrappedConnection();
        final PreparedStatement preparedStatement = preparedStatement(connection, Arrays.asList(arguments));
        final ResultSet resultSet = getWrappedResultSet(preparedStatement);

        return getResult(type, connection, preparedStatement, resultSet);
    }

    /**
     * Returns the first row in a query in a pojo, or null if the query returns no results.
     * Will return it in a Map if a class that implements Map is specified.
     */
    public <T> T findFirst(Class<T> type) {
        List<T> list = find(type);
        return !list.isEmpty() ? (T) list.get(0) : null;
    }

    public Query execute() {
        final Connection connection = getWrappedConnection();
        final PreparedStatement preparedStatement = preparedStatement(connection, Arrays.asList(arguments), Statement.RETURN_GENERATED_KEYS);
        this.rowsAffected = wrapExecute(preparedStatement);
    }

    private <T> List<T> getResult(Class<T> type, Connection connection, PreparedStatement preparedStatement, ResultSet result) {
        Function<ResultSet, List<T>> function = (resultSet) ->
                Utils.isPrimitiveOrString(type) ? getPrimitiveResult(resultSet) : getTypeResult(type, resultSet);

        return function.andThen((rs) -> {
            wrappClose(preparedStatement);
            wrappClose(result);
            wrappClose(connection);
            return rs;
        }).apply(result);
    }

    private <T> List<T> getTypeResult(Class<T> type, ResultSet resultSet) {
        List<T> result = new ArrayList<>();

        ResultSetMetaData meta = getWrappedResultSetMetaData(resultSet);
        List<Object> temp = new ArrayList<>(getWrappedColumnCount(meta));

        Model model = statementMaker.getModel(type);

        while (getWrappedResultSetNext(resultSet)) {
            T row = getNewInstance(type);

            temp.forEach(o -> {
                model.putValue(
                        row,
                        getWrappedColumnLabel(meta, temp.indexOf(o) + 1),
                        getWrappedObject(resultSet, temp.indexOf(o) + 1)
                );
            });
            result.add((T) row);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getPrimitiveResult(ResultSet resultSet) {
        assert resultSet != null;
        while(getWrappedResultSetNext(resultSet)) {
            return Collections.singletonList((T) getWrappedObject(resultSet, 1));
        }
        return new ArrayList<>();
    }

    private int wrapExecute(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private ResultSetMetaData getWrappedResultSetMetaData(ResultSet resultSet) {
        try {
            return resultSet.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getWrappedColumnLabel(ResultSetMetaData resultSetMetaData, int index) {
        try {
            return resultSetMetaData.getColumnLabel(index);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getWrappedColumnCount(ResultSetMetaData resultSetMetaData) {
        try {
            return resultSetMetaData.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private <T> T getNewInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Object getWrappedObject(ResultSet resultSet, int columnIndex) {
        try {
            return resultSet.getObject(columnIndex);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean getWrappedResultSetNext(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param connection
     * @param objects
     * @return
     */
    private PreparedStatement preparedStatement(Connection connection, List<Object> objects) {
        return prepareStatement(getWrappedPreparedStatement(connection), objects);
    }

    private PreparedStatement preparedStatement(Connection connection, List<Object> objects, int autoGeneratedKeys) {
        return prepareStatement(getWrappedPreparedStatement(connection, autoGeneratedKeys), objects);
    }

    /**
     * @param preparedStatement
     * @param parameters
     * @return
     */
    private PreparedStatement prepareStatement(PreparedStatement preparedStatement, List<Object> parameters) {
        if(parameters != null) {
            parameters.forEach(param -> {
                try {
                    preparedStatement.setObject(parameters.indexOf(param) + 1, param);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        return preparedStatement;
    }

    /**
     * @return
     */
    private Connection getWrappedConnection() {
        try {
            return database.getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param connection
     * @return
     */
    private PreparedStatement getWrappedPreparedStatement(Connection connection) {
        try {
            return connection.prepareStatement(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PreparedStatement getWrappedPreparedStatement(Connection connection, int autoGeneratedKeys) {
        try {
            return connection.prepareStatement(this.statement, autoGeneratedKeys);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param preparedStatement
     * @return
     */
    private ResultSet getWrappedResultSet(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void wrappClose(AutoCloseable autoCloseable) {
        if(autoCloseable == null) return;
        try {
            autoCloseable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object getInsertRow() {
        return insertRow;
    }

    public String getStatement() {
        return statement;
    }

    public String getTable() {
        return table;
    }

    public String getWhere() {
        return where;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public int getRowsAffected() {
        return rowsAffected;
    }
}

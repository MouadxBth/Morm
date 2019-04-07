package me.espada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Query {

    private Object insertRow;
    private String statement;

    private String table;
    private String where;
    private String orderBy;
    private Object[] arguments;
    private int rowsAffected;

    private Database database;

    public Query(Database database) {
        this.database = database;
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
        final Connection connection = getWrappedConnection();
        final PreparedStatement preparedStatement = preparedStatement(connection, Arrays.asList(arguments));
        final ResultSet resultSet = getWrappedResultSet(preparedStatement);
    }

    /**
     * @param connection
     * @param objects
     * @return
     */
    protected PreparedStatement preparedStatement(Connection connection, List<Object> objects) {
        return prepareStatement(getWrappedPreparedStatement(connection), objects);
    }

    /**
     * @param preparedStatement
     * @param parameters
     * @return
     */
    protected PreparedStatement prepareStatement(PreparedStatement preparedStatement, List<Object> parameters) {
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

    /**
     * Returns the first row in a query in a pojo, or null if the query returns no results.
     * Will return it in a Map if a class that implements Map is specified.
     */
    public <T> T findFirst(Class<T> type) {
        List<T> list = find(type);
        return list.size() > 0 ? (T) list.get(0) : null;
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

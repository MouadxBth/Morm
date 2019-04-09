package me.espada;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.espada.functions.BiProducingFunction;
import me.espada.model.StatementMaker;
import me.espada.standard.StandardStatementMaker;

import javax.sql.DataSource;

/**
 * Use the {@link me.espada.utils.GenericBuilder} to create a new instance of this class
 * providing it the {@link Credentials}
 */
public class Database {

    private Credentials credentials;
    private DataSource dataSource;
    private static StatementMaker statementMaker;

    static {
        statementMaker = new StandardStatementMaker();
    }

    /**
     * To create and configure the HikariConfig with whatever
     * properties you like!
     *
     * @param configurate takes in a {@link BiProducingFunction} that takes the {@link Credentials} and a
     * {@link HikariConfig}, edits it, and returns it
     *
     * @return a Data source, in this case, a HikariDataSource
     */
    protected DataSource getDataSource(BiProducingFunction<HikariConfig, Credentials, HikariConfig> configurate) {
        final HikariConfig hikariConfig = configurate.apply(new HikariConfig(), credentials);
        this.dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    /**
     * @return the initialized data source
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**This is used to instantiate the {@link Credentials} in this class
     * @param credentials The final valid credentials
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public StatementMaker getStatementMaker() {
        return statementMaker;
    }
}

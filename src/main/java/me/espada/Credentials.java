package me.espada;


public class Credentials  {

    private String hostname;
    private int port;
    private String username;
    private String password;
    private String database;

    public Credentials setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public Credentials setPort(int port) {
        this.port = port;
        return this;
    }

    public Credentials setUsername(String username) {
        this.username = username;
        return this;
    }

    public Credentials setPassword(String password) {
        this.password = password;
        return this;
    }

    public Credentials setDatabase(String database) {
        this.database = database;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String toURL() {
        return "jdbc:mysql://" + hostname + ":" + port + "/" + database;
    }
}

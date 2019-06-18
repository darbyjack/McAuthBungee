package me.glaremasters.mcauth.database.mysql;

import com.sun.rowset.CachedRowSetImpl;
import com.zaxxer.hikari.HikariDataSource;
import me.glaremasters.mcauth.McAuth;
import me.glaremasters.mcauth.database.DatabaseProvider;
import net.md_5.bungee.config.Configuration;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL implements DatabaseProvider {

    private HikariDataSource hikari;
    private McAuth mcAuth;
    private Configuration config;

    public MySQL(McAuth mcAuth) {
        this.mcAuth = mcAuth;
        config = mcAuth.getConfiguration();
    }

    @Override
    public void init() {

        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(config.getInt("pool-size"));

        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        hikari.addDataSourceProperty("serverName", config.getString("host"));
        hikari.addDataSourceProperty("port", config.getInt("port"));
        hikari.addDataSourceProperty("databaseName", config.getString("database"));

        hikari.addDataSourceProperty("user", config.getString("username"));
        hikari.addDataSourceProperty("password", config.getString("password"));

        hikari.addDataSourceProperty("characterEncoding", "utf8");
        hikari.addDataSourceProperty("useUnicode", "true");

        hikari.validate();

        execute(Query.CREATE_TABLE);
    }

    @Override
    public void insertUser(String uuid, String token) {
        execute(Query.INSERT_USER, uuid, token);
    }

    @Override
    public void setToken(String token, String uuid) {
        execute(Query.UPDATE_TOKEN, token, uuid);
    }

    @Override
    public boolean hasToken(String uuid) {
        try {
            ResultSet rs = executeQuery(Query.TOKEN_CHECK, uuid);
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void execute(String query, Object... parameters) {

        try (Connection connection = hikari
                .getConnection(); PreparedStatement statement = connection
                .prepareStatement(query)) {

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private ResultSet executeQuery(String query, Object... parameters) {
        try (Connection connection = hikari
                .getConnection(); PreparedStatement statement = connection
                .prepareStatement(query)) {
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            CachedRowSet resultCached = new CachedRowSetImpl();
            ResultSet resultSet = statement.executeQuery();

            resultCached.populate(resultSet);
            resultSet.close();

            return resultCached;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}

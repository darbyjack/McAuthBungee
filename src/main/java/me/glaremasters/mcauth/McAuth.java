package me.glaremasters.mcauth;

import me.glaremasters.mcauth.database.DatabaseProvider;
import me.glaremasters.mcauth.database.mysql.MySQL;
import me.glaremasters.mcauth.events.Login;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class McAuth extends Plugin {

    private Configuration configuration;
    private File file;
    private DatabaseProvider database;

    @Override
    public void onEnable() {
        createConfig();
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        database = new MySQL(this);
        database.init();

        getProxy().getPluginManager().registerListener(this, new Login(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Create the config file if it doesn't exist.
     */
    private void createConfig() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the config file
     * @return config file
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Get the database
     * @return database
     */
    public DatabaseProvider getDatabase() {
        return database;
    }
}

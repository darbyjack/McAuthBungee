package me.glaremasters.mcauth.events;

import me.glaremasters.mcauth.McAuth;
import me.glaremasters.mcauth.database.DatabaseProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class Login implements Listener {

    private McAuth mcAuth;
    private DatabaseProvider database;
    private Configuration config;
    private String random = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public Login(McAuth mcAuth) {
        this.mcAuth = mcAuth;
        this.database = mcAuth.getDatabase();
        this.config = mcAuth.getConfiguration();
    }

    @EventHandler
    public void onJoin(PreLoginEvent event) {
        String uuid = event.getConnection().getUUID();
        String token = randomAlphaNumeric(10);
        String name = event.getConnection().getName();
        String ip = event.getConnection().getAddress().toString().substring(1).split(":")[0];

        if (database.hasToken(uuid)) {
            database.setToken(token, uuid);
        } else {
            database.insertUser(uuid, token);
        }

        StringBuilder sb = new StringBuilder();
        List<String> kickMessage = config.getStringList("kick-message");
        for (String line : kickMessage) {
            sb.append(color(line).replace("{uuid}", uuid).replace("{token}", token).replace("{name}", name).replace("{ip}", ip) + "\n");
        }

        event.setCancelReason(sb.toString());
        event.setCancelled(true);
    }

    /**
     * Create the token
     * @param count the amount of characters
     * @return the random string
     */
    public String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * random.length());
            builder.append(random.charAt(character));
        }
        return builder.toString();
    }

    /**
     * Color the string
     * @param input input
     * @return colored
     */
    public String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}

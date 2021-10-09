package me.TurtlesAreHot.NameHistory;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main extends Plugin {

    public static Configuration configuration;
    public static File dataFolder;

    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        try {
            configuration = loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new NameHistory());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Reload());
    }

    @Override
    public void onDisable() {

    }

    public Configuration loadConfig() throws IOException {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File con = new File(getDataFolder(), "config.yml");

        if(!con.exists()) {
            con.createNewFile();
            FileWriter fw = new FileWriter(con);
            fw.write("cooldown: 60");
            fw.flush();
            fw.close();
        }

        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(con);
    }

    public static int getCooldown() {
        return configuration.getInt("cooldown");
    }
}

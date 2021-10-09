package me.TurtlesAreHot.NameHistory;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Reload extends Command {

    public Reload() {
        super("nhreload");
    }

    public void execute(CommandSender sender, String[] args) {
        if(sender.hasPermission("namehistory.reload")) {
            try {
                Main.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Main.dataFolder, "config.yml"));
                sender.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[NameHistory] Reloaded config.yml!"));
            }
            catch(IOException e) {
                e.printStackTrace();
                sender.sendMessage(new TextComponent(ChatColor.DARK_GREEN + "[NameHistory] An error has occured."));
            }
        }
        else {
            sender.sendMessage(new TextComponent(ChatColor.DARK_GREEN  + "[NameHistory] No permissions for this command."));
        }
    }
}

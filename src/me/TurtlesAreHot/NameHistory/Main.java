package me.TurtlesAreHot.NameHistory;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
  public void onEnable() {
    ProxyServer.getInstance().getPluginManager().registerCommand(this, new NameHistory());
  }
  
  public void onDisable() {}
}

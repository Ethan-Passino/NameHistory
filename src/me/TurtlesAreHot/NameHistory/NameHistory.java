package me.TurtlesAreHot.NameHistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class NameHistory extends Command {
  int cooldownTime;
  
  Map<String, Long> cooldowns;
  
  public NameHistory() {
    super("nh");
    this.cooldownTime = 1;
    this.cooldowns = new HashMap<>();
  }
  
  public void sendMsg(ProxiedPlayer player, String message) {
    player.sendMessage(player.getUniqueId(), (BaseComponent)new TextComponent(message));
  }
  
  public void execute(CommandSender sender, String[] args) {
    if (!(sender instanceof ProxiedPlayer))
      return; 
    ProxiedPlayer player = (ProxiedPlayer)sender;
    if (args.length != 1) {
      sendMsg(player, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[NameHistory]" + ChatColor.YELLOW + " Proper usage: /nh <username>");
      sendMsg(player, ChatColor.YELLOW + "NameHistory v1.0 created by" + ChatColor.DARK_GREEN + " TurtlesAreHot");
      return;
    } 
    String username = args[0];
    if (!username.matches("[a-zA-Z0-9_]{1,16}")) {
      sendMsg(player, ChatColor.DARK_GREEN + "[NameHistory] Impossible username. Try again.");
      return;
    } 
    if (this.cooldowns.containsKey(player.getDisplayName())) {
      if ((System.currentTimeMillis() - ((Long)this.cooldowns.get(player.getDisplayName())).longValue()) / 1000L < this.cooldownTime) {
        sendMsg(player, ChatColor.YELLOW + "You are still on cooldown for " + (((
            System.currentTimeMillis() - ((Long)this.cooldowns.get(player.getDisplayName())).longValue()) / 1000L - this.cooldownTime) * -1L) + " seconds.");
        return;
      } 
      this.cooldowns.remove(player.getDisplayName());
    } 
    this.cooldowns.put(player.getDisplayName(), Long.valueOf(System.currentTimeMillis()));
    try {
      URL uuidURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
      HttpURLConnection con = (HttpURLConnection)uuidURL.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      int status = con.getResponseCode();
      if (status != 200) {
        sendMsg(player, ChatColor.DARK_GREEN + "[NameHistory] " + ChatColor.YELLOW + "This user doesn't exist.");
        return;
      } 
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuffer content = new StringBuffer();
      String inputLine;
      while ((inputLine = in.readLine()) != null)
        content.append(inputLine); 
      in.close();
      con.disconnect();
      String contentUUID = content.toString();
      String playerUUID = contentUUID.substring(contentUUID.indexOf(",") + 7, contentUUID.indexOf("}") - 1);
      inputLine = "";
      content = new StringBuffer();
      URL nhURL = new URL("https://api.mojang.com/user/profiles/" + playerUUID + "/names");
      HttpURLConnection connection = (HttpURLConnection)nhURL.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/json");
      status = connection.getResponseCode();
      BufferedReader inNH = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      while ((inputLine = inNH.readLine()) != null)
        content.append(inputLine); 
      inNH.close();
      connection.disconnect();
      String contentHistory = content.toString();
      String[] spliced = contentHistory.split("}");
      String[] changes = new String[spliced.length - 1];
      String[] times = new String[spliced.length - 1];
      int i;
      for (i = 0; i < spliced.length && 
        spliced[i].length() >= 10; i++) {
        String dataString = spliced[i];
        String nameAndDate = dataString.substring(10);
        changes[i] = nameAndDate.substring(0, nameAndDate.indexOf('"'));
        if (i == 0) {
          times[0] = "";
        } else {
          String date = nameAndDate.substring(nameAndDate.indexOf("At") + 4);
          long epoch = Long.parseLong(date);
          Date tm = new Date(epoch);
          times[i] = tm.toString();
        } 
      } 
      sendMsg(player, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[NameHistory] " + username + "'s previous names");
      for (i = 0; i < changes.length; i++)
        sendMsg(player, ChatColor.YELLOW + changes[i] + ChatColor.GRAY + " - " + times[i]); 
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}

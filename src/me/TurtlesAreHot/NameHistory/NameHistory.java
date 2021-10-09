package me.TurtlesAreHot.NameHistory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class NameHistory extends Command {

    public NameHistory() {
        super("nh");
    }

    public void sendMsg(ProxiedPlayer player, String message) {
        player.sendMessage(player.getUniqueId(), new TextComponent(message));
    }


    int cooldownTime = Main.getCooldown();
    Map<String, Long> cooldowns = new HashMap<>(); //Player name and a time in miliseconds when the player ran the command.

    public void checkAllCooldowns() {
        Long currentTime = System.currentTimeMillis();
        for(Map.Entry<String, Long> entry : cooldowns.entrySet()) {
            if((currentTime - entry.getValue()) / 1000 >= cooldownTime) {
                cooldowns.remove(entry.getKey());
            }
        }
    }

    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if(args.length != 1) {
                sendMsg(player, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[NameHistory]" +
                        ChatColor.YELLOW + " Proper usage: /nh <username>");
                sendMsg(player, ChatColor.YELLOW + "NameHistory v1.0 " +
                        "created by" + ChatColor.DARK_GREEN + " TurtlesAreHot");
                return;
            }
            String username = args[0];
            // Checking if the username provided is a possible username
            // added 1-2 character accounts here too because they exist
            if(!(username.matches("[a-zA-Z0-9_]{1,16}"))) {
                sendMsg(player, ChatColor.DARK_GREEN + "[NameHistory] Impossible username. Try again.");
                return;
            }
            if(!player.hasPermission("namehistory.bypass")) {

                if (cooldowns.containsKey(player.getDisplayName())) {
                    if ((System.currentTimeMillis() - cooldowns.get(player.getDisplayName())) / 1000 < cooldownTime) {
                        sendMsg(player, ChatColor.YELLOW + "You are still on cooldown for " +
                                ((((System.currentTimeMillis() - cooldowns.get(player.getDisplayName())) / 1000)
                                        - cooldownTime) * -1)
                                + " seconds.");
                        return;
                    } else {
                        cooldowns.remove(player.getDisplayName());
                        checkAllCooldowns();
                    }
                }

                cooldowns.put(player.getDisplayName(), System.currentTimeMillis());
            }
            try {
                URL uuidURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
                HttpURLConnection con = (HttpURLConnection) uuidURL.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                int status = con.getResponseCode();
                if(status != 200) {
                    sendMsg(player, ChatColor.DARK_GREEN + "[NameHistory] " + ChatColor.YELLOW +
                            "This user doesn't exist.");
                    return;
                }
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                String contentUUID = content.toString();
                String playerUUID = contentUUID.substring(contentUUID.indexOf(",") + 7, contentUUID.indexOf("}")-1);

                inputLine = "";
                content = new StringBuffer();
                URL nhURL = new URL("https://api.mojang.com/user/profiles/" + playerUUID + "/names");
                HttpURLConnection connection = (HttpURLConnection) nhURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                status = connection.getResponseCode();
                BufferedReader inNH = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                while((inputLine = inNH.readLine()) != null) {
                    content.append(inputLine);
                }
                inNH.close();
                connection.disconnect();
                String contentHistory = content.toString();
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(contentHistory);
                JsonArray ja = jsonTree.getAsJsonArray();
                //changedToAt, Name
                Map<Date, String> names = new TreeMap<Date, String>();
                for(int i = 0; i < ja.size(); i++) {
                    JsonElement je = ja.get(i);
                    JsonObject nameInfo = je.getAsJsonObject();
                    if(nameInfo.size() == 1) {
                        names.put(new java.util.Date(0L), nameInfo.get("name").toString());
                    } else {
                        names.put(new java.util.Date(nameInfo.get("changedToAt").getAsLong()), nameInfo.get("name").toString());
                    }
                }

                sendMsg(player, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[NameHistory] "
                        + username + "'s previous names");
                for(Map.Entry<Date, String> entry : names.entrySet()) {
                    if(entry.getKey().before(new java.util.Date(1L))) {
                        sendMsg(player, ChatColor.YELLOW + entry.getValue().replaceAll("[\"]", "") + ChatColor.GRAY + " - ");

                    }
                    else {
                        sendMsg(player, ChatColor.YELLOW + entry.getValue().replaceAll("[\"]", "") + ChatColor.GRAY + " - " + entry.getKey());
                    }
                }








            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

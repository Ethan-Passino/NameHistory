# 🕹️ NameHistory

**⚠️ Note**: *This project no longer functions as intended due to Mojang's UI changes.* Previously, this tool retrieved a user's past Minecraft usernames directly. However, due to these changes, username history can only be viewed on third-party sites that store past username data.

## 📖 Overview

NameHistory is a Minecraft plugin that was designed to allow users to access the history of a Minecraft player's usernames directly within the game. The tool fetched data from Mojang's API to display previous usernames.

## ✨ Features

- 🧑‍🤝‍🧑 Retrieve username history for Minecraft accounts
- 💬 Simple commands to view and reload name data
- ⚙️ Configurable options via `plugin.yml`

## 📦 Setup and Installation

1. **Requirements**: This plugin requires a compatible Minecraft server running [Spigot](https://www.spigotmc.org/) or [Bukkit](https://bukkit.org/).
2. **Build with Maven**: Use the `pom.xml` file to compile and build the plugin with Maven.
3. **Installation**:
   - 📂 Download the compiled `.jar` file.
   - 📂 Place it in your server's `plugins` folder.
   - 🔄 Restart the server to enable the plugin.

## 💻 Commands

- `/namehistory <username>`: Fetches the username history for the specified player.
- `/namehistory reload`: Reloads the plugin configurations.

## ⚠️ Disclaimer

As of Mojang's recent updates, this plugin is no longer functional, as Mojang no longer provides username history through their official API. To view username history, please refer to third-party websites that store historical data.

package com.MylesAndMore.Tumble;

import com.MylesAndMore.Tumble.commands.*;
import com.MylesAndMore.Tumble.config.ArenaManager;

import com.MylesAndMore.Tumble.config.SettingsManager;
import com.MylesAndMore.Tumble.config.LanguageManager;
import com.MylesAndMore.Tumble.config.LayerManager;
import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class Main extends JavaPlugin {
    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        migrateConfig();
        LanguageManager.readConfig();
        SettingsManager.readConfig();
        ArenaManager.readConfig();
        LayerManager.readConfig();

        Objects.requireNonNull(this.getCommand("tumble")).setExecutor(new Tumble());
        new Metrics(this, 16940);

        this.getLogger().info("Tumble successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Stop any running games
        for (Arena a : ArenaManager.arenas.values()) {
            if (a.game != null) {
                a.game.stopGame();
            }
        }
    }

    /**
     * Populate the new configs with the data from the old config.yml and rename to config_old.yml
     */
    private void migrateConfig() {
        boolean needsMigration = new File(plugin.getDataFolder(), "config.yml").exists() && !new File(plugin.getDataFolder(), "arenas.yml").exists();
        if (!needsMigration) {
            return;
        }
        plugin.getLogger().info("Converting config.yml...");

        CustomConfig configYml = new CustomConfig("config.yml");
        FileConfiguration oldConfig = configYml.getConfig();

        //collect data from old config
        boolean hideJoinLeaveMessages = oldConfig.getBoolean("hideJoinLeaveMessages");
        String permissionMessage = oldConfig.getString("permissionMessage");
        double winnerTeleportX = oldConfig.getDouble("winnerTeleport.x");
        double winnerTeleportY = oldConfig.getDouble("winnerTeleport.y");
        double winnerTeleportZ = oldConfig.getDouble("winnerTeleport.z");
        String lobbyWorldName = oldConfig.getString("lobbyWorld");
        String gameWorldName = oldConfig.getString("gameWorld");

        World lobbyWorld = lobbyWorldName == null ? null : Bukkit.getWorld(lobbyWorldName);
        World gameWorld = gameWorldName == null ? null : Bukkit.getWorld(gameWorldName);

        // create arena with info from config
        Arena a = new Arena("default");
        if (lobbyWorld != null) {
            a.lobby = new Location(lobbyWorld, lobbyWorld.getSpawnLocation().getX(), lobbyWorld.getSpawnLocation().getY(), lobbyWorld.getSpawnLocation().getZ());
            if (winnerTeleportX != 0 || winnerTeleportY != 0 || winnerTeleportZ != 0) {
                a.winnerLobby = new Location(lobbyWorld, winnerTeleportX, winnerTeleportY, winnerTeleportZ);
            }
        }
        if (gameWorld != null) {
            a.gameSpawn = new Location(gameWorld, gameWorld.getSpawnLocation().getX(), gameWorld.getSpawnLocation().getY(), gameWorld.getSpawnLocation().getZ());
            // game world is required so the arena will only be added in this case
            ArenaManager.readConfig();
            ArenaManager.arenas.put(a.name, a);
            ArenaManager.writeConfig();
        }

        // move permission message to language.yml
        if (permissionMessage != null && !permissionMessage.equals("You do not have permission to perform this command!")) { // skip migration if they left it at the old default
            CustomConfig languagesYml = new CustomConfig("languages.yml");
            languagesYml.saveDefaultConfig();
            FileConfiguration languagesConfig = languagesYml.getConfig();
            languagesConfig.set("no-permission", permissionMessage);
            languagesYml.saveConfig();
        }

        // wipe config and re-add hide-join-leave-messages under new its new name
        if (hideJoinLeaveMessages) {
            CustomConfig settingsYml = new CustomConfig("settings.yml");
            settingsYml.saveDefaultConfig();
            FileConfiguration settingsConfig = settingsYml.getConfig();
            settingsConfig.set("hide-join-leave-messages", true);
            settingsYml.saveConfig();
        }

        if (!new File("config.yml").renameTo(new File(plugin.getDataFolder(), "config_old.yml"))) {
            plugin.getLogger().severe("Failed to rename config.yml to config_old.yml. Please manually rename this to avoid data loss");
        }
    }
}
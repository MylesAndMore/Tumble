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
     * Populate the new configs with the data from the old (v1.x) config.yml and rename to config_old.yml if necessary
     */
    private void migrateConfig() {
        boolean needsMigration = new File(plugin.getDataFolder(), "config.yml").exists() && !new File(plugin.getDataFolder(), "arenas.yml").exists();
        if (!needsMigration) {
            return;
        }
        plugin.getLogger().info("Converting config.yml...");

        CustomConfig configYml = new CustomConfig("config.yml");
        FileConfiguration oldConfig = configYml.getConfig();

        // Collect data from old config
        boolean autoStartEnabled = oldConfig.getBoolean("autoStart.enabled");
        boolean hideJoinLeaveMessages = oldConfig.getBoolean("hideJoinLeaveMessages");
        String permissionMessage = oldConfig.getString("permissionMessage");
        double winnerTeleportX = oldConfig.getDouble("winnerTeleport.x");
        double winnerTeleportY = oldConfig.getDouble("winnerTeleport.y");
        double winnerTeleportZ = oldConfig.getDouble("winnerTeleport.z");
        String lobbyWorldName = oldConfig.getString("lobbyWorld");
        String gameWorldName = oldConfig.getString("gameWorld");

        World lobbyWorld = lobbyWorldName == null ? null : Bukkit.getWorld(lobbyWorldName);
        World gameWorld = gameWorldName == null ? null : Bukkit.getWorld(gameWorldName);

        // Create arena with info from config
        Arena a = new Arena("default");
        if (lobbyWorld != null) {
            a.lobby = normalizeLocation(new Location(lobbyWorld, lobbyWorld.getSpawnLocation().getX(), lobbyWorld.getSpawnLocation().getY(), lobbyWorld.getSpawnLocation().getZ()));
            if (winnerTeleportX != 0 || winnerTeleportY != 0 || winnerTeleportZ != 0) {
                a.winnerLobby = normalizeLocation(new Location(lobbyWorld, winnerTeleportX, winnerTeleportY, winnerTeleportZ));
            }
        }
        if (gameWorld != null) {
            a.gameSpawn = normalizeLocation(new Location(gameWorld, gameWorld.getSpawnLocation().getX(), gameWorld.getSpawnLocation().getY(), gameWorld.getSpawnLocation().getZ()));
            // Game world is required so the arena will only be added in this case
            ArenaManager.readConfig();
            ArenaManager.arenas.put(a.name, a);
            ArenaManager.writeConfig();
        }

        // Move permission message to language.yml
        // Skip migration if they left the message as the (old) default
        if (permissionMessage != null && !permissionMessage.equals("You do not have permission to perform this command!")) {
            CustomConfig languagesYml = new CustomConfig("language.yml");
            languagesYml.saveDefaultConfig();
            FileConfiguration languagesConfig = languagesYml.getConfig();
            languagesConfig.set("no-permission", permissionMessage);
            languagesYml.saveConfig();
        }

        // Move hide-join-leave-messages and autostart to settings.yml
        if (hideJoinLeaveMessages || autoStartEnabled) {
            CustomConfig settingsYml = new CustomConfig("settings.yml");
            settingsYml.saveDefaultConfig();
            FileConfiguration settingsConfig = settingsYml.getConfig();
            settingsConfig.set("hide-join-leave-messages", true);
            // wait-duration should stay as default unless autostart was disabled
            if (!autoStartEnabled) {
                settingsConfig.set("wait-duration", 0);
            }
            settingsYml.saveConfig();
        }

        if (!new File(plugin.getDataFolder(), "config.yml").renameTo(new File(plugin.getDataFolder(), "config_old.yml"))) {
            plugin.getLogger().severe("Failed to rename config.yml to config_old.yml. Please manually rename this to avoid data loss.");
        }
        plugin.getLogger().info("Conversion complete! Please restart the server to apply changes.");
    }

    /**
     * Normalize a location to allow it to be saved to a config
     * @param loc The location to normalize
     * @return The normalized location
     */
    private Location normalizeLocation(Location loc) {
        return new Location(loc.getWorld(),
            loc.getX() == 0.0 ? 0.5 : loc.getX(),
            loc.getY() == 0.0 ? 0.5 : loc.getY(),
            loc.getZ() == 0.0 ? 0.5 : loc.getZ());
    }
}
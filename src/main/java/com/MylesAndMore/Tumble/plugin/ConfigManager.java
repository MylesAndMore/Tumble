package com.MylesAndMore.Tumble.plugin;

import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

public class ConfigManager {
    public static HashMap<String, Arena> arenas;
    public static Location lobby;
    public static Location winnerLobby;
    public static Location waitArea;
    public static boolean HideLeaveJoin;
    public static int waitDuration;

    /**
     * Reads config file and populates values above
     */
    public static void readConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        HideLeaveJoin = config.getBoolean("hideJoinLeaveMessages", false);
        waitDuration = config.getInt("wait-duration", 15);

        // wait area
        if (config.getBoolean("wait-area.enable", false)) {
            Result<Location>res = readWorld(config.getConfigurationSection("wait-area.spawn"));
            if (!res.success) {
                plugin.getLogger().warning("Failed to load winner lobby: "+res.error);
                waitArea = null;
            }
            else {
                waitArea = res.value;
            }
        }

        // lobby
        {
            Result<Location>res = readWorld(config.getConfigurationSection("lobby.spawn"));
            if (!res.success) {
                plugin.getLogger().warning("Failed to load lobby: "+res.error);
                plugin.getLogger().severe("Lobby spawn is required! Lobby spawn will default to spawn in the default world. Run '/tumble-config set lobbyWorld' to change it");
                lobby = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
            }
            else {
                lobby = res.value;
            }
        }
        
        // winner lobby
        if (config.getBoolean("winner-lobby.enable", false)) {
            Result<Location>res = readWorld(config.getConfigurationSection("winner-lobby.spawn"));
            if (!res.success) {
                plugin.getLogger().warning("Failed to load winner lobby: "+res.error);
                winnerLobby = null;
            }
            else {
                winnerLobby = res.value;
            }
        }

        // arenas
        ConfigurationSection arenasSection = config.getConfigurationSection("arenas");
        if (arenasSection == null) {
            plugin.getLogger().warning("Section 'arenas' is missing from config");
            return;
        }
        arenas = new HashMap<>();
        for (String arenaName: arenasSection.getKeys(false)) {

            ConfigurationSection anArenaSection = arenasSection.getConfigurationSection(arenaName);
            if (anArenaSection == null) {
                plugin.getLogger().warning("Failed to load arena "+arenaName+": Error loading config section");
                continue;
            }

            Integer killAtY = anArenaSection.getInt("kill-at-y", 0);
            if (killAtY == 0) {
                killAtY = null;
            }

            Result<Location> res = readWorld(anArenaSection.getConfigurationSection("spawn"));
            if (!res.success) {
                plugin.getLogger().warning("Failed to load arena "+arenaName+": "+res.error);
                continue;
            }

            arenas.put(arenaName, new Arena(arenaName, res.value, killAtY));
        }
    }

    /**
     * tries to convert a config section in the following format to a world
     * section:
     *   x: 
     *   y: 
     *   z:
     *   world:
     * @param section the section in the yaml with x, y, z, and world as its children
     * @return result of either: 
     *   success = true and a world
     *   success = false and an error string
     */
    private static Result<Location> readWorld(@Nullable ConfigurationSection section) {

        if (section == null) {
            return new Result<>("Section missing from config");
        }

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        if (x == 0 || y == 0 || z == 0) {
            return new Result<>("Arena coordinates are missing or are zero. Coordinates cannot be zero.");
        }

        String worldName = section.getString("world");
        if (worldName == null) {
            return new Result<>("World name is missing");
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return new Result<>("Failed to load world " + worldName);
        }

        return new Result<>(new Location(world,x,y,z));
    }

    public static void WriteConfig() {
        if (waitArea != null) {
            WriteWorld(Objects.requireNonNull(plugin.getConfig().getConfigurationSection("wait-area")), waitArea);
            plugin.getConfig().set("wait-area.enable", true);
        }
        else {
            plugin.getConfig().set("wait-area.enable", false);
        }

        if (lobby != null) {
            WriteWorld(Objects.requireNonNull(plugin.getConfig().getConfigurationSection("lobby.spawn")), lobby);
        }

        if (winnerLobby != null) {
            WriteWorld(Objects.requireNonNull(plugin.getConfig().getConfigurationSection("winner-lobby.spawn")), winnerLobby);
            plugin.getConfig().set("winner-lobby.enable", true);
        }
        else {
            plugin.getConfig().set("winner-lobby.enable", false);
        }

        for (String arenaName: arenas.keySet()) {
            ConfigurationSection c = plugin.getConfig().getConfigurationSection("arenas."+arenaName+".spawn");
            if (c == null) {
                 c = plugin.getConfig().createSection("arenas."+arenaName+".spawn");
            }
            WriteWorld(c, arenas.get(arenaName).location);
        }

        plugin.saveConfig();

    }

    private static void WriteWorld(ConfigurationSection section, Location location) {
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("world", Objects.requireNonNull(location.getWorld()).getName());
    }

    /**
     * Searches all arenas for a game that player p is in
     * @param p Player to search for
     * @return the game the player is in, or null if not found
     */
    public static Game findGamePlayerIsIn(Player p) {
        for (Arena a : arenas.values()) {
            if (a.game != null && a.game.gamePlayers.contains(p)) {
                return a.game;
            }
        }
        return null;
    }
}

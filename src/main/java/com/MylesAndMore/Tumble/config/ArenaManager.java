package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.game.Game;
import com.MylesAndMore.Tumble.plugin.Result;
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

public class ArenaManager {

    public static HashMap<String, Arena> arenas;

    private static final CustomConfig customConfig = new CustomConfig("arenas.yml");
    private static final FileConfiguration config = customConfig.getConfig();

    public static void init() {
        customConfig.saveDefaultConfig();
        readConfig();
    }

    /**
     * Reads config file and populates values above
     */
    public static void readConfig() {

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

            Arena arena = new Arena(arenaName);
            arenas.put(arena.name, arena);

            int killAtY = anArenaSection.getInt("kill-at-y", 0);
            if (killAtY != 0) {
                arena.killAtY = killAtY;
            }

            Result<Location> res = readWorld(anArenaSection.getConfigurationSection("game-spawn"));
            if (res.success) {
                arena.gameSpawn = res.value;
            }

            Result<Location> lobbyRes = readWorld(anArenaSection.getConfigurationSection("lobby"));
            if (lobbyRes.success) {
                arena.lobby = lobbyRes.value;
            }

            Result<Location> winnerLobbyRes = readWorld(anArenaSection.getConfigurationSection("winner-lobby"));
            if (winnerLobbyRes.success) {
                arena.winnerLobby = winnerLobbyRes.value;
            }

            Result<Location> waitAreaRes = readWorld(anArenaSection.getConfigurationSection("wait-area"));
            if (waitAreaRes.success) {
                arena.waitArea = waitAreaRes.value;
            }

        }
    }

    public static void WriteConfig() {
        config.set("arenas", null); // clear everything

        for (Arena arena: arenas.values()) {
            WriteWorld("arenas." + arena.name + ".game-spawn", arena.gameSpawn);
            WriteWorld("arenas." + arena.name + ".lobby", arena.lobby);
            WriteWorld("arenas." + arena.name + ".winner-lobby", arena.winnerLobby);
            WriteWorld("arenas." + arena.name + ".wait-area", arena.waitArea);
        }

        customConfig.saveConfig();

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

    private static void WriteWorld(String path, @Nullable Location location) {
        if (location == null) {
            return;
        }

        ConfigurationSection section = config.getConfigurationSection(path);

        if (section == null) {
            section = config.createSection(path);
        }

        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("world", Objects.requireNonNull(location.getWorld()).getName());

    }
}

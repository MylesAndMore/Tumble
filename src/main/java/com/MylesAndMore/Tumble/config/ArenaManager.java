package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.game.Game;
import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

/**
 * Manages arenas.yml and stores list of arenas
 */
public class ArenaManager {
    public static HashMap<String, Arena> arenas;

    private static CustomConfig arenasYml;
    private static FileConfiguration config;

    /**
     * Read arenas from arenas.yml and populate this.arenas
     */
    public static void readConfig() {
        arenasYml = new CustomConfig("arenas.yml");
        arenasYml.saveDefaultConfig();
        config = arenasYml.getConfig();
        arenas = new HashMap<>();

        ConfigurationSection arenasSection = config.getConfigurationSection("arenas");
        if (arenasSection == null) {
            plugin.getLogger().warning("arenas.yml is missing key 'arenas'");
            return;
        }
        for (String arenaName: arenasSection.getKeys(false)) {
            Arena arena = new Arena(arenaName);

            if (config.contains("arenas." + arenaName + ".kill-at-y", true)) {
                arena.killAtY = config.getInt("arenas." + arenaName + ".kill-at-y");
            }
            if (config.contains("arenas." + arenaName + ".game-spawn")) {
                arena.gameSpawn = readWorld("arenas." + arenaName + ".game-spawn");
            }
            if (config.contains("arenas." + arenaName + ".lobby")) {
                arena.lobby = readWorld("arenas." + arenaName + ".lobby");
            }
            if (config.contains("arenas." + arenaName + ".winner-lobby")) {
                arena.winnerLobby = readWorld("arenas." + arenaName + ".winner-lobby");
            }
            if (config.contains("arenas." + arenaName + ".wait-area")) {
                arena.waitArea = readWorld("arenas." + arenaName + ".wait-area");
            }

            // Validate arena locations
            if (arena.gameSpawn == null) {
                plugin.getLogger().severe("arenas.yml: Arena " + arenaName + " is missing a game spawn, before you can join you must set it with '/tumble setgamespawn'.");
            }
            if (arena.lobby == null) {
                plugin.getLogger().warning("arenas.yml: Arena " + arenaName + " is missing a lobby location. The spawn point of the default world will be used.");
            }

            arenas.put(arena.name, arena);
        }
    }

    /**
     * Write arenas from this.arenas to arenas.yml
     */
    public static void WriteConfig() {
        config.set("arenas", null); // clear everything

        for (Arena arena: arenas.values()) {
            if (arena.killAtY != null) {
                config.set("arenas." + arena.name + ".kill-at-y", arena.killAtY);
            }
            if (arena.gameSpawn != null) {
                WriteWorld("arenas." + arena.name + ".game-spawn", arena.gameSpawn);
            }
            if (arena.lobby != null) {
                WriteWorld("arenas." + arena.name + ".lobby", arena.lobby);
            }
            if (arena.winnerLobby != null) {
                WriteWorld("arenas." + arena.name + ".winner-lobby", arena.winnerLobby);
            }
            if (arena.waitArea != null) {
                WriteWorld("arenas." + arena.name + ".wait-area", arena.waitArea);
            }
        }

        arenasYml.saveConfig();

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
     * Tries to convert a config section in the following format to a world
     * section:
     *   x:
     *   y:
     *   z:
     *   world:
     * @param path The section in the yaml with x, y, z, and world as its children
     * @return Result of either:
     *   Result#success = true and Result#value OR
     *   Result#success = false and Result#error
     */
    private static Location readWorld(String path) {

        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            plugin.getLogger().warning("arenas.yml: Error loading location at '" + path + "' - " + "Section is null");
            return null;
        }

        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        if (x == 0 || y == 0 || z == 0) {
            plugin.getLogger().warning("arenas.yml: Error loading location at '" + path + "' - " + "Arena coordinates are missing or are zero. Coordinates cannot be zero.");
            return null;
        }

        String worldName = section.getString("world");
        if (worldName == null) {
            plugin.getLogger().warning("arenas.yml: Error loading location at '" + path +"' - " + "World name is missing");
            return null;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("arenas.yml: Error loading location at '" + path + "' - " + "Failed to load world '" + worldName + "'");
            return null;
        }

        return new Location(world,x,y,z);
    }

    /**
     * Write a location into the config using the following format:
     * section:
     *   x:
     *   y:
     *   z:
     *   world:
     * @param path The path of the section to write
     * @param location The location to write
     */
    private static void WriteWorld(String path, @NotNull Location location) {

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

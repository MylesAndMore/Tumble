package com.MylesAndMore.Tumble.game;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * An arena is the world and spawn location where a game can take place. An arena can only host one game at a time.
 */
public class Arena {
    public Game game = null;
    public final World world;
    public final Location location;
    public final String name;

    /**
     * Creates a new Arena
     * @param name Name of the arena
     * @param location Center point / spawn point.
     */
    public Arena(@NotNull String name, @NotNull Location location) {
        this.location = location;
        this.world = location.getWorld();
        this.name = name;
    }
}

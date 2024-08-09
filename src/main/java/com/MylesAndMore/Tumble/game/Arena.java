package com.MylesAndMore.Tumble.game;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * An arena is the world and spawn location where a game can take place. An arena can only host one game at a time.
 */
public class Arena {

    public final String name;

    public Integer killAtY = null;
    public Location gameSpawn = null;
    public Location lobby = null;
    public Location winnerLobby = null;
    public Location waitArea = null;

    public Game game = null;

    /**
     * Creates a new Arena
     * @param name Name of the arena
     */
    public Arena(@NotNull String name) {
        this.name = name;
    }
}

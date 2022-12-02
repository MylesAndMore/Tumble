package com.MylesAndMore.tumble;

import com.MylesAndMore.tuble.TumbleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;

import Java.util.Arrays;
import Java.util.Collections;
import Java.util.List;

public final class Game {
    // Define the gameInstnace
    private static Game gameInstance;

    // Private Game() constructor for singleton instance
    private Game() { }

    // Other priate methods for getters and setters go here
    
    // Define local game vars
    // The gameType keeps the current game type (shocker)
    String gameType = null;

    // Creates a new Game
    // This will return true if the game succeeds creation, and false if not
    private static boolean createGame() {
        if (Objects.equals(TumbleManager.getGameType(), "shovels")) {
            // shovels logic
            gameType = "shovels"
        }
        else if (Objects.equals(TumbleManager.getGameType(), "snowballs")) {
            // snowballs
            gameType = "snowballs"
        }
        else if (Objects.equals(TumbleManager.getGameType(), "mixed")) {
            // mixed (choose random shovels or snowballs)
            gameType = "mixed"
        }
        else {
            // The game type in the config did not match a specified game type; return false to signify that
            return false;
        }
        return true;
    }

    private static boolean sendPlayers() {
        World gameWorld = Bukkit.getWorld(TumbleManager.getGameWorld());
        // Define the game world's spawnpoint as a new Location
        Location gameSpawn = new Location(gameWorld.getSpawnLocation());
        // Get the X, Y, and Z coords of that location
        Location x = gameSpawn.getX();
        Location y = gameSpawn.getY();
        Location z = gameSpawn.getZ();
        // Create Locations to scatter players around the first layer
        // These are just edited off the original spawn location;
        // they assume that the first layer has a radius of 17 blocks (it always will w/ the current generator code)
        List<Location> scatterLocations = Arrays.asList(
            new Location(gameWorld, (x - 16), y, z);
            new Location(gameWorld, x, y, (z - 16));
            new Location(gameWorld, (x + 16), y, z);;
            new Location(gameWorld, x, y, (z + 16))
        );
        // Shuffle the location list so players don't always spawn in the same location (basically, actually scatter the locations)
        Collections.shuffle(scatterLocations);
        // While there are still players in the lobby, send them to the gameWorld
        // This is just a way of sending everybody in the lobby to the game
        for (List<Player> playersInLobby = TumbleManager.getPlayersInLobby(); playersInLobby.size() > 0; playersInLobby = TumbleManager.getPlayersInLobby() & scatterLoations.remove(0)) {
            // Get a singular player from the player list
            Player aPlayer = playersInLobby.get(0);
            // Get a singular location from the scatter list
            Location aLocation = scatterLoations.get(0);
            // Teleport that player to that scatter location
            aPlayer.teleport(aLocation);
        }

        // Add a little break because it can take the clients a bit to load into the new world
        // Then, transition to another method because this one is getting really long
        // In that method: set a flag to monitor the playerDeathEvent so we know when all the players have died
        // Also start music
    }



    // Public method to get the game instance (singleton logic)
    public static Game getGame() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }
        return gameInstance;
    }

    // Public method to get the game type
    // This is public because you aren't modifying the game, just getting its type, so there shouldn't be any conflicts
    public static String getGameType() {
        return gameType;
    }
}
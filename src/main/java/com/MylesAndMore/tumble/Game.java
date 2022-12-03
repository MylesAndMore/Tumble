package com.MylesAndMore.tumble;

import com.MylesAndMore.tumble.api.Generator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game {
    // Singleton class logic
    // Define the gameInstance
    private static Game gameInstance;

    // Private Game() constructor for singleton instance
    private Game() { }

    // ONLY Public method to get the game instance
    public static Game getGame() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }
        return gameInstance;
    }



    // Define local game vars
    // The gameType keeps the current game type (shocker)
    private String gameType = null;
    // The gameState keeps the current state of the game (I'm so creative, I know)
    private String gameState = null;

    // Initialize a new instance of the Random class for use later
    private Random Random = new Random();
    // Define the game world and its spawnpoint as a new Location for use later
    private World gameWorld = Bukkit.getWorld(TumbleManager.getGameWorld());
    private Location gameSpawn = gameWorld.getSpawnLocation();

    // Make a list of the game's players for later
    private List<Player> gamePlayers = null;

    // Creates a new Game
    // This will return true if the game succeeds creation, and false if not
    public boolean startGame() {
        gameState = "starting";
        if (Objects.equals(TumbleManager.getGameType(), "shovels")) {
            // Set the correct gameType for the game we're playing, for later
            gameType = "shovels";
            // Generate the correct layers for a Shovels game
            // The else statement is just in case the generator fails; this command will fail
            if (generateLayers(gameType)) {
                // If the layer generation succeeds, give players diamond shovels
                // Reminder: we need a way for blocks to break instantly and for these to not lose durability!
                giveItems(new ItemStack(Material.DIAMOND_SHOVEL));
                // Send players to the game
                sendPlayers();
                // Keep in mind that after this runs, this command will complete and return true
            }
            else {
                return false;
            }
        }
        else if (Objects.equals(TumbleManager.getGameType(), "snowballs")) {
            gameType = "snowballs";
            if (generateLayers(gameType)) {
                // Reminder: we need a way to make this snowball infinite!
                giveItems(new ItemStack(Material.SNOWBALL));
                sendPlayers();
            }
            else {
                return false;
            }
        }
        else if (Objects.equals(TumbleManager.getGameType(), "mixed")) {
            gameType = "mixed";
            // Mixed gamemode (choose random shovels or snowballs)
            if (Random.nextInt(2) == 0) {
                generateLayers("shovels");
                giveItems(new ItemStack(Material.DIAMOND_SHOVEL));
                sendPlayers();
            }
            else {
                generateLayers("snowballs");
                giveItems(new ItemStack(Material.SNOWBALL));
                sendPlayers();
            }
        }
        else {
            // The game type in the config did not match a specified game type; return false to signify that
            return false;
        }
        // Update the game's players for later
        gamePlayers = new ArrayList<>(TumbleManager.getPlayersInGame());
        gameState = "running";
        return true;
    }

    private boolean generateLayers(String gameType) {
        Location layer = gameSpawn;
        if (Objects.equals(gameType, "shovels")) {
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 17, 1, Material.SNOW_BLOCK);
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 13, 1, Material.GRASS_BLOCK);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 4, 1, Material.PODZOL);
            layer.setY(layer.getY() + 1);
            Generator.generateLayer(layer, 4, 2, Material.GRASS);
        }
        else if (Objects.equals(gameType, "snowballs")) {
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 17, 1, Material.COAL_ORE);
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 13, 1, Material.GRANITE);
            Generator.generateLayer(layer, 4, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA);
        }
        else {
            return false;
        }
        return true;
    }

    private void sendPlayers() {
        // Get the X, Y, and Z coords of that location
        double x = gameSpawn.getX();
        double y = gameSpawn.getY();
        double z = gameSpawn.getZ();
        // Create Locations to scatter players around the first layer
        // These are just edited off the original spawn location;
        // they assume that the first layer has a radius of 17 blocks (it always will w/ the current generator code)
        List<Location> scatterLocations = new ArrayList<>();
        scatterLocations.addAll(List.of(
                new Location(gameWorld, (x - 14.5), y, (z + 0.5) , -90, 0),
                new Location(gameWorld, (x + 0.5), y, (z - 14.5), 0, 0),
                new Location(gameWorld, (x + 15.5), y, (z + 0.5), 90, 0),
                new Location(gameWorld, (x + 0.5), y, (z + 15.5), 180, 0 ),
                new Location(gameWorld, (x - 10.5), y, (z - 10.5), -45, 0),
                new Location(gameWorld, (x - 10.5), y, (z + 11.5), -135, 0),
                new Location(gameWorld, (x + 11.5), y, (z - 10.5), 45, 0),
                new Location(gameWorld, (x + 11.5), y, (z + 11.5), 135, 0))
        );
        // Shuffle the location list so players don't always spawn in the same location (basically, actually scatter the locations)
        Collections.shuffle(scatterLocations);
        // While there are still players in the lobby, send them to the gameWorld
        // This is just a way of sending everybody in the lobby to the game
        for (Player aPlayer : TumbleManager.getPlayersInLobby()) {
            //for (List<Player> playersInLobby = TumbleManager.getPlayersInLobby(); playersInLobby.size() > 0; playersInLobby = TumbleManager.getPlayersInLobby()) {
            // Get a singular player from the player list
            //Player aPlayer = playersInLobby.get(0);
            // Get a singular location from the scatter list
            Location aLocation = scatterLocations.get(0);
            // Teleport that player to that scatter location
            aPlayer.teleport(aLocation);
            // Remove that location from the list so that it cannot be used again
            scatterLocations.remove(0);
        }
    }

    public void playerDeath(@NotNull Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        // If there are more than 2 players in the game,
        if (gamePlayers.size() > 2) {
            // remove that player (who just died) from the gamePlayersArray, effectively eliminating them,
            gamePlayers.remove(player);
        }
        // otherwise, the game must have two people left (and one just died), meaning it is over
        // This logic is so that it will not remove the last player standing from the list, so we know who the winner is.
        else {
            // End the game, passing the winner to the gameEnd method
            gameEnd(gamePlayers.get(0));
        }
    }

    private void gameEnd(@NotNull Player winner) {
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + winner.getName() + " has won!");
    }

    private void giveItems(ItemStack itemStack) {
        for (List<Player> playersWithoutItem = TumbleManager.getPlayersInLobby(); playersWithoutItem.size() > 0; playersWithoutItem.remove(0)) {
            // Get a singular player from the player list
            Player playerWithoutItem = playersWithoutItem.get(0);
            // Give that player the specified item
            playerWithoutItem.getInventory().addItem(itemStack);
        }
    }


    // Methods to get the game type and game state for other classes outside the Game
    private String getGameType() { return gameType; }

    private String getGameState() { return gameState; }

}
package com.MylesAndMore.tumble;

import com.MylesAndMore.tumble.api.Generator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game {
    // Singleton class logic
    // Define the gameInstance
    private static Game gameInstance;

    // Private Game() constructor for singleton instance
    private Game() {
        gameWorld = Bukkit.getWorld(TumbleManager.getGameWorld());
        gameSpawn = gameWorld.getSpawnLocation();
    }

    // ONLY Public method to get the game instance
    public static Game getGame() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }
        return gameInstance;
    }

    // Define local game vars
    // The gameType keeps the current game type (shocker)
    private static String gameType;
    // The gameState keeps the current state of the game (I'm so creative, I know)
    private String gameState;
    // Define a variable for the roundType
    private String roundType;

    // Initialize a new instance of the Random class for use later
    private final Random Random = new Random();
    // Define the game world and its spawnpoint as a new Location for use later
    private final World gameWorld;
    private final Location gameSpawn;

    // Make a list of the game's players for later
    private List<Player> gamePlayers;
    // Make a list of the round's players
    private List<Player> roundPlayers;
    // Initialize a list to keep track of wins between rounds
    private List<Integer> gameWins;

    /**
     * Creates a new Game
     * @return true if the game succeeds creation, and false if not
     */
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
                roundType = "shovels";
                generateLayers("shovels");
                giveItems(new ItemStack(Material.DIAMOND_SHOVEL));
                sendPlayers();
            }
            else {
                roundType = "snowballs";
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
        roundPlayers = new ArrayList<>(TumbleManager.getPlayersInGame());
        gameWins = new ArrayList<>();
        gameWins.addAll(List.of(0,0,0,0,0,0,0,0));
        gameState = "running";
        return true;
    }

    private boolean generateLayers(String gameType) {
        Location layer = new Location(gameSpawn.getWorld(), gameSpawn.getX(), gameSpawn.getY(), gameSpawn.getZ(), gameSpawn.getYaw(), gameSpawn.getPitch());
        if (Objects.equals(roundType, "shovels")) {
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 17, 1, Material.SNOW_BLOCK);
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 13, 1, Material.GRASS_BLOCK);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 4, 1, Material.PODZOL);
            layer.setY(layer.getY() + 2);
            Generator.generateLayer(layer, 4, 2, Material.TALL_GRASS);
            roundType = "shovels";
        }
        else if (Objects.equals(roundType, "snowballs")) {
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 17, 1, Material.COAL_ORE);
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 13, 1, Material.GRANITE);
            Generator.generateLayer(layer, 4, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA);
            roundType = "snowballs";
        }
        else if (Objects.equals(gameType, "mixed")) {
            if (Random.nextInt(2) == 0) {
                generateLayers("shovels");
            } else {
                generateLayers("snowballs");
            }
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
            // Get a singular location from the scatter list
            Location aLocation = scatterLocations.get(0);
            // Teleport that player to that scatter location
            aPlayer.teleport(aLocation);
            // Remove that location from the list so that it cannot be used again
            scatterLocations.remove(0);
        }
    }

    private void setSurvival() {
        for (List<Player> spectators = gamePlayers; spectators.size() > 0; spectators.remove(0)) {
            // Get a singular player from the player list
            Player spectatorPlayer = spectators.get(0);
            // Set that player's gamemode to survival
            spectatorPlayer.setGameMode(GameMode.SURVIVAL);
        }
    }

    public void itemDamage(PlayerItemDamageEvent event) {
        // If the game type is shovels,
        if (Objects.equals(roundType, "shovels")) {
            // Cancel the event
            event.setCancelled(true);
        }
    }

    public void playerDeath(@NotNull Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        // If there are more than 2 players in the game,
        if (roundPlayers.size() > 2) {
            // remove that player (who just died) from the roundPlayersArray, effectively eliminating them,
            roundPlayers.remove(player);
        }
        // otherwise, the game must have two people left (and one just died), meaning it is over
        // This logic is so that it will not remove the last player standing from the list, so we know who the winner is.
        else {
            // roundPlayers.remove(player);
            // End the game, passing the winner to the gameEnd method
            roundEnd(roundPlayers.get(0));
        }
    }

    private void roundEnd(@NotNull Player winner) {
        // Set the wins of the player to their current # of wins + 1
        gameWins.set(gamePlayers.indexOf(winner), (gameWins.get(gamePlayers.indexOf(winner)) + 1));
        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + winner.getName() + " has won the round!");
        // Clear old layers (as a fill command, this would be /fill ~-20 ~-4 ~-20 ~20 ~ ~20 relative to spawn)
        Generator.generateCuboid(new Location(gameSpawn.getWorld(), gameSpawn.getX() - 20, gameSpawn.getY() - 4, gameSpawn.getZ() - 20), new Location(gameSpawn.getWorld(), gameSpawn.getX() + 20, gameSpawn.getY(), gameSpawn.getZ() + 20), Material.AIR);
        // If the player has three wins, they won the game, so initiate the gameEnd
        if (gameWins.get(gamePlayers.indexOf(winner)) == 3)  {
            gameEnd(winner);
        }
        // If that player doesn't have three wins, nobody else does, so we need another round
        else {
            // Re-generate layers
            generateLayers(gameType);
            // Teleport players
            // A new method will need to be written for this; current one only supports lobby

            // Set their gamemodes to survival
            setSurvival();
        }
    }

    private void gameEnd(@NotNull Player winner) {
        Bukkit.getServer().broadcastMessage(ChatColor.GOLD + winner.getName() + " has won the game!");
    
        // Send players back to lobby
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

    public String getGameState() { return gameState; }

}
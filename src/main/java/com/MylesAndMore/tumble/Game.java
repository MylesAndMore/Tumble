package com.MylesAndMore.tumble;

import com.MylesAndMore.tumble.api.Generator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
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
    // The gameState keeps the current state of the game (I'm so creative, I know)
    private String gameState;
    // Define a variable for the roundType
    private String roundType;

    // Initialize a new instance of the Random class for use later
    private final Random Random = new Random();
    // Define the game world and its spawnpoint as a new Location for use later
    private final World gameWorld;
    private final Location gameSpawn;

    // Make a list of the lobby's players for later
    private List<Player> lobbyPlayers = TumbleManager.getPlayersInLobby();
    // Make a list of the game's players for later
    private List<Player> gamePlayers;
    // Make a list of the round's players
    private List<Player> roundPlayers;
    // Initialize a list to keep track of wins between rounds
    private List<Integer> gameWins;


    // BEGIN PUBLIC METHODS

    /**
     * Creates a new Game
     * @param type The type of game
     * @return true if the game succeeds creation, and false if not
     */
    public boolean startGame(@NotNull String type) {
        // Check if the game is starting or running, if so, do not start
        if (Objects.equals(gameState, "starting")) {
            return false;
        }
        else if (Objects.equals(gameState, "running")) {
            return false;
        }
        else {
            // Define the gameType
            if (Objects.equals(type, "shovels")) {
                gameState = "starting";
                // Set the roundType to gameType since it won't change for this mode
                roundType = type;
                // Generate the correct layers for a Shovels game
                // The else statement is just in case the generator fails; this command will fail
                if (generateLayers(type)) {
                    // Send all players from lobby to the game
                    scatterPlayers(lobbyPlayers);
                    // Keep in mind that after this runs, this command will complete and return true
                }
                else {
                    return false;
                }
            }
            else if (Objects.equals(type, "snowballs")) {
                gameState = "starting";
                roundType = type;
                if (generateLayers(type)) {
                    scatterPlayers(lobbyPlayers);
                }
                else {
                    return false;
                }
            }
            else if (Objects.equals(type, "mixed")) {
                gameState = "starting";
                roundType = type;
                if (generateLayers(type)) {
                    scatterPlayers(lobbyPlayers);
                }
                else {
                    return false;
                }
            }
            else {
                // The game type in the config did not match a specified game type; return false to signify that
                return false;
            }
            // If a game creation succeeded, then,
            // Update the game's players for later
            gamePlayers = new ArrayList<>(TumbleManager.getPlayersInGame());
            // Update the round's players for later
            roundPlayers = new ArrayList<>(TumbleManager.getPlayersInGame());
            // Create a list that will later keep track of each player's wins
            gameWins = new ArrayList<>();
            gameWins.addAll(List.of(0,0,0,0,0,0,0,0));
            // Wait 5s (100t) for the clients to load in
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                // Begin the countdown sequence
                playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 1);
                displayTitles(gamePlayers, ChatColor.DARK_GREEN + "3", null, 3, 10, 7);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                    playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 1);
                    displayTitles(gamePlayers, ChatColor.YELLOW + "2", null, 3, 10, 7);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                        playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 1);
                        displayTitles(gamePlayers, ChatColor.DARK_RED + "1", null, 3, 10, 7);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                            playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 2);
                            displayTitles(gamePlayers, ChatColor.GREEN + "Go!", null, 1, 5, 1);
                            setGamemode(gamePlayers, GameMode.SURVIVAL);
                            gameState = "running";
                        }, 20);
                    }, 20);
                }, 20);
            }, 100);
        }
        return true;
    }

    /**
     * This method should be called on the death of one of the Game's players
     * @param player The player who died
     */
    public void playerDeath(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        // If there are more than 2 players in the game,
        if (roundPlayers.size() > 2) {
            // remove that player (who just died) from the roundPlayersArray, effectively eliminating them,
            roundPlayers.remove(player);
        }
        // Otherwise, the game must have two people left (and one just died), meaning it is over
        // This logic is so that it will not remove the last player standing from the list, so we know who the winner is.
        else {
            // roundPlayers.remove(player);
            // End the game, passing the winner to the gameEnd method
            roundEnd(roundPlayers.get(0));
        }
    }

    // Methods to get the game type and game state for other classes outside the Game

    /**
     * @return The round type of the current round as a String ("shovels", "snowballs")
     */
    public String getRoundType() { return roundType; }

    /**
     * @return The game's current state as a String ("starting", "running", "complete")
     */
    public String getGameState() { return gameState; }


    // BEGIN PRIVATE METHODS

    /**
     * Generates the layers in the gameWorld for a certain gameType
     * @param type can be either "shovels", "snowballs", or "mixed", anything else will fail generation
     * @return true if gameType was recognized and layers were (hopefully) generated, false if unrecognized
     */
    private boolean generateLayers(String type) {
        // Create a new Location for the layers to work with--this is so that we don't modify the actual gameSpawn var
        Location layer = new Location(gameSpawn.getWorld(), gameSpawn.getX(), gameSpawn.getY(), gameSpawn.getZ(), gameSpawn.getYaw(), gameSpawn.getPitch());
        if (Objects.equals(type, "shovels")) {
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 17, 1, Material.SNOW_BLOCK);
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 13, 1, Material.GRASS_BLOCK);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 4, 1, Material.PODZOL);
            layer.setY(layer.getY() + 2);
            Generator.generateLayer(layer, 4, 2, Material.TALL_GRASS);
            giveItems(lobbyPlayers, new ItemStack(Material.DIAMOND_SHOVEL));
        }
        else if (Objects.equals(type, "snowballs")) {
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 17, 1, Material.COAL_ORE);
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 13, 1, Material.GRANITE);
            Generator.generateLayer(layer, 4, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA);
            giveItems(lobbyPlayers, new ItemStack(Material.SNOWBALL));
        }
        else if (Objects.equals(type, "mixed")) {
            // Randomly select either shovels or snowballs and re-run the method
            if (Random.nextInt(2) == 0) {
                generateLayers("shovels");
            } else {
                generateLayers("snowballs");
            }
        }
        // Game type was invalid
        else {
            return false;
        }
        return true;
    }

    /**
     * Distributes items to a provided list of players
     * @param players The player list for which to distribute the items to
     * @param itemStack The ItemStack to be distributed
     */
    private void giveItems(List<Player> players, ItemStack itemStack) {
        for (Player aPlayer : players) {
            // Get a singular player from the player list and give that player the specified item
            aPlayer.getInventory().addItem(itemStack);
        }
    }

    /**
     * Sets the gamemodes of a provided list of players
     * @param players The player list for which to set the gamemodes of
     * @param gameMode The GameMode to set
     */
    private void setGamemode(List<Player> players, GameMode gameMode) {
        for (Player aPlayer : players) {
            // Get a singular player from the player list and set their gamemode to the specified gamemode
            aPlayer.setGameMode(gameMode);
        }
    }

    /**
     * Displays a customized title to a provided list of players
     * @param players The player list for which to show the titles to
     * @param title The top title text
     * @param subtitle The bottom title subtext (nullable)
     * @param fadeIn The fadeIn duration (in ticks)
     * @param stay The stay duration (in ticks)
     * @param fadeOut The fadeOut duration (in ticks)
     */
    private void displayTitles(List<Player> players, String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player aPlayer : players) {
            // Get a singular player from the player list and display them the specified title
            aPlayer.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * Displays a message to a provided list of players
     * @param players The player list for which to send the message to
     * @param message The provided message (String format)
     */
    private void displayMessage(List<Player> players, String message) {
        for (Player aPlayer : players) {
            aPlayer.sendMessage(message);
        }
    }

    /**
     * Plays a sound to a provided list of players
     * @param players The player list for which to play the sound to
     * @param sound The sound to play
     * @param category The category of the sound
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    private void playSound(@NotNull List<Player> players, @NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        for (Player aPlayer : players) {
            aPlayer.playSound(aPlayer, sound, category, volume, pitch);
        }
    }

    /**
     * Teleports a list of players to the specified scatter locations in the gameWorld
     * @param players a List of Players to teleport
     */
    private void scatterPlayers(List<Player> players) {
        // Get the coords of the game's spawn location
        double x = gameSpawn.getX();
        double y = gameSpawn.getY();
        double z = gameSpawn.getZ();
        // Create the scatter locations based off the game's spawn
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
        // Shuffle the list (randomize)
        Collections.shuffle(scatterLocations);
        // While there are still unteleported players from the list, teleport them
        for (Player aPlayer : players) {
            // Select a singular player and singular location from the lists and teleport that player
            aPlayer.teleport(scatterLocations.get(0));
            // Remove that location so multiple players won't get the same one
            scatterLocations.remove(0);
        }
    }

    private void roundEnd(Player winner) {
        // Set the wins of the player to their current # of wins + 1
        gameWins.set(gamePlayers.indexOf(winner), (gameWins.get(gamePlayers.indexOf(winner)) + 1));
        // Clear old layers (as a fill command, this would be /fill ~-20 ~-4 ~-20 ~20 ~ ~20 relative to spawn)
        Generator.generateCuboid(new Location(gameSpawn.getWorld(), gameSpawn.getX() - 20, gameSpawn.getY() - 4, gameSpawn.getZ() - 20), new Location(gameSpawn.getWorld(), gameSpawn.getX() + 20, gameSpawn.getY(), gameSpawn.getZ() + 20), Material.AIR);
        playSound(gamePlayers, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 5, 0);
        // If the player has three wins, they won the game, so initiate the gameEnd
        if (gameWins.get(gamePlayers.indexOf(winner)) == 3)  {
            gameEnd(winner);
        }
        // If that player doesn't have three wins, nobody else does, so we need another round
        else {
            displayTitles(gamePlayers, ChatColor.RED + "Round over!", ChatColor.GOLD + winner.getName() + " has won the round!", 5, 60, 5);
            // Re-generate layers
            generateLayers(roundType);
            // Wait 5s (100t) for tp method
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                // Re-scatter players
                gameState = "starting";
                scatterPlayers(gamePlayers);
                playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 1);
                displayTitles(gamePlayers, ChatColor.DARK_GREEN + "3", null, 3, 10, 7);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                    playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 1);
                    displayTitles(gamePlayers, ChatColor.YELLOW + "2", null, 3, 10, 7);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                        playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 1);
                        displayTitles(gamePlayers, ChatColor.DARK_RED + "1", null, 3, 10, 7);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                            playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1, 2);
                            displayTitles(gamePlayers, ChatColor.GREEN + "Go!", null, 1, 5, 1);
                            setGamemode(gamePlayers, GameMode.SURVIVAL);
                            gameState = "running";
                        }, 20);
                    }, 20);
                }, 20);
            }, 100);
        }
    }

    private void gameEnd(Player winner) {
        // Announce win
        displayTitles(gamePlayers, ChatColor.RED + "Game over!", ChatColor.GOLD + winner.getName() + " has won the game!", 5, 60, 5);
        displayMessage(gamePlayers, ChatColor.BLUE + "Returning to lobby in ten seconds...");
        // Wait 10s (200t), then
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
            // First, check to see if there is a separate location to tp the winner to
            if ((TumbleManager.getPlugin().getConfig().getDouble("winnerTeleport.x") != 0) && (TumbleManager.getPlugin().getConfig().getDouble("winnerTeleport.y") != 0)  && (TumbleManager.getPlugin().getConfig().getDouble("winnerTeleport.z") != 0)) {
                // Tp the winner to that location
                winner.teleport(new Location(Bukkit.getWorld(TumbleManager.getLobbyWorld()), TumbleManager.getPlugin().getConfig().getDouble("winnerTeleport.x"), TumbleManager.getPlugin().getConfig().getDouble("winnerTeleport.y"), TumbleManager.getPlugin().getConfig().getDouble("winnerTeleport.z")));
                // Remove the winner from the gamePlayers so they don't get double-tp'd
                gamePlayers.remove(winner);
            }
            // Send all players back to lobby (spawn)
            for (Player aPlayer : gamePlayers) {
                aPlayer.teleport(Bukkit.getWorld(TumbleManager.getLobbyWorld()).getSpawnLocation());
            }
        }, 200);
        gameState = "complete";
    }
}

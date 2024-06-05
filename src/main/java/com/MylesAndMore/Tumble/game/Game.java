package com.MylesAndMore.Tumble.game;

import com.MylesAndMore.Tumble.plugin.Constants;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Everything relating to the Tumble game
 */
public class Game {
    // Singleton class logic
    private static Game gameInstance;
    private Game() {
        gameWorld = Bukkit.getWorld(Constants.getGameWorld());
        gameSpawn = Objects.requireNonNull(gameWorld).getSpawnLocation();
    }
    public static Game getGame() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }
        return gameInstance;
    }

    // Define local game vars
    private String gameState;
    private String gameType;
    private int gameID = -1;
    private int autoStartID = -1;
    private final World gameWorld;
    private final Location gameSpawn;
    private List<Player> gamePlayers;
    private List<Player> roundPlayers;
    private List<Integer> gameWins;

    private final Random Random = new Random();

    /**
     * Creates a new Game
     * @param type The type of game
     * @return true if the game succeeds creation, and false if not
     */
    public boolean startGame(@NotNull String type) {
        // Check if the game is starting or running
        if (Objects.equals(gameState, "starting")) { return false; }
        else if (Objects.equals(gameState, "running")) { return false; }
        else {
            // Define the gameType
            switch (type) {
                case "shovels", "snowballs", "mixed" -> {
                    gameState = "starting";
                    // Set the type to gameType since it won't change for this mode
                    gameType = type;
                    // Clear the players' inventories so they can't bring any items into the game
                    clearInventories(Constants.getPlayersInLobby());
                    // Generate the correct layers for a Shovels game
                    // The else statement is just in case the generator fails; this command will fail
                    if (generateLayers(type)) {
                        // Send all players from lobby to the game
                        scatterPlayers(Constants.getPlayersInLobby());
                    } else {
                        return false;
                    }
                }
                default -> {
                    // The game type in the config did not match a specified game type
                    return false;
                }
            }
            // Update the game/round players for later
            gamePlayers = new ArrayList<>(Constants.getPlayersInGame());
            roundPlayers = new ArrayList<>(Constants.getPlayersInGame());
            // Create a list that will later keep track of each player's wins
            gameWins = new ArrayList<>();
            gameWins.addAll(List.of(0,0,0,0,0,0,0,0));
            // Put all players in spectator to prevent them from getting kicked for flying (this needs a delay bc servers are slow)
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> setGamemode(gamePlayers, GameMode.SPECTATOR), 25);
            // Wait 5s (100t) for the clients to load in
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                // Begin the countdown sequence
                playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                displayTitles(gamePlayers, ChatColor.DARK_GREEN + "3", null, 3, 10, 7);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                    playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                    displayTitles(gamePlayers, ChatColor.YELLOW + "2", null, 3, 10, 7);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                        playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                        displayTitles(gamePlayers, ChatColor.DARK_RED + "1", null, 3, 10, 7);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                            playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 2);
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
     * Initiates an automatic start of a Tumble game
     */
    public void autoStart() {
        // Wait for the player to load in
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
            gameState = "waiting";
            displayActionbar(Constants.getPlayersInLobby(), ChatColor.GREEN + "Game will begin in 15 seconds!");
            playSound(Constants.getPlayersInLobby(), Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 1);
            Constants.getMVWorldManager().loadWorld(Constants.getGameWorld());
            // Schedule a process to start the game in 300t (15s) and save the PID so we can cancel it later if needed
            autoStartID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> startGame(Constants.getGameType()), 300);
        }, 50);
    }

    /**
     * Cancels a "waiting" automatic start
     */
    public void cancelStart() {
        Bukkit.getServer().getScheduler().cancelTask(Game.getGame().getAutoStartID());
        displayActionbar(Constants.getPlayersInLobby(), ChatColor.RED + "Game start cancelled!");
        playSound(Constants.getPlayersInLobby(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, 1);
        gameState = null;
        autoStartID = -1;
    }

    /**
     * This method should be called on the death of one of the Game's players
     * @param player The player who died
     */
    public void playerDeath(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        // Add a delay to tp them to the gameWorld just in case they have a bed in another world (yes you Jacob)
        // Delay is needed because instant respawn is a lie (it's not actually instant)
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
            player.teleport(gameSpawn);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> player.setGameMode(GameMode.SPECTATOR), 5);
        }, 5);
        // remove that player (who just died) from the roundPlayersArray, effectively eliminating them,
        roundPlayers.remove(player);
        // If there are less than 2 players in the game (1 just died),
        if (roundPlayers.size() < 2) {
            roundEnd(roundPlayers.get(0));
        }
    }

    // Methods to get the game type and game state for other classes outside the Game

    /**
     * @return The game's current state as a String ("waiting", "starting", "running", "complete")
     * Can also be null if not initialized.
     */
    public String getGameState() { return gameState; }

    /**
     * @return The Bukkit process ID of the autostart process, if applicable
     * Can also be null if not initialized, or -1 if the process failed to schedule.
     */
    public int getAutoStartID() { return autoStartID; }


    private final Layers layers = new Layers();
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
            // Choose a random type of generation; a circular layer, a square layer, or a multi-tiered layer of either variety
            if (Random.nextInt(4) == 0) {
                // Circular layer
                Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.SNOW_BLOCK), layers.getSafeMaterialList());
            }
            else if (Random.nextInt(4) == 1) {
                // Square layer
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.SNOW_BLOCK), layers.getSafeMaterialList());
            }
            else if (Random.nextInt(4) == 2) {
                // Multi-tiered circle
                Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.SNOW_BLOCK), layers.getSafeMaterialList());
                Generator.generateLayer(layer, 13, 1, Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateLayer(layer, 13, 1, Material.GRASS_BLOCK), layers.getMaterialList());
                Generator.generateLayer(layer, 4, 1, Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateLayer(layer, 4, 1, Material.PODZOL), layers.getMaterialList());
            }
            else {
                // Multi-tiered square
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.SNOW_BLOCK), layers.getSafeMaterialList());
                Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.GRASS_BLOCK), layers.getMaterialList());
                Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.PODZOL), layers.getMaterialList());
            }
            ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
            shovel.addEnchantment(Enchantment.SILK_TOUCH, 1);
            if (Objects.equals(gameState, "running")) {
                giveItems(Constants.getPlayersInGame(), shovel);
            }
            else if (Objects.equals(gameState, "starting")) {
                giveItems(Constants.getPlayersInLobby(), shovel);
            }
            // Schedule a process to give snowballs after 2m30s (so people can't island, the OG game had this); add 160t because of the countdown
            gameID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                clearInventories(gamePlayers);
                giveItems(gamePlayers, new ItemStack(Material.SNOWBALL));
                displayActionbar(gamePlayers, ChatColor.DARK_RED + "Showdown!");
                playSound(gamePlayers, Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1, 1);
                // End the round in another 2m30s
                gameID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> roundEnd(null), 3000);
            }, 3160);
        }
        else if (Objects.equals(type, "snowballs")) {
            layer.setY(layer.getY() - 1);
            // Similar generation to shovels, except there are three layers
            if (Random.nextInt(4) == 0) {
                // Circular layer
                Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getSafeMaterialList());
                layer.setY(layer.getY() - 6);
                Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getMaterialList());
                layer.setY(layer.getY() - 6);
                Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getMaterialList());
            }
            else if (Random.nextInt(4) == 1) {
                // Square layer
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getSafeMaterialList());
                layer.setY(layer.getY() - 6);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getMaterialList());
                layer.setY(layer.getY() - 6);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getMaterialList());
            }
            else if (Random.nextInt(4) == 2) {
                // Multi-tiered circle
                Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getSafeMaterialList());
                Generator.generateLayer(layer, 13, 1, Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateLayer(layer, 13, 1, Material.GRANITE), layers.getMaterialList());
                Generator.generateLayer(layer, 4, 1, Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
                layer.setY(layer.getY() - 6);

                Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getSafeMaterialList());
                Generator.generateLayer(layer, 13, 1, Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateLayer(layer, 13, 1, Material.GRANITE), layers.getMaterialList());
                Generator.generateLayer(layer, 4, 1, Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
                layer.setY(layer.getY() - 6);

                Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getSafeMaterialList());
                Generator.generateLayer(layer, 13, 1, Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateLayer(layer, 13, 1, Material.GRANITE), layers.getMaterialList());
                Generator.generateLayer(layer, 4, 1, Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
            }
            else {
                // Multi-tiered square
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getSafeMaterialList());
                Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.GRANITE), layers.getMaterialList());
                Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
                layer.setY(layer.getY() - 6);

                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getSafeMaterialList());
                Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.GRANITE), layers.getMaterialList());
                Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
                layer.setY(layer.getY() - 6);

                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getSafeMaterialList());
                Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.GRANITE), layers.getMaterialList());
                Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.AIR);
                layer.setY(layer.getY() - 1);
                Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
            }
            if (Objects.equals(gameState, "running")) {
                giveItems(Constants.getPlayersInGame(), new ItemStack(Material.SNOWBALL));
            }
            else if (Objects.equals(gameState, "starting")) {
                giveItems(Constants.getPlayersInLobby(), new ItemStack(Material.SNOWBALL));
            }
            // End the round in 5m
            gameID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> roundEnd(null), 6160);
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
            aPlayer.getInventory().addItem(itemStack);
        }
    }

    /**
     * Clears the inventories of a provided player list
     * @param players The player list for which to clear the inventories of
     */
    private void clearInventories(List<Player> players) {
        for (Player aPlayer : players) {
            aPlayer.getInventory().clear();
        }
    }

    /**
     * Sets the gamemodes of a provided list of players
     * @param players The player list for which to set the gamemodes of
     * @param gameMode The GameMode to set
     */
    private void setGamemode(List<Player> players, GameMode gameMode) {
        for (Player aPlayer : players) {
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
            aPlayer.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * Displays an actionbar message to a provided list of players
     * @param players The player list for which to display the actionbar to
     * @param message The provided message (String format)
     */
    private void displayActionbar(List<Player> players, String message) {
        for (Player aPlayer : players) {
            aPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
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
            aPlayer.playSound(aPlayer.getLocation(), sound, category, volume, pitch);
        }
    }

    /**
     * Teleports a list of players to the specified scatter locations in the gameWorld
     * @param players a List of Players to teleport
     */
    private void scatterPlayers(List<Player> players) {
        double x = gameSpawn.getX();
        double y = gameSpawn.getY();
        double z = gameSpawn.getZ();
        // Create the scatter locations based off the game's spawn
        List<Location> scatterLocations = new ArrayList<>(List.of(
                new Location(gameWorld, (x - 14.5), y, (z + 0.5), -90, 0),
                new Location(gameWorld, (x + 0.5), y, (z - 14.5), 0, 0),
                new Location(gameWorld, (x + 15.5), y, (z + 0.5), 90, 0),
                new Location(gameWorld, (x + 0.5), y, (z + 15.5), 180, 0),
                new Location(gameWorld, (x - 10.5), y, (z - 10.5), -45, 0),
                new Location(gameWorld, (x - 10.5), y, (z + 11.5), -135, 0),
                new Location(gameWorld, (x + 11.5), y, (z - 10.5), 45, 0),
                new Location(gameWorld, (x + 11.5), y, (z + 11.5), 135, 0)));
        Collections.shuffle(scatterLocations);
        for (Player aPlayer : players) {
            aPlayer.teleport(scatterLocations.get(0));
            scatterLocations.remove(0); // Remove that location so multiple players won't get the same one
        }
    }

    private void roundEnd(@Nullable Player winner) {
        // Cancel the tasks that auto-end the round
        Bukkit.getServer().getScheduler().cancelTask(gameID);
        // Clear old layers (as a fill command, this would be /fill ~-20 ~-20 ~-20 ~20 ~ ~20 relative to spawn)
        Generator.generateCuboid(new Location(gameSpawn.getWorld(), gameSpawn.getX() - 20, gameSpawn.getY() - 20, gameSpawn.getZ() - 20), new Location(gameSpawn.getWorld(), gameSpawn.getX() + 20, gameSpawn.getY(), gameSpawn.getZ() + 20), Material.AIR);
        playSound(gamePlayers, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 5, 0);
        // Check if there was a definite winner or not
        if (winner != null) {
            // Set the wins of the player to their current # of wins + 1
            gameWins.set(gamePlayers.indexOf(winner), (gameWins.get(gamePlayers.indexOf(winner)) + 1));
            // If the player has three wins, they won the game, so initiate the gameEnd
            if (gameWins.get(gamePlayers.indexOf(winner)) == 3)  {
                gameEnd(winner);
            }
            // If that player doesn't have three wins, nobody else does, so we need another round
            else {
                roundPlayers.get(0).setGameMode(GameMode.SPECTATOR);
                roundPlayers.remove(0);
                roundPlayers.addAll(gamePlayers);
                clearInventories(gamePlayers);
                displayTitles(gamePlayers, ChatColor.RED + "Round over!", ChatColor.GOLD + winner.getName() + " has won the round!", 5, 60, 5);
                // Wait for the player to respawn before completely lagging the server ._.
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                    generateLayers(gameType);
                    // Wait 5s (100t) for tp method
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                        // Kill all items (pistons are weird)
                        for (Entity entity : gameWorld.getEntities()) {
                            if (entity instanceof Item) {
                                entity.remove();
                            }
                        }
                        gameState = "starting";
                        scatterPlayers(gamePlayers);
                        playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                        displayTitles(gamePlayers, ChatColor.DARK_GREEN + "3", null, 3, 10, 7);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                            playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                            displayTitles(gamePlayers, ChatColor.YELLOW + "2", null, 3, 10, 7);
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                                playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                                displayTitles(gamePlayers, ChatColor.DARK_RED + "1", null, 3, 10, 7);
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                                    playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 2);
                                    displayTitles(gamePlayers, ChatColor.GREEN + "Go!", null, 1, 5, 1);
                                    setGamemode(gamePlayers, GameMode.SURVIVAL);
                                    gameState = "running";
                                }, 20);
                            }, 20);
                        }, 20);
                    }, 100);
                }, 1);
            }
        }
        else {
            setGamemode(gamePlayers, GameMode.SPECTATOR);
            roundPlayers.clear();
            roundPlayers.addAll(gamePlayers);
            clearInventories(gamePlayers);
            displayTitles(gamePlayers, ChatColor.RED + "Round over!", ChatColor.GOLD + "Draw!", 5, 60, 5);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                generateLayers(gameType);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                    for (Entity entity : gameWorld.getEntities()) {
                        if (entity instanceof Item) {
                            entity.remove();
                        }
                    }
                    gameState = "starting";
                    scatterPlayers(gamePlayers);
                    playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                    displayTitles(gamePlayers, ChatColor.DARK_GREEN + "3", null, 3, 10, 7);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                        playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                        displayTitles(gamePlayers, ChatColor.YELLOW + "2", null, 3, 10, 7);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                            playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                            displayTitles(gamePlayers, ChatColor.DARK_RED + "1", null, 3, 10, 7);
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
                                playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 2);
                                displayTitles(gamePlayers, ChatColor.GREEN + "Go!", null, 1, 5, 1);
                                setGamemode(gamePlayers, GameMode.SURVIVAL);
                                gameState = "running";
                            }, 20);
                        }, 20);
                    }, 20);
                }, 100);
            }, 1);
        }
    }

    private void gameEnd(Player winner) {
        winner.setGameMode(GameMode.SPECTATOR);
        clearInventories(gamePlayers);
        displayTitles(gamePlayers, ChatColor.RED + "Game over!", ChatColor.GOLD + winner.getName() + " has won the game!", 5, 60, 5);
        displayActionbar(gamePlayers, ChatColor.BLUE + "Returning to lobby in ten seconds...");
        // Wait 10s (200t), then
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> {
            // First, check to see if there is a separate location to tp the winner to
            if ((Constants.getPlugin().getConfig().getDouble("winnerTeleport.x") != 0) && (Constants.getPlugin().getConfig().getDouble("winnerTeleport.y") != 0)  && (Constants.getPlugin().getConfig().getDouble("winnerTeleport.z") != 0)) {
                winner.teleport(new Location(Bukkit.getWorld(Constants.getLobbyWorld()), Constants.getPlugin().getConfig().getDouble("winnerTeleport.x"), Constants.getPlugin().getConfig().getDouble("winnerTeleport.y"), Constants.getPlugin().getConfig().getDouble("winnerTeleport.z")));
                // Remove the winner from the gamePlayers so they don't get double-tp'd
                gamePlayers.remove(winner);
            }
            // Send all players back to lobby (spawn)
            for (Player aPlayer : gamePlayers) {
                aPlayer.teleport(Objects.requireNonNull(Bukkit.getWorld(Constants.getLobbyWorld())).getSpawnLocation());
            }
        }, 200);
        gameState = "complete";
    }
}

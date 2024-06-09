package com.MylesAndMore.Tumble.game;

import com.MylesAndMore.Tumble.plugin.ConfigManager;
import com.MylesAndMore.Tumble.plugin.GameState;
import com.MylesAndMore.Tumble.plugin.GameType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.MylesAndMore.Tumble.Main.plugin;

/**
 * Everything relating to the Tumble game
 */
public class Game {

    public final GameType type;
    public final Arena arena;
    public final World gameWorld;
    public final List<Player> gamePlayers = new ArrayList<>();
    private final Location gameSpawn;
    private final HashMap<Player, Integer> gameWins = new HashMap<>();
    public GameState gameState = GameState.WAITING;
    public GameType roundType;
    private int gameID = -1;
    private int autoStartID = -1;
    private List<Player> playersAlive;
    private EventListener eventListener;

    public Game(@NotNull Arena arena, @NotNull GameType type) {
        this.arena = arena;
        this.type = type;
        this.gameWorld = arena.world;
        this.gameSpawn = arena.location;

    }

    /**
     * Creates a new Game
     */
    public void startGame() {

        // Check if the game is starting or running
        if (gameState != GameState.WAITING) {
            return;
        }

        Bukkit.getServer().getScheduler().cancelTask(autoStartID);
        autoStartID = -1;
//        if (waitingPlayers.size() < 2) {
//            return false;
//        }

        eventListener = new EventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(eventListener, plugin);

        // clear area in case it did not get properly cleared
        roundStart();
    }

    /**
     * Starts a new round
     */
    private void roundStart() {
        gameState = GameState.STARTING;
        playersAlive = new ArrayList<>(gamePlayers);
        // Put all players in spectator to prevent them from getting kicked for flying
        setGamemode(gamePlayers, GameMode.SPECTATOR);
        scatterPlayers(gamePlayers);
        clearInventories(gamePlayers);
        clearArena();
        prepareGameType(type);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            // Begin the countdown sequence
            countdown(() -> {
                setGamemode(gamePlayers, GameMode.SURVIVAL);
                gameState = GameState.RUNNING;
            });
        }, 100);
    }

    /**
     * Type specific setup: Generating layers and giving items
     * @param type can be either "shovels", "snowballs", or "mixed"
     */
    private void prepareGameType(GameType type) {
        roundType = type; // note: may need deepcopy this for it to work properly
        if (roundType.equals(GameType.MIXED)) {
            // Randomly select either shovels or snowballs and re-run the method
            Random random = new Random();
            switch (random.nextInt(2)) {
                case 0 -> roundType = GameType.SHOVELS;
                case 1 -> roundType = GameType.SNOWBALLS;
            }
        }

        switch (roundType) {
            case SHOVELS -> {
                Generator.generateLayersShovels(gameSpawn.clone());
                ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
                shovel.addEnchantment(Enchantment.SILK_TOUCH, 1);
                giveItems(gamePlayers, shovel);
                // Schedule a process to give snowballs after 2m30s (so people can't island, the OG game had this); add 160t because of the countdown
                gameID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    clearInventories(gamePlayers);
                    giveItems(gamePlayers, new ItemStack(Material.SNOWBALL));
                    displayActionbar(gamePlayers, ChatColor.DARK_RED + "Showdown!");
                    playSound(gamePlayers, Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1, 1);
                    // End the round in another 2m30s
                    gameID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::roundEnd, 3000);
                }, 3160);
            }
            case SNOWBALLS -> {
                Generator.generateLayersSnowballs(gameSpawn.clone());
                giveItems(gamePlayers, new ItemStack(Material.SNOWBALL));

                // End the round in 5m
                gameID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::roundEnd, 6160);
            }
        }
    }

    /**
     * Round end stuff: Finds and displays winner, starts next round if necessary
     */
    private void roundEnd() {
        // Cancel the tasks that auto-end the round
        Bukkit.getServer().getScheduler().cancelTask(gameID);
        // Clear old layers (as a fill command, this would be /fill ~-20 ~-20 ~-20 ~20 ~ ~20 relative to spawn)
        playSound(gamePlayers, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 5, 0);
        // Check if there was a definite winner or not
        if (!playersAlive.isEmpty()) {
            Player winner = playersAlive.get(0);
            // Set the wins of the player to their current # of wins + 1
            if (!gameWins.containsKey(winner)) {
                gameWins.put(winner, 0);
            }
            gameWins.put(winner, gameWins.get(winner)+1);
            if (gameWins.get(winner) == 3) {
                gameEnd();
            }
            // If that player doesn't have three wins, nobody else does, so we need another round
            else {
                displayTitles(gamePlayers, ChatColor.RED + "Round over!", ChatColor.GOLD + winner.getName() + " has won the round!", 5, 60, 5);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::roundStart, 100);
            }
        }
        else {
            displayTitles(gamePlayers, ChatColor.RED + "Round over!", ChatColor.GOLD + "Draw!", 5, 60, 5);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::roundStart, 100);
        }
    }

    /**
     * Game end stuff: Displays overall winner and teleports players to lobby
     */
    private void gameEnd() {
        if (!gamePlayers.isEmpty()) {
            Player winner = getPlayerWithMostWins(gameWins);
            setGamemode(gamePlayers, GameMode.SPECTATOR);
            clearInventories(gamePlayers);
            if (winner != null) {
                displayTitles(gamePlayers, ChatColor.RED + "Game over!", ChatColor.GOLD + winner.getName() + " has won the game!", 5, 60, 5);
            }
            displayActionbar(gamePlayers, ChatColor.BLUE + "Returning to lobby in ten seconds...");
            // Wait 10s (200t), then
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                // First, check to see if there is a separate location to tp the winner to
                if (ConfigManager.winnerLobby != null && winner != null) {
                    winner.teleport(ConfigManager.winnerLobby);
                    // Remove the winner from the game so they don't get double-tp'd
                    gamePlayers.remove(winner);
                }
                // Send all players back to lobby (spawn)
                for (Player aPlayer : gamePlayers) {
                    aPlayer.teleport(Objects.requireNonNull(ConfigManager.lobby));
                }
            }, 200);
        }
        HandlerList.unregisterAll(eventListener);
        arena.game = null;
    }

    /**
     * Force stops a game
     */
    public void killGame() {
        Bukkit.getServer().getScheduler().cancelTask(gameID);
        HandlerList.unregisterAll(eventListener);
        arena.game = null;
    }

    /**
     * Removes a player from the game
     * Called when a player leaves the server, or if they issue the leave command
     * @param p Player to remove
     */
    public void removePlayer(Player p) {
        gamePlayers.remove(p);
        if (gamePlayers.size() < 2) {
            gameEnd();
        }
        p.teleport(ConfigManager.lobby);
    }

    public void addPlayer(Player p) {
        gamePlayers.add(p);
        if (gamePlayers.size() >= 2 && gameState == GameState.WAITING) {
            autoStart();
        }
    }

    /**
     * Initiates an automatic start of a Tumble game
     */
    public void autoStart() {
        // Wait for the player to load in
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            displayActionbar(gamePlayers, ChatColor.GREEN + "Game will begin in 15 seconds!");
            playSound(gamePlayers, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 1);
            // Schedule a process to start the game in 300t (15s) and save the PID so we can cancel it later if needed
            autoStartID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::startGame, 300);
        }, 50);
    }

    /**
     * This method should be called on the death of one of the Game's players
     * @param player The player who died
     */
    public void playerDeath(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        // remove that player (who just died) from the roundPlayersArray, effectively eliminating them,
        playersAlive.remove(player);
        // If there are less than 2 players in the game (1 just died),
        if (playersAlive.size() < 2) {
            roundEnd();
        }
    }

    // utility functions

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
            if (!aPlayer.teleport(scatterLocations.get(0))) {
                plugin.getLogger().info("dbg: FAILED TELEPORT");
            }
            scatterLocations.remove(0); // Remove that location so multiple players won't get the same one
        }
    }

    /**
     * Displays the 3, 2, 1 countdown
     * @param doAfter Will be executed after the countdown
     */
    private void countdown(Runnable doAfter) {
        playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
        displayTitles(gamePlayers, ChatColor.DARK_GREEN + "3", null, 3, 10, 7);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
            displayTitles(gamePlayers, ChatColor.YELLOW + "2", null, 3, 10, 7);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                displayTitles(gamePlayers, ChatColor.DARK_RED + "1", null, 3, 10, 7);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 2);
                    displayTitles(gamePlayers, ChatColor.GREEN + "Go!", null, 1, 5, 1);
                    doAfter.run();
                }, 20);
            }, 20);
        }, 20);
    }

    /**
     * Finds the player with the most wins
     * @param list List of players and their number of wins
     * @return Player with the most wins
     */
    private Player getPlayerWithMostWins(HashMap<Player, Integer> list) {
        Player largest = null;
        for (Player p: list.keySet()) {
            if (largest == null || list.get(p) > list.get(largest)) {
                largest = p;
            }
        }
        return largest;
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
     * Clears old layers
     * (as a fill command, this would be /fill ~-20 ~-20 ~-20 ~20 ~ ~20 relative to spawn)
     */
    private void clearArena() {
        Generator.generateCuboid(
                new Location(gameSpawn.getWorld(), gameSpawn.getX() - 20, gameSpawn.getY() - 20, gameSpawn.getZ() - 20),
                new Location(gameSpawn.getWorld(), gameSpawn.getX() + 20, gameSpawn.getY(), gameSpawn.getZ() + 20),
                Material.AIR);
    }
}

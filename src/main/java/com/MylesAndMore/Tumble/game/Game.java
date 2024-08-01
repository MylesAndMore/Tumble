package com.MylesAndMore.Tumble.game;

import com.MylesAndMore.Tumble.config.ConfigManager;
import com.MylesAndMore.Tumble.config.LanguageManager;
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
    private final Location gameSpawn;
    public final List<Player> gamePlayers = new ArrayList<>();
    private final HashMap<Player, Integer> gameWins = new HashMap<>();
    private final HashMap<Player, ItemStack[]> inventories = new HashMap<>();
    public GameState gameState = GameState.WAITING;
    public GameType roundType;
    private int gameID = -1;
    private int autoStartID = -1;
    private List<Player> playersAlive;
    private EventListener eventListener;

    /**
     * Create a new Game
     * @param arena The arena the game is taking place in
     * @param type The game type
     */
    public Game(@NotNull Arena arena, @NotNull GameType type) {
        this.arena = arena;
        this.type = type;
        this.gameSpawn = Objects.requireNonNull(arena.gameSpawn);
    }

    /**
     * Adds a player to the wait area. Called from /tumble join
     * Precondition: the game is in state WAITING
     * @param p Player to add
     */
    public void addPlayer(Player p) {
        gamePlayers.add(p);

        if (arena.waitArea != null) {
            inventories.put(p,p.getInventory().getContents());
            p.teleport(arena.waitArea);
            p.getInventory().clear();
        }
        if (gamePlayers.size() >= 2 && gameState == GameState.WAITING) {
            autoStart();
        } else {
            displayActionbar(Collections.singletonList(p), LanguageManager.fromKeyNoPrefix("waiting-for-players"));
        }
    }

    /**
     * Starts the game
     * Called from /tumble forceStart or after the wait counter finishes
     */
    public void gameStart() {

        // Check if the game is starting or running
        if (gameState != GameState.WAITING) {
            return;
        }

        // Cancel wait timer
        Bukkit.getServer().getScheduler().cancelTask(autoStartID);
        autoStartID = -1;

        // Register event listener
        eventListener = new EventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(eventListener, plugin);

        // Save inventories (if not already done)
        for (Player p : gamePlayers) {
            if (!inventories.containsKey(p)) {
                inventories.put(p, p.getInventory().getContents());
            }
        }

        roundStart();
    }

    /**
     * Starts a round
     */
    private void roundStart() {
        gameState = GameState.STARTING;
        playersAlive = new ArrayList<>(gamePlayers);

        scatterPlayers(gamePlayers);
        // Put all players in spectator to prevent them from getting kicked for flying
        setGamemode(gamePlayers, GameMode.SPECTATOR);
        // Do it again in a bit in case they were not in the world yet
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> setGamemode(gamePlayers, GameMode.SPECTATOR), 10);

        clearInventories(gamePlayers);
        clearArena();
        prepareGameType(type);

        // Begin the countdown sequence
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> countdown(() -> {
            setGamemode(gamePlayers, GameMode.SURVIVAL);
            gameState = GameState.RUNNING;
        }), 100);
    }

    /**
     * Type specific setup: Generating layers and giving items
     * @param type game type,
     */
    private void prepareGameType(GameType type) {
        roundType = type;
        switch (type) {
            case SHOVELS -> {
                Generator.generateLayersShovels(gameSpawn.clone());

                ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
                shovel.addEnchantment(Enchantment.SILK_TOUCH, 1);
                giveItems(gamePlayers, shovel);

                // Schedule a process to give snowballs after 2m30s (so people can't island, the OG game had this);
                // Also add 160t because of the countdown
                gameID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    clearInventories(gamePlayers);
                    giveItems(gamePlayers, new ItemStack(Material.SNOWBALL));
                    displayActionbar(gamePlayers, LanguageManager.fromKeyNoPrefix("showdown"));
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
            case MIXED -> {
                Random random = new Random();
                switch (random.nextInt(2)) {
                    case 0 -> prepareGameType(GameType.SHOVELS);
                    case 1 -> prepareGameType(GameType.SNOWBALLS);
                }
            }
        }
    }

    /**
     * Ends round: Finds and displays winner, starts next round if necessary
     */
    private void roundEnd() {
        // Cancel the tasks that auto-end the round
        gameState = GameState.ENDING;
        Bukkit.getServer().getScheduler().cancelTask(gameID);
        gameID = -1;

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
            } else { // If that player doesn't have three wins, nobody else does, so we need another round
                displayTitles(gamePlayers, LanguageManager.fromKeyNoPrefix("round-over"), LanguageManager.fromKeyNoPrefix("round-winner").replace("%winner%", winner.getDisplayName()), 5, 60, 5);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::roundStart, 100);
            }
        } else {
            displayTitles(gamePlayers, LanguageManager.fromKeyNoPrefix("round-over"), LanguageManager.fromKeyNoPrefix("round-draw"), 5, 60, 5);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::roundStart, 100);
        }
    }

    /**
     * Ends game: Displays overall winner and teleports players to lobby
     */
    private void gameEnd() {
        if (!gamePlayers.isEmpty()) {

            setGamemode(gamePlayers, GameMode.SPECTATOR);
            clearInventories(gamePlayers);

            // Display winner
            Player winner = getPlayerWithMostWins(gameWins);
            if (winner != null) {
                displayTitles(gamePlayers, LanguageManager.fromKeyNoPrefix("game-over"), LanguageManager.fromKeyNoPrefix("game-winner").replace("%winner%",winner.getDisplayName()), 5, 60, 5);
            }

            displayActionbar(gamePlayers, LanguageManager.fromKeyNoPrefix("lobby-in-10"));
            // Wait 10s (200t), then clear the arena and teleport players back
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                clearArena();
                for (Player p : gamePlayers) {
                    sendToLobby(p, p == winner);
                }
            }, 200);
        }

        Bukkit.getServer().getScheduler().cancelTask(gameID);
        gameID = -1;
        Bukkit.getServer().getScheduler().cancelTask(autoStartID);
        autoStartID = -1;
        HandlerList.unregisterAll(eventListener);
        arena.game = null;
    }

    /**
     * Stops the game, usually while it is still going
     * Called if too many players leave, or from /tumble forceStop
     */
    public void stopGame() {
        // A new list must be created to avoid removing elements while iterating
        List<Player> players = new ArrayList<>(gamePlayers);
        players.forEach(this::removePlayer);

        Bukkit.getServer().getScheduler().cancelTask(gameID);
        gameID = -1;
        Bukkit.getServer().getScheduler().cancelTask(autoStartID);
        autoStartID = -1;
        HandlerList.unregisterAll(eventListener);
        arena.game = null;
    }

    /**
     * Removes a player from the game.
     * Called when a player leaves the server, or if they issue the leave command
     * @param p Player to remove
     */
    public void removePlayer(Player p) {
        gamePlayers.remove(p);

        // Check if the game has not started yet
        if (gameState == GameState.WAITING) {
            // Inform player that there are no longer enough players to start
            if (gamePlayers.size() < 2) {
                displayActionbar(gamePlayers, LanguageManager.fromKeyNoPrefix("waiting-for-players"));
            }

            sendToLobby(p, false);
        } else {
            // Stop the game if there are no longer enough players
            if (gamePlayers.size() < 2) {
                stopGame();
            }

            sendToLobby(p, false); // You can never win if you quit, remember that kids!!
        }
    }

    /**
     * Attempts to initiate an automatic start of a Tumble game
     */
    public void autoStart() {
        int waitDuration = ConfigManager.waitDuration;
        if (waitDuration <= 0) { return; }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            displayActionbar(gamePlayers, LanguageManager.fromKeyNoPrefix("time-till-start").replace("%wait%",waitDuration+""));
            playSound(gamePlayers, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 1);
            // Schedule a process to start the game in the specified waitDuration and save the PID so we can cancel it later if needed
            autoStartID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::gameStart, waitDuration * 20L);
        }, 50);
    }

    /**
     * This method should be called on the death of one of the Game's players
     * @param player The player who died
     */
    public void playerDeath(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        // Remove that player (who just died) from the alive players, effectively eliminating them,
        playersAlive.remove(player);
        // If there are less than 2 players in the game (1 just died),
        if (playersAlive.size() < 2 && gameState == GameState.RUNNING) {
            roundEnd();
        }
    }

    // -- Utility functions --

    /**
     * Teleports a list of players to the specified scatter locations in the gameWorld
     * @param players a List of Players to teleport
     */
    private void scatterPlayers(List<Player> players) {
        double x = gameSpawn.getX();
        double y = gameSpawn.getY();
        double z = gameSpawn.getZ();
        World gameWorld = gameSpawn.getWorld();
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

    /**
     * Displays the 3, 2, 1 countdown
     * @param doAfter Will be executed after the countdown
     */
    private void countdown(Runnable doAfter) {
        playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
        displayTitles(gamePlayers, LanguageManager.fromKeyNoPrefix("count-3"), null, 3, 10, 7);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
            displayTitles(gamePlayers, LanguageManager.fromKeyNoPrefix("count-2"), null, 3, 10, 7);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                displayTitles(gamePlayers, LanguageManager.fromKeyNoPrefix("count-1"), null, 3, 10, 7);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 2);
                    displayTitles(gamePlayers, LanguageManager.fromKeyNoPrefix("count-go"), null, 1, 5, 1);
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
        players.forEach(player -> player.playSound(player.getLocation(), sound, category, volume, pitch));
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

    /**
     * Teleports a player to the lobby and restores their inventory
     * @param p Player to teleport
     * @param winner Whether the player is the winner
     */
    private void sendToLobby(Player p, boolean winner) {
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        if (winner && arena.winnerLobby != null) {
            p.teleport(arena.winnerLobby);
        } else {
            // Use default world spawn if lobby is not set
            if (arena.lobby == null) {
                p.teleport(Objects.requireNonNull(Bukkit.getWorlds().get(0)).getSpawnLocation());
            } else {
                p.teleport(Objects.requireNonNull(arena.lobby));
            }
        }
        if (inventories.containsKey(p)) {
            p.getInventory().setContents(inventories.get(p));
        }
    }
}

package com.MylesAndMore.Tumble.game;

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

import static com.MylesAndMore.Tumble.Main.*;

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
     * Adds a player to the wait area. Called from /tmbl join
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
        }
        else {
            displayActionbar(Collections.singletonList(p), languageManager.fromKeyNoPrefix("waiting-for-players"));
        }
    }

    /**
     * Starts the game
     * Called from /tmbl forceStart or after the wait counter finishes
     */
    public void gameStart() {

        // Check if the game is starting or running
        if (gameState != GameState.WAITING) {
            return;
        }

        // cancel wait timer
        Bukkit.getServer().getScheduler().cancelTask(autoStartID);
        autoStartID = -1;

        // register event listener
        eventListener = new EventListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(eventListener, plugin);

        // save inventories (if not already done)
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
        // do it again in case they were not in the world yet
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            setGamemode(gamePlayers, GameMode.SPECTATOR);
        }, 10);

        clearInventories(gamePlayers);
        clearArena();
        prepareGameType(type);

        // Begin the countdown sequence
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            countdown(() -> {
                setGamemode(gamePlayers, GameMode.SURVIVAL);
                gameState = GameState.RUNNING;
            });
        }, 100);
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
                // add 160t because of the countdown
                gameID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    clearInventories(gamePlayers);
                    giveItems(gamePlayers, new ItemStack(Material.SNOWBALL));
                    displayActionbar(gamePlayers, languageManager.fromKeyNoPrefix("showdown"));
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
            }
            else { // If that player doesn't have three wins, nobody else does, so we need another round
                displayTitles(gamePlayers, languageManager.fromKeyNoPrefix("round-over"), languageManager.fromKeyNoPrefix("round-winner").replace("%winner%", winner.getDisplayName()), 5, 60, 5);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::roundStart, 100);
            }
        }
        else {
            displayTitles(gamePlayers, languageManager.fromKeyNoPrefix("round-over"), languageManager.fromKeyNoPrefix("round-draw"), 5, 60, 5);
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

            // display winner
            Player winner = getPlayerWithMostWins(gameWins);
            if (winner != null) {
                displayTitles(gamePlayers, languageManager.fromKeyNoPrefix("game-over"), languageManager.fromKeyNoPrefix("game-winner").replace("%winner%",winner.getDisplayName()), 5, 60, 5);
            }

            displayActionbar(gamePlayers, languageManager.fromKeyNoPrefix("lobby-in-10"));

            // Wait 10s (200t), then
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                clearArena();

                // teleport player back and restore inventory
                for (Player p : gamePlayers) {
                    if (p == winner && arena.winnerLobby != null) {
                        p.teleport(arena.winnerLobby);
                    }
                    else {
                        p.teleport(Objects.requireNonNull(arena.lobby));
                    }

                    if (inventories.containsKey(p)) {
                        p.getInventory().setContents(inventories.get(p));
                    }
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
     * called if too many players leave, or from /tmbl forceStop
     */
    public void stopGame() {
        gamePlayers.forEach(this::removePlayer);

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

        // check if the game has not started yet
        if (gameState == GameState.WAITING) {

            // inform player that there are no longer enough players to start
            if (gamePlayers.size() < 2) {
                displayActionbar(gamePlayers, languageManager.fromKeyNoPrefix("waiting-for-players"));
            }

            // teleport player back and restore inventory
            if (arena.waitArea != null) {
                p.getInventory().clear();
                p.teleport(arena.lobby);
                if (inventories.containsKey(p)) {
                    p.getInventory().setContents(inventories.get(p));
                }
            }
        }
        else {
            // stop the game if there are not enough players
            if (gamePlayers.size() < 2) {
                stopGame();
            }

            // teleport player back and restore inventory
            p.getInventory().clear();
            p.teleport(arena.lobby);
            if (inventories.containsKey(p)) {
                p.getInventory().setContents(inventories.get(p));
            }
        }
    }

    /**
     * Initiates an automatic start of a Tumble game
     */
    public void autoStart() {
        // Wait for the player to load in
        int waitDuration = configManager.waitDuration;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            displayActionbar(gamePlayers, languageManager.fromKeyNoPrefix("time-till-start").replace("%wait%",waitDuration+""));
            playSound(gamePlayers, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 1);
            // Schedule a process to start the game in 300t (15s) and save the PID so we can cancel it later if needed
            autoStartID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this::gameStart, waitDuration * 20L);
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
        if (playersAlive.size() < 2 && gameState == GameState.RUNNING) {
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
        displayTitles(gamePlayers, languageManager.fromKeyNoPrefix("count-3"), null, 3, 10, 7);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
            displayTitles(gamePlayers, languageManager.fromKeyNoPrefix("count-2"), null, 3, 10, 7);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 1);
                displayTitles(gamePlayers, languageManager.fromKeyNoPrefix("count-1"), null, 3, 10, 7);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    playSound(gamePlayers, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 5, 2);
                    displayTitles(gamePlayers, languageManager.fromKeyNoPrefix("count-go"), null, 1, 5, 1);
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
}

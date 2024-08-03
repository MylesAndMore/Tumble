package com.MylesAndMore.Tumble.game;

import com.MylesAndMore.Tumble.config.SettingsManager;
import com.MylesAndMore.Tumble.plugin.GameState;
import com.MylesAndMore.Tumble.plugin.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

/**
 * An event listener for a game of Tumble.
 */
public class EventListener implements Listener {
    final Game game;

    /**
     * Create a new EventListener for a game.
     * This should be active when the game starts (not while it is waiting)
     * @param game The game that the EventListener belongs to.
     */
    public EventListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        // Hide/show join message accordingly
        if (event.getPlayer().getWorld() == game.arena.gameSpawn.getWorld() && SettingsManager.hideLeaveJoin) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        // Hide/show leave message accordingly
        if (event.getPlayer().getWorld() == game.arena.gameSpawn.getWorld() && SettingsManager.hideLeaveJoin) {
            event.setQuitMessage(null);
        }

        // Remove player from game if they leave during a game
        if (game.gamePlayers.contains(event.getPlayer())) {
            game.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        // Hide death messages if configured
        if (event.getEntity().getWorld() == game.arena.gameSpawn.getWorld() && SettingsManager.hideDeathMessages) {
            event.setDeathMessage(null);
        }

        // Inform the game that the player died and respawn them
        if (game.gamePlayers.contains(event.getEntity()) && game.gameState == GameState.RUNNING) {
            game.playerDeath(event.getEntity());
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> event.getEntity().spigot().respawn(), 10);
        }
    }

    @EventHandler
    public void PlayerItemDamageEvent(PlayerItemDamageEvent event) {
        // Remove item damage within games
        if (game.gamePlayers.contains(event.getPlayer()) && game.gameState == GameState.RUNNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player p)) { return; }
        if (!game.gamePlayers.contains(p)) { return; }

        if (game.roundType == GameType.SNOWBALLS && event.getEntity() instanceof Snowball) {
            if (game.gameState == GameState.RUNNING) {
                // Give players a snowball when they've used one (infinite snowballs)
                Bukkit.getServer().getScheduler().runTask(plugin, () -> p.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1)));
            } else {
                // Prevent projectiles (snowballs) from being thrown before the game starts
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player p)) { return; }
        if (!game.gamePlayers.contains(p)) { return; }

        // Removes blocks that snowballs thrown by players have hit in the game world
        if (game.roundType == GameType.SNOWBALLS && event.getEntity() instanceof Snowball) {
            if (event.getHitBlock() != null) {
                if (event.getHitBlock().getLocation().distanceSquared(game.arena.gameSpawn) < 579) {
                    game.gamePlayers.forEach(pl -> pl.playEffect(
                            event.getHitBlock().getLocation(),
                            Effect.STEP_SOUND,
                            event.getHitBlock().getType()));
                    event.getHitBlock().setType(Material.AIR);
                }
            } else if (event.getHitEntity() != null) {
                if (event.getHitEntity() instanceof Player hitPlayer) {
                    // Also cancel any knockback
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> hitPlayer.setVelocity(new Vector()));
                }
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        // Don't allow items to drop during the game
        if (game.gamePlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        if (!game.gamePlayers.contains(event.getPlayer())) { return; }
        // Cancel movement if the game is starting (so players can't move before the game starts)
        if (Objects.equals(game.gameState, GameState.STARTING) && !equalPosition(event.getFrom(),event.getTo())) {
            event.setCancelled(true);
        }
        // Kill player if they are below configured Y level
        if (game.arena.killAtY != null && game.gameState == GameState.RUNNING) {
            if (event.getPlayer().getLocation().getY() <= game.arena.killAtY) {
                event.getPlayer().setHealth(0);
            }
        }
    }

    @EventHandler
    public void BlockDropItemEvent(BlockDropItemEvent event) {
        // If a block was going to drop an item (ex. snow dropping snowballs) in the game world, cancel it
        if (event.getBlock().getWorld() == game.arena.gameSpawn.getWorld()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (!game.gamePlayers.contains(event.getPlayer())) { return; }

        // Remove blocks when clicked in the game world (all game types require this functionality)
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock() != null) {
            event.getPlayer().playEffect(
                    event.getClickedBlock().getLocation(),
                    Effect.STEP_SOUND,
                    event.getClickedBlock().getType()
            );
            event.getClickedBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        // This just doesn't allow blocks to break in the gameWorld; the PlayerInteractEvent will take care of everything
        // This prevents any weird client-server desync
        if (game.gamePlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void FoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player p)) { return; }
        if (!game.gamePlayers.contains(p)) { return; }
        // INFINITE FOOD (YAY!!!!)
        event.setCancelled(true);
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) { return; }
        if (!game.gamePlayers.contains(p)) { return; }

        // Check to see if a player got damaged by another entity (player, snowball, etc) in the gameWorld, if so, cancel it
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                || event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void InventoryDragEvent(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) { return; }
        if (!game.gamePlayers.contains(p)) { return; }
        // Disable inventory dragging
        event.setCancelled(true);
    }

    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent event) {
        // Make sure players respawn in the correct location
        if (game.gamePlayers.contains(event.getPlayer())) {
            event.setRespawnLocation(game.arena.gameSpawn);
        }
    }

    /**
     * Check to see if two locations are in the same place.
     * A location also includes where the player is facing which is why this is used instead of .equals()
     * @param l1 The first location
     * @param l2 The second location
     * @return True if they are in the same place
     */
    public static boolean equalPosition(Location l1, @Nullable Location l2) {
        if (l2 == null) {
            return true;
        }
        return  (l1.getX() == l2.getX()) &&
                (l1.getY() == l2.getY()) &&
                (l1.getZ() == l2.getZ());
    }
}
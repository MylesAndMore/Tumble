package com.MylesAndMore.Tumble.plugin;

import java.util.Objects;

import com.MylesAndMore.Tumble.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Tumble event listener for all plugin and game-related events.
 */
public class EventListener implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        // Hide/show join message accordingly
        if (Constants.getPlugin().getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setJoinMessage(null);
        }
        // Check if either of the worlds are not defined in config, if so, end to avoid any NPEs later on
        if (Constants.getGameWorld() == null || Constants.getLobbyWorld() == null) { return; }
        if (event.getPlayer().getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
            // Send the player back to the lobby if they try to join in the middle of a game
            event.getPlayer().teleport(Objects.requireNonNull(Bukkit.getWorld(Constants.getLobbyWorld())).getSpawnLocation());
        }
        if (Constants.getPlugin().getConfig().getBoolean("autoStart.enabled")) {
            if (Constants.getPlayersInLobby().size() == Constants.getPlugin().getConfig().getInt("autoStart.players")) {
                // The autoStart should begin if it is already enabled and the amount of players is correct; pass this to the Game
                Game.getGame().autoStart();
            }
        }
    }

    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        if (Constants.getGameWorld() == null || Constants.getLobbyWorld() == null) {
            return;
        }
        if (event.getPlayer().getWorld() == Bukkit.getWorld(Constants.getLobbyWorld())) {
            // Another event on which autostart could be triggered
            if (Constants.getPlugin().getConfig().getBoolean("autoStart.enabled")) {
                if (Constants.getPlayersInLobby().size() == Constants.getPlugin().getConfig().getInt("autoStart.players")) {
                    Game.getGame().autoStart();
                }
            }
        }
        // Also check if the player left to another world and cancel autostart
        else if (event.getFrom() == Bukkit.getWorld(Constants.getLobbyWorld())) {
            if (Objects.equals(Game.getGame().getGameState(), "waiting")) {
                Game.getGame().cancelStart();
            }
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        // Hide/show leave message accordingly
        if (Constants.getPlugin().getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setQuitMessage(null);
        }
        if (Constants.getLobbyWorld() == null) { return; }
        if (event.getPlayer().getWorld() == Bukkit.getWorld(Constants.getLobbyWorld())) {
            // Check if the game is in the process of autostarting, if so cancel
            if (Objects.equals(Game.getGame().getGameState(), "waiting")) {
                Game.getGame().cancelStart();
            }
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        // Pass game deaths to the Game
        if (event.getEntity().getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
            Game.getGame().playerDeath(event.getEntity());
        }
    }

    @EventHandler
    public void PlayerItemDamageEvent(PlayerItemDamageEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        // Remove item damage within games
        if (event.getPlayer().getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (Constants.getGameWorld() == null) {
            return;
        }
        if (event.getEntity().getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
            if (event.getEntity() instanceof Snowball) {
                if (event.getEntity().getShooter() instanceof Player player) {
                    // Prevent projectiles (snowballs) from being thrown before the game starts
                    if (Objects.equals(Game.getGame().getGameState(), "starting")) {
                        event.setCancelled(true);
                    }
                    else {
                        // Give players a snowball when they've used one (infinite snowballs)
                        Bukkit.getServer().getScheduler().runTask(Constants.getPlugin(), () -> player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 1)));
                    }
                }
            }
        }
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        else if (event.getHitBlock() == null) { return; }
        // Removes blocks that snowballs thrown by players have hit in the game world
        if (event.getHitBlock().getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
            if (event.getEntity() instanceof Snowball) {
                if (event.getEntity().getShooter() instanceof Player) {
                    if (event.getHitBlock() != null) {
                        if (event.getHitBlock().getLocation().distanceSquared(Objects.requireNonNull(Bukkit.getWorld(Constants.getGameWorld())).getSpawnLocation()) < 579) {
                            event.getHitBlock().setType(Material.AIR);
                        }
                    }
                    else if (event.getHitEntity() != null) {
                        if (event.getHitEntity() instanceof Player hitPlayer) {
                            // Also cancel any knockback
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Constants.getPlugin(), () -> hitPlayer.setVelocity(new Vector()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        // Don't allow items to drop in the game world
        if (event.getPlayer().getWorld() == Bukkit.getWorld((Constants.getGameWorld()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        // Cancel movement if the game is starting (so players can't move before the game starts)
        if (Objects.equals(Game.getGame().getGameState(), "starting")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        // Remove blocks when clicked in the game world (all gamemodes require this functionality)
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (Objects.requireNonNull(event.getClickedBlock()).getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
                event.getClickedBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        // This just doesn't allow blocks to break in the gameWorld; the PlayerInteractEvent will take care of everything
        // This prevents any weird client-server desync
        if (event.getBlock().getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void FoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        // INFINITE FOOD (YAY!!!!)
        if (event.getEntity().getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        // Check to see if a player got damaged by another entity (player, snowball, etc) in the gameWorld, if so, cancel it
        if (event.getEntity().getWorld() == Bukkit.getWorld(Constants.getGameWorld())) {
            if (event.getEntity() instanceof Player) {
                if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void InventoryDragEvent(InventoryDragEvent event) {
        if (Constants.getGameWorld() == null) { return; }
        if (event.getWhoClicked().getWorld() == Bukkit.getWorld((Constants.getGameWorld()))) {
            event.setCancelled(true);
        }
    }
    
}

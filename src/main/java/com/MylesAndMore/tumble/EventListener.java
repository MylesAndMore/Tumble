package com.MylesAndMore.tumble;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EventListener implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        // On a PlayerJoinEvent, check if the config is set to hide the join/leave
        // messages
        // If true, null out the join message (which just makes it so that there is no
        // message)
        // If false, nothing will happen, and the default message will display
        if (TumbleManager.getPlugin().getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setJoinMessage(null);
        }
        // Check if either of the worlds are not defined in config, if so, end
        // This is to avoid NPEs and such
        if (TumbleManager.getGameWorld() == null || TumbleManager.getLobbyWorld() == null) {
            return;
        }
        // Check if the player joining is in the game world, if true then
        if (event.getPlayer().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            // send them back to the lobby.
            event.getPlayer().teleport(Bukkit.getWorld(TumbleManager.getLobbyWorld()).getSpawnLocation());
        }
        // For auto-start function: check if the autoStart is enabled
        if (TumbleManager.getPlugin().getConfig().getBoolean("autoStart.enabled")) {
            // If so, check if the amount of players has been reached
            if (TumbleManager.getPlayersInLobby().size() == TumbleManager.getPlugin().getConfig().getInt("autoStart.players")) {
                // The autoStart should begin; pass this to the Game
                Game.getGame().autoStart();
            }
        }
    }

    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        if (TumbleManager.getGameWorld() == null || TumbleManager.getLobbyWorld() == null) {
            return;
        }
        // Check if the player changed to the lobbyWorld, then
        if (event.getPlayer().getWorld() == Bukkit.getWorld(TumbleManager.getLobbyWorld())) {
            // run the autostart checks (commented above)
            if (TumbleManager.getPlugin().getConfig().getBoolean("autoStart.enabled")) {
                if (TumbleManager.getPlayersInLobby().size() == TumbleManager.getPlugin().getConfig().getInt("autoStart.players")) {
                    Game.getGame().autoStart();
                }
            }
        }
        // also check if the player left to another world
        else if (event.getFrom() == Bukkit.getWorld(TumbleManager.getLobbyWorld())) {
            if (Objects.equals(Game.getGame().getGameState(), "waiting")) {
                Game.getGame().cancelStart();
            }
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        // On a PlayerQuitEvent, check if the config is set to hide the join/leave
        // messages
        // If true, null out the quit message (which just makes it so that there is no
        // message)
        // If false, nothing will happen, and the default message will display
        if (TumbleManager.getPlugin().getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setQuitMessage(null);
        }
        if (TumbleManager.getLobbyWorld() == null) {
            return;
        }
        if (event.getPlayer().getWorld() == Bukkit.getWorld(TumbleManager.getLobbyWorld())) {
            // Check if the game is in the process of autostarting
            if (Objects.equals(Game.getGame().getGameState(), "waiting")) {
                // Cancel the autostart
                Game.getGame().cancelStart();
            }
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // On a PlayerDeathEvent,
        // check to see if the player died in the gameWorld,
        if (event.getEntity().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            // then pass this off to the Game
            Game.getGame().playerDeath(event.getEntity());
        }
    }

    @EventHandler
    public void PlayerItemDamageEvent(PlayerItemDamageEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // On an ItemDamageEvent
        // check to see if the item was damaged in the gameWorld,
        if (event.getPlayer().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            event.setCancelled(true);
        }
    }

    // private long lastTimeP;
    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // When a projectile is launched,
        // check to see if projectile was thrown in the gameWorld.
        if (event.getEntity().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            if (event.getEntity() instanceof Snowball) {
                if (event.getEntity().getShooter() instanceof Player player) {
                    // Check to see if the last snowball was thrown less than 200ms ago, if so, don't allow another
                    // if ((System.currentTimeMillis() - lastTimeP) < 200) { event.setCancelled(true); }
                    // else {
                    // // Otherwise, continue with logic
                    // lastTimeP = System.currentTimeMillis();
                    // // This prevents players from shooting snowballs before the game actually begins
                    if (Objects.equals(Game.getGame().getGameState(), "starting")) {
                        event.setCancelled(true);
                    } else {
                        // This gives players a snowball when they've used one
                        Bukkit.getServer().getScheduler().runTask(TumbleManager.getPlugin(), () -> {
                            player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
                        });
                    }
                    // }
                }
            }
        }
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // Weird stacktrace thing
        else if (event.getHitBlock() == null) {
            return;
        }
        // When a projectile hits
        // check to see if the projectile hit in the gameWorld,
        if (event.getHitBlock().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            // then check if the projectile was a snowball,
            if (event.getEntity() instanceof Snowball) {
                // then check if a player threw it,
                if (event.getEntity().getShooter() instanceof Player shooterPlayer) {
                    // then check to see if it hit a player or a block
                    if (event.getHitBlock() != null) {
                        // if it was a block, check if that block is within the game area,
                        if (event.getHitBlock().getLocation().distanceSquared(Bukkit.getWorld(TumbleManager.getGameWorld()).getSpawnLocation()) < 579) {
                            // then remove that block.
                            event.getHitBlock().setType(Material.AIR);
                        }
                    } else if (event.getHitEntity() != null) {
                        // if it was an entity, check if it hit a player,
                        if (event.getHitEntity() instanceof Player hitPlayer) {
                            // then cancel the knockback (has to be delayed by a tick for some reason)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TumbleManager.getPlugin(), () -> {
                                hitPlayer.setVelocity(new Vector());
                            });
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // When an item is dropped,
        // check if the item was dropped in the game world
        if (event.getPlayer().getWorld() == Bukkit.getWorld((TumbleManager.getGameWorld()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // On a PlayerMoveEvent, check if the game is starting
        if (Objects.equals(Game.getGame().getGameState(), "starting")) {
            // Cancel the event if the game is starting (so players can't move before the game starts)
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void BlockDropItemEvent(BlockDropItemEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // If a block was going to drop an item (ex. snow dropping snowballs) in the GameWorld, cancel it
        if (event.getBlock().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            event.setCancelled(true);
        }
    }

    // private long lastTimeI;
    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // Check if a player was left clicking a block in the gameWorld
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getClickedBlock().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
                // Then check to see if the player interacted less than 150ms ago
                // if ((System.currentTimeMillis() - lastTimeI) < 150) return;
                // If not, set that block to air (break it)
                // else {
                // lastTimeI = System.currentTimeMillis();
                event.getClickedBlock().setType(Material.AIR);
                // }
            }
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // This just doesn't allow blocks to break in the gameWorld; the PlayerInteractEvent will take care of everything
        // It just keeps client commonality w/ animations and stuff
        if (event.getBlock().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void FoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // When someone's food level changes, check if that happened in the gameWorld, then cancel it
        if (event.getEntity().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        // Check to see if a player got damaged by another entity (player, snowball, etc) in the gameWorld, if so, cancel it
        if (event.getEntity().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
            if (event.getEntity() instanceof Player) {
                if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void InventoryDragEvent(InventoryDragEvent event) {
        if (TumbleManager.getGameWorld() == null) {
            return;
        }
        if (event.getWhoClicked().getWorld() == Bukkit.getWorld((TumbleManager.getGameWorld()))) {
            event.setCancelled(true);

        }
    }

}

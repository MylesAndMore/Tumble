package com.MylesAndMore.tumble;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener{
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        // On a PlayerJoinEvent, check if the config is set to hide the join/leave messages
        // If true, null out the join message (which just makes it so that there is no message)
        // If false, nothing will happen, and the default message will display
        if (TumbleManager.getPlugin().getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setJoinMessage(null);
        }
        // If the gameWorld and lobbyWorld is not null, then check
        if (TumbleManager.getGameWorld() != null && TumbleManager.getLobbyWorld() != null) {
            // if the player joining is in the game world, then
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
    }

    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        // If the gameWorld and lobbyWorld is not null, then check
        if (TumbleManager.getGameWorld() != null && TumbleManager.getLobbyWorld() != null) {
            // if the player changed to the lobbyWorld, then
            if (event.getPlayer().getWorld() == Bukkit.getWorld(TumbleManager.getLobbyWorld())) {
                // run the autostart checks (from above)
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
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        // On a PlayerQuitEvent, check if the config is set to hide the join/leave messages
        // If true, null out the quit message (which just makes it so that there is no message)
        // If false, nothing will happen, and the default message will display
        if (TumbleManager.getPlugin().getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setQuitMessage(null);
        }
        // Check if a player left in the lobbyWorld
        if (TumbleManager.getLobbyWorld() != null) {
            if (event.getPlayer().getWorld() == Bukkit.getWorld(TumbleManager.getLobbyWorld())) {
                // Check if the game is in the process of autostarting
                if (Objects.equals(Game.getGame().getGameState(), "waiting")) {
                    // Cancel the autostart
                    Game.getGame().cancelStart();
                }
            }
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {
        // On a PlayerDeathEvent, check to make sure the gameWorld is defined,
        if (TumbleManager.getGameWorld() != null) {
            // then check to see if the player died in the gameWorld,
            if (event.getEntity().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
                // then pass this off to the Game
                Game.getGame().playerDeath(event.getEntity());
            }
        }
    }

    @EventHandler
    public void ItemDamageEvent(PlayerItemDamageEvent event) {
        // On a BlockBreakEvent, check to make sure there is a defined gameWorld
        if (TumbleManager.getGameWorld() != null) {
            // Then check to see if the block was broken in the gameWorld,
            if (event.getPlayer().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
                // If it was in the gameWorld, check if the roundType was shovels
                if (Objects.equals(Game.getGame().getRoundType(), "shovels")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event) {
        // When A projectile is launched, check to make sure there is a defined gameWorld
        if (TumbleManager.getGameWorld() != null) {
            // Then check to see if projectile was thrown in the gameWorld.
            if (event.getEntity().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
                if (event.getEntity() instanceof Snowball) {
                    if (event.getEntity().getShooter() instanceof Player player) {
                        player.getInventory().addItem(new ItemStack(Material.SNOWBALL,1));
                    }
                }
            }
        }
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event) {
        // When a projectile hits, check to see if the gameWorld is null,
        if (TumbleManager.getGameWorld() != null) {
            // then check to see if the projectile hit in the gameWorld,
            if (event.getHitBlock().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
                // then check if the projectile was a snowball,
                if (event.getEntity() instanceof Snowball) {
                    // then check if a player threw it,
                    if (event.getEntity().getShooter() instanceof Player player) {
                        // then check if that block is within the game area,
                        if (event.getHitBlock().getLocation().distanceSquared(Bukkit.getWorld(TumbleManager.getGameWorld()).getSpawnLocation()) < 402) {
                            // then remove that block.
                            event.getHitBlock().setType(Material.AIR);
                        }
                    }
                }
            }
            else if (event.getHitBlock().getWorld() == null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        // When an item is dropped, make sure there is a defined gameWorld
        if (TumbleManager.getGameWorld() != null) {
            // Then check if the item was dropped in the game world
            if (event.getPlayer().getWorld() == Bukkit.getWorld((TumbleManager.getGameWorld()))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        // On a PlayerMoveEvent, check if the game is starting
        if (Objects.equals(Game.getGame().getGameState(), "starting")) {
            // Cancel the event if the game is starting (so players can't move before the game starts)
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void FoodLevelChangeEvent(FoodLevelChangeEvent event) {
        // When someone's food level changes, check if the gameWorld is null,
        if (TumbleManager.getGameWorld() != null) {
            // then check if that happened in the gameWorld
            if (event.getEntity().getWorld() == Bukkit.getWorld(TumbleManager.getGameWorld())) {
                event.setCancelled(true);
            }
        }
    }
}





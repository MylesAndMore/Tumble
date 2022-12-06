package com.MylesAndMore.tumble;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
                // If it was in the gameWorld, pass this event to the Game
                Game.getGame().itemDamage(event);
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
                        player.getInventory().addItem(new ItemStack(Material.SNOWBALL));
                    }
                }
            }
        }
    }
}





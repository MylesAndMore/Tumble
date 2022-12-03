package com.MylesAndMore.tumble;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
}

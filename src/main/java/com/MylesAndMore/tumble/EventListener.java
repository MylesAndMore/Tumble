package com.MylesAndMore.tumble;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class EventListener implements Listener{
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("tumble");

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){
        // On a PlayerJoinEvent, check if the config is set to hide the join/leave messages
        // If true, null out the join message (which just makes it so that there is no message)
        // If false, nothing will happen, and the default message will display
        if (plugin.getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event){
        // On a PlayerQuitEvent, check if the config is set to hide the join/leave messages
        // If true, null out the quit message (which just makes it so that there is no message)
        // If false, nothing will happen, and the default message will display
        if (plugin.getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setQuitMessage(null);
        }
    }
}

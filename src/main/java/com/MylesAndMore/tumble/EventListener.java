package com.MylesAndMore.tumble;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener{
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){
        if (Bukkit.getServer().getPluginManager().getPlugin("tumble").getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event){
        if (Bukkit.getServer().getPluginManager().getPlugin("tumble").getConfig().getBoolean("hideJoinLeaveMessages")) {
            event.setQuitMessage(null);
        }
    }
}

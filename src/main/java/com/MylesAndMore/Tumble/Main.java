package com.MylesAndMore.Tumble;

import com.MylesAndMore.Tumble.commands.*;
import com.MylesAndMore.Tumble.plugin.Metrics;
import com.MylesAndMore.Tumble.plugin.Constants;
import com.MylesAndMore.Tumble.plugin.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
    @Override
    public void onEnable() {
        // Register setup items
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getCommand("reload").setExecutor(new Reload());
        this.getCommand("link").setExecutor(new SetWorldConfig());
        this.getCommand("start").setExecutor(new StartGame());
        this.getCommand("winlocation").setExecutor(new SetWinnerLoc());
        this.getCommand("autostart").setExecutor(new SetAutoStart());
        int pluginId = 16940;
        Metrics metrics = new Metrics(this, 16940);
        this.saveDefaultConfig(); // Saves the default config file (packaged in the JAR) if we haven't already\

        // Check if worlds are null in config and throw warnings if so
        if (Constants.getGameWorld() == null) {
            Bukkit.getServer().getLogger().warning("[Tumble] It appears you have not configured a game world for Tumble.");
            Bukkit.getServer().getLogger().info("[Tumble] If this is your first time running the plugin, you may disregard this message.");
        }
        if (Constants.getLobbyWorld() == null) {
            Bukkit.getServer().getLogger().warning("[Tumble] It appears you have not configured a lobby world for Tumble.");
            Bukkit.getServer().getLogger().info("[Tumble] If this is your first time running the plugin, you may disregard this message.");
        }
        Bukkit.getServer().getLogger().info("[Tumble] Tumble successfully enabled!");
    }
}
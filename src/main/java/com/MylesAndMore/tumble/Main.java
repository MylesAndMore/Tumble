package com.MylesAndMore.tumble;

import com.MylesAndMore.tumble.commands.*;
import com.MylesAndMore.tumble.api.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
    @Override
    public void onEnable() {
        // Register our event listener
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        // Register commands
        this.getCommand("reload").setExecutor(new ReloadCommand());
        this.getCommand("link").setExecutor(new SetWorldConfig());
        this.getCommand("start").setExecutor(new StartGame());
        this.getCommand("winlocation").setExecutor(new SetWinnerLoc());
        this.getCommand("autostart").setExecutor(new SetAutoStart());
        // Save the default config file (packaged in the JAR)
        this.saveDefaultConfig();

        // Register bStats
        int pluginId = 16940;
        Metrics metrics = new Metrics(this, 16940);

        // Check if worlds are null in config
        if (TumbleManager.getGameWorld() == null) {
            Bukkit.getServer().getLogger().warning("[tumble] It appears you have not configured a game world for Tumble.");
            Bukkit.getServer().getLogger().info("[tumble] If this is your first time running the plugin, you may disregard this message.");
        }
        if (TumbleManager.getLobbyWorld() == null) {
            Bukkit.getServer().getLogger().warning("[tumble] It appears you have not configured a lobby world for Tumble.");
            Bukkit.getServer().getLogger().info("[tumble] If this is your first time running the plugin, you may disregard this message.");
        }

        // Init message
        Bukkit.getServer().getLogger().info("[tumble] tumble successfully enabled!");
    }
}
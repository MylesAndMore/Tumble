package com.MylesAndMore.tumble;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
    @Override
    public void onEnable() {
        // Register our event listener
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        // Register our config file
        this.saveDefaultConfig();
        // Register bStats
        int pluginId = 16940;
        Metrics metrics = new Metrics(this, 16940);
    }

}
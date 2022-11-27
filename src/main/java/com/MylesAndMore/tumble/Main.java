package com.MylesAndMore.tumble;

import com.MylesAndMore.tumble.commands.ReloadCommand;
import com.MylesAndMore.tumble.api.Metrics;
import com.MylesAndMore.tumble.commands.SetWorldConfig;
import com.MylesAndMore.tumble.commands.StartGame;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
    @Override
    public void onEnable() {
        // Register our event listener
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        // Register commands
        this.getCommand("reload").setExecutor(new ReloadCommand());
        this.getCommand("setworld").setExecutor(new SetWorldConfig());
        this.getCommand("start").setExecutor(new StartGame());
        // Save the default config file (packaged in the JAR)
        this.saveDefaultConfig();

        // Register bStats
        int pluginId = 16940;
        Metrics metrics = new Metrics(this, 16940);
    }

    public void onDisable() {

    }
}
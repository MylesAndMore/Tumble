package com.MylesAndMore.Tumble;

import com.MylesAndMore.Tumble.commands.*;
import com.MylesAndMore.Tumble.config.ArenaManager;

import com.MylesAndMore.Tumble.config.ConfigManager;
import com.MylesAndMore.Tumble.config.LanguageManager;
import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin{
    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;
        ArenaManager.loadConfig();
        ConfigManager.loadConfig();
        LanguageManager.loadConfig();

        Objects.requireNonNull(this.getCommand("tumble")).setExecutor(new Tumble());
        new Metrics(this, 16940);

        this.saveDefaultConfig(); // Saves the default config file (packaged in the JAR) if we haven't already

        Bukkit.getServer().getLogger().info("[Tumble] Tumble successfully enabled!");
    }
}
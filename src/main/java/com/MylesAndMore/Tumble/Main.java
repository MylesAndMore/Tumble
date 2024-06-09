package com.MylesAndMore.Tumble;

import com.MylesAndMore.Tumble.commands.*;
import com.MylesAndMore.Tumble.plugin.ConfigManager;

import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin{
    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Objects.requireNonNull(this.getCommand("reload")).setExecutor(new Reload());
        Objects.requireNonNull(this.getCommand("config")).setExecutor(new Config());
        Objects.requireNonNull(this.getCommand("forcestart")).setExecutor(new ForceStart());
        Objects.requireNonNull(this.getCommand("join")).setExecutor(new Join());
        Objects.requireNonNull(this.getCommand("leave")).setExecutor(new Leave());
        Objects.requireNonNull(this.getCommand("forcestop")).setExecutor(new ForceStop());
        new Metrics(this, 16940);
        // TODO: change command format

        this.saveDefaultConfig(); // Saves the default config file (packaged in the JAR) if we haven't already
        ConfigManager.readConfig();

        Bukkit.getServer().getLogger().info("[Tumble] Tumble successfully enabled!");
    }
}
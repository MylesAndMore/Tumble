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

        Objects.requireNonNull(this.getCommand("tumble-reload")).setExecutor(new Reload());
        Objects.requireNonNull(this.getCommand("tumble-config")).setExecutor(new Config());
        Objects.requireNonNull(this.getCommand("tumble-forcestart")).setExecutor(new ForceStart());
        Objects.requireNonNull(this.getCommand("tumble-join")).setExecutor(new Join());
        Objects.requireNonNull(this.getCommand("tumble-leave")).setExecutor(new Leave());
        Objects.requireNonNull(this.getCommand("tumble-forcestop")).setExecutor(new ForceStop());
        new Metrics(this, 16940);
        // TODO: change command format

        this.saveDefaultConfig(); // Saves the default config file (packaged in the JAR) if we haven't already
        ConfigManager.readConfig();

        Bukkit.getServer().getLogger().info("[Tumble] Tumble successfully enabled!");
    }
}
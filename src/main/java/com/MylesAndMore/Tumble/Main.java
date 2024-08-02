package com.MylesAndMore.Tumble;

import com.MylesAndMore.Tumble.commands.*;
import com.MylesAndMore.Tumble.config.ArenaManager;

import com.MylesAndMore.Tumble.config.ConfigManager;
import com.MylesAndMore.Tumble.config.LanguageManager;
import com.MylesAndMore.Tumble.config.LayerManager;
import com.MylesAndMore.Tumble.game.Arena;
import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {
    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        LanguageManager.readConfig();
        ConfigManager.readConfig();
        ArenaManager.readConfig();
        try {
            LayerManager.readConfig();
        } catch (InvalidConfigurationException e) {
            this.getLogger().severe(e.getMessage());
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Objects.requireNonNull(this.getCommand("tumble")).setExecutor(new Tumble());
        new Metrics(this, 16940);

        this.getLogger().info("Tumble successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Stop any running games
        for (Arena a : ArenaManager.arenas.values()) {
            if (a.game != null) {
                a.game.stopGame();
            }
        }
    }
}
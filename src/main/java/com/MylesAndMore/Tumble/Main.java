package com.MylesAndMore.Tumble;

import com.MylesAndMore.Tumble.commands.*;
import com.MylesAndMore.Tumble.config.ArenaManager;

import com.MylesAndMore.Tumble.config.ConfigManager;
import com.MylesAndMore.Tumble.config.LanguageManager;
import com.MylesAndMore.Tumble.game.Arena;
import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin{
    public static Main plugin;

    public static LanguageManager languageManager;
    public static ArenaManager arenaManager;
    public static ConfigManager configManager;

    @Override
    public void onEnable() {
        plugin = this;

        languageManager = new LanguageManager();
        configManager = new ConfigManager();
        arenaManager = new ArenaManager();

        Objects.requireNonNull(this.getCommand("tumble")).setExecutor(new Tumble());
        new Metrics(this, 16940);

        Bukkit.getServer().getLogger().info("[Tumble] Tumble successfully enabled!");
    }

    @Override
    public void onDisable() {
        // stop running games
        for (Arena a : arenaManager.arenas.values()) {
            if (a.game != null) {
                a.game.stopGame();
            }
        }
    }
}
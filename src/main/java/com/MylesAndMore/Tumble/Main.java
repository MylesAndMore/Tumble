package com.MylesAndMore.Tumble;

import com.MylesAndMore.Tumble.commands.*;
import com.MylesAndMore.Tumble.config.*;

import com.MylesAndMore.Tumble.game.Arena;
import org.bstats.bukkit.Metrics;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {
    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        OldConfigManager.migrateConfig();
        LanguageManager.readConfig();
        SettingsManager.readConfig();
        ArenaManager.readConfig();
        LayerManager.readConfig();

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
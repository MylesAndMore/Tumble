package com.MylesAndMore.Tumble.config;

import org.bukkit.configuration.file.FileConfiguration;

import static com.MylesAndMore.Tumble.Main.plugin;

public class ConfigManager {
    private static FileConfiguration config;

    public static boolean HideLeaveJoin;
    public static int waitDuration;

    public static void loadConfig() {
        config = plugin.getConfig();
        readConfig();
    }

    public static void readConfig() {
        HideLeaveJoin = config.getBoolean("hide-join-leave-messages", false);
        waitDuration = config.getInt("wait-duration", 15);
    }


}

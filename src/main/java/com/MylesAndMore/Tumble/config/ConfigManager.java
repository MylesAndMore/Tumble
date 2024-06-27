package com.MylesAndMore.Tumble.config;

import org.bukkit.configuration.file.FileConfiguration;

import static com.MylesAndMore.Tumble.Main.plugin;

public class ConfigManager {
    private static FileConfiguration config;

    public static boolean HideLeaveJoin;
    public static int waitDuration;

    public ConfigManager() {
        config = plugin.getConfig();
    }

    public static void readConfig() {
        HideLeaveJoin = config.getBoolean("hideJoinLeaveMessages", false);
        waitDuration = config.getInt("wait-duration", 15);
    }


}

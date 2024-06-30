package com.MylesAndMore.Tumble.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private static final CustomConfig customConfig = new CustomConfig("config.yml");
    private static final FileConfiguration config = customConfig.getConfig();

    public static boolean HideLeaveJoin;
    public static int waitDuration;

    public static void init() {
        customConfig.saveDefaultConfig();
        readConfig();
    }

    public static void readConfig() {
        HideLeaveJoin = config.getBoolean("hide-join-leave-messages", false);
        waitDuration = config.getInt("wait-duration", 15);
    }

}

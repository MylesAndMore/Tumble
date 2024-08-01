package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.configuration.Configuration;

import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

/**
 * Manages config.yml and stores its options
 */
public class ConfigManager {
    public static boolean HideLeaveJoin;
    public static int waitDuration;

    private static Configuration config;
    private static Configuration defaultConfig;

    /**
     * Reads options in from config.yml
     */
    public static void readConfig() {
        CustomConfig configYml = new CustomConfig("config.yml");
        configYml.saveDefaultConfig();
        config = configYml.getConfig();
        defaultConfig = Objects.requireNonNull(config.getDefaults());
        HideLeaveJoin = config.getBoolean("hide-join-leave-messages", false);
        waitDuration = config.getInt("wait-duration", 15);

        validate();
    }

    /**
     * Check keys of config.yml against the defaults
     */
    public static void validate() {
        boolean invalid = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key,true)) {
                plugin.getLogger().warning("config.yml is missing key '" + key + "'.");
                invalid = true;
            }
        }
        if (invalid) {
            plugin.getLogger().severe("Errors were found in config.yml, default values will be used.");
        }
    }
}

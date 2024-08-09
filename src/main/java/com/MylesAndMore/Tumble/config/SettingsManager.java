package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.configuration.Configuration;

import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

/**
 * Manages settings.yml and stores its options
 */
public class SettingsManager {
    public static boolean hideLeaveJoin;
    public static boolean hideDeathMessages;
    public static int waitDuration;

    private static Configuration config;
    private static Configuration defaultConfig;

    /**
     * Reads options in from settings.yml
     */
    public static void readConfig() {
        CustomConfig settingsYml = new CustomConfig("settings.yml");
        settingsYml.saveDefaultConfig();
        config = settingsYml.getConfig();
        defaultConfig = Objects.requireNonNull(config.getDefaults());
        hideLeaveJoin = config.getBoolean("hide-join-leave-messages", false);
        hideDeathMessages = config.getBoolean("hide-death-messages", false);
        waitDuration = config.getInt("wait-duration", 15);

        validate();
    }

    /**
     * Check keys of settings.yml against the defaults
     */
    public static void validate() {
        boolean invalid = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key,true)) {
                plugin.getLogger().warning("settings.yml is missing key '" + key + "'.");
                invalid = true;
            }
        }
        if (invalid) {
            plugin.getLogger().severe("Errors were found in settings.yml, default values will be used.");
        }
    }
}

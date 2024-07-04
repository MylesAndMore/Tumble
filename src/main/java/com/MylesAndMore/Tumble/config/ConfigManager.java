package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.configuration.Configuration;

import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

/**
 * Manages config.yml and stores its options
 */
public class ConfigManager {
    public boolean HideLeaveJoin;
    public int waitDuration;

    private final Configuration config;
    private final Configuration defaultConfig;

    /**
     * Create a config manager
     */
    public ConfigManager() {
        CustomConfig configYml = new CustomConfig("config.yml");
        configYml.saveDefaultConfig();
        config = configYml.getConfig();
        defaultConfig = Objects.requireNonNull(config.getDefaults());

        validate();
        readConfig();
    }

    /**
     * Check keys of config.yml against the defaults
     */
    public void validate() {
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

    /**
     * Reads options in from config.yml
     */
    public void readConfig() {
        HideLeaveJoin = config.getBoolean("hide-join-leave-messages", false);
        waitDuration = config.getInt("wait-duration", 15);
    }

}

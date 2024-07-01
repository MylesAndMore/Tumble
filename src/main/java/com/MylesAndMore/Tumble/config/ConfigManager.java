package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.configuration.Configuration;

import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

public class ConfigManager {
    public boolean HideLeaveJoin;
    public int waitDuration;

    private final CustomConfig configYml = new CustomConfig("config.yml");
    private final Configuration config = configYml.getConfig();
    private final Configuration defaultConfig = Objects.requireNonNull(config.getDefaults());

    public ConfigManager() {
        configYml.saveDefaultConfig();
        validate();
        readConfig();
    }

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

    public void readConfig() {
        HideLeaveJoin = config.getBoolean("hide-join-leave-messages", false);
        waitDuration = config.getInt("wait-duration", 15);
    }

}

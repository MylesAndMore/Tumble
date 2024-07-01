package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

public class LanguageManager {
    private final CustomConfig languageYml = new CustomConfig("language.yml");
    private final Configuration config = languageYml.getConfig();
    private final Configuration defaultConfig = Objects.requireNonNull(config.getDefaults());

    public LanguageManager() {
        languageYml.saveDefaultConfig();
        validate();
    }

    public void validate() {
        boolean invalid = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key,true)) {
                plugin.getLogger().warning("language.yml is missing key '" + key + "'.");
                invalid = true;
            }
        }
        if (invalid) {
            plugin.getLogger().severe("Errors were found in language.yml, default values will be used.");
        }
    }

    public String fromKey(String key) {
        return fromKeyNoPrefix("prefix") + fromKeyNoPrefix(key);
    }

    public String fromKeyNoPrefix(String key) {
        String val = config.getString(key, "LANG_ERR");
        return ChatColor.translateAlternateColorCodes('&',val);
    }
}

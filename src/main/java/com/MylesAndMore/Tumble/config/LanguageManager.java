package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.Objects;

import static com.MylesAndMore.Tumble.Main.plugin;

/**
 * Manages language.yml and allows retrieval of keys
 */
public class LanguageManager {
    private static final CustomConfig languageYml = new CustomConfig("language.yml");
    private static Configuration config;
    private static Configuration defaultConfig;

    public static void readConfig() {
        languageYml.saveDefaultConfig();
        config = languageYml.getConfig();
        defaultConfig = Objects.requireNonNull(config.getDefaults());

        validate();
    }

    /**
     * Check keys of language.yml against the defaults
     */
    public static void validate() {
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

    /**
     * Gets a key from language.yml and prepends the prefix.
     * If it is not present, a default value will be returned
     * @param key The key representing the message
     * @return The message from the key
     */
    public static String fromKey(String key) {
        return fromKeyNoPrefix("prefix") + fromKeyNoPrefix(key);
    }

    /**
     * Gets a key from language.yml.
     * If it is not present, a default value will be returned
     * @param key The key representing the message
     * @return The message from the key
     */
    public static String fromKeyNoPrefix(String key) {
        String val = config.getString(key);

        if (val == null) {
            val = defaultConfig.getString(key);
        }

        if (val == null) {
            val = "LANG_ERR";
        }

        return ChatColor.translateAlternateColorCodes('&',val);
    }
}

package com.MylesAndMore.Tumble.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import static com.MylesAndMore.Tumble.Main.plugin;

public class LanguageManager {
    private static final CustomConfig customConfig = new CustomConfig("language.yml");
    private static final FileConfiguration config = customConfig.getConfig();

    public static void init() {
        customConfig.saveDefaultConfig();
    }

    public static String fromKey(String key) {
        return fromKeyNoPrefix("prefix") + fromKeyNoPrefix(key);
    }

    public static String fromKeyNoPrefix(String key) {
        String tmp = config.getString(key, "LANG_ERR");
        if (tmp.equals("LANG_ERR")) {
            plugin.getLogger().severe("There was an error getting key '"+ key +"' from language.yml");
        }
        return ChatColor.translateAlternateColorCodes('&',tmp);
    }
}

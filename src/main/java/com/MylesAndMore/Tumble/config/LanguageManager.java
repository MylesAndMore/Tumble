package com.MylesAndMore.Tumble.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.MylesAndMore.Tumble.Main.plugin;

public class LanguageManager {
    private static FileConfiguration config;

    public LanguageManager() {
        String fileName = "language.yml";
        // create config
        File customConfigFile = new File(plugin.getDataFolder(), fileName);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }

        config = new YamlConfiguration();
        try {
            config.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        /* User Edit:
            Instead of the above Try/Catch, you can also use
            YamlConfiguration.loadConfiguration(customConfigFile)
        */
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

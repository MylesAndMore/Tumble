package com.MylesAndMore.tumble;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginManager {
    // Tumble plugin
    public static Plugin getPlugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("tumble");
    }

    // Multiverse plugin
    public static MultiverseCore getMV() { return (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core"); }
    // Multiverse worldManager
    public static MVWorldManager getMVWorldManager() { return getMV().getMVWorldManager(); }
}

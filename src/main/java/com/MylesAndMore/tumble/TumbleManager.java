package com.MylesAndMore.tumble;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class TumbleManager {
    // Tumble plugin
    public static Plugin getPlugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("tumble");
    }

    // Tumble static methods
    public static String getPermissionMessage() { return TumbleManager.getPlugin().getConfig().getString("permissionMessage"); }
    public static String getGameWorld() { return TumbleManager.getPlugin().getConfig().getString("gameWorld"); }
    public static String getLobbyWorld() { return TumbleManager.getPlugin().getConfig().getString("lobbyWorld"); }
    public static String getGameType() { return TumbleManager.getPlugin().getConfig().getString("gameMode"); }
    public static List<Player> getPlayersInGame() { return Bukkit.getServer().getWorld(TumbleManager.getGameWorld()).getPlayers(); }
    public static List<Player> getPlayersInLobby() { return Bukkit.getServer().getWorld(TumbleManager.getLobbyWorld()).getPlayers(); }


    // Multiverse plugin
    public static MultiverseCore getMV() { return (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core"); }
    // Multiverse worldManager
    public static MVWorldManager getMVWorldManager() { return getMV().getMVWorldManager(); }
}

package com.MylesAndMore.Tumble.plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;

public class Constants {
    public static Plugin getPlugin() {
        return Bukkit.getServer().getPluginManager().getPlugin("tumble");
    }
    public static String getPermissionMessage() { return Constants.getPlugin().getConfig().getString("permissionMessage"); }
    public static String getGameWorld() { return Constants.getPlugin().getConfig().getString("gameWorld"); }
    public static String getLobbyWorld() { return Constants.getPlugin().getConfig().getString("lobbyWorld"); }
    public static String getGameType() { return Constants.getPlugin().getConfig().getString("gameMode"); }
    public static List<Player> getPlayersInGame() { return Objects.requireNonNull(Bukkit.getServer().getWorld(Constants.getGameWorld())).getPlayers(); }
    public static List<Player> getPlayersInLobby() { return Objects.requireNonNull(Bukkit.getServer().getWorld(Constants.getLobbyWorld())).getPlayers(); }

    public static MultiverseCore getMV() { return (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core"); }
    public static MVWorldManager getMVWorldManager() { return getMV().getMVWorldManager(); }
}

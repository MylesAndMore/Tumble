package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.plugin.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This cannot be run by the console");
            return false;
        }

        if (!sender.hasPermission("tumble.config")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
            return false;
        }

        if (args.length < 2 || args[0] == null || args[1] == null) {
            sender.sendMessage(ChatColor.RED + "Missing arguments");
            return false;
        }

        switch (args[0]) {
            case "add" -> {
                String arenaName = args[1];
                ConfigManager.arenas.put(arenaName, new Arena(arenaName, ((Player)sender).getLocation()));
                sender.sendMessage(ChatColor.GREEN + "Arena added.");
            }
            case "set" -> {
                String world = args[1];
                if (ConfigManager.arenas.containsKey(world)) {
                    ConfigManager.arenas.get(world).location = ((Player)sender).getLocation();
                }
                else if (world.equals("waitArea")) {
                    ConfigManager.waitArea = ((Player)sender).getLocation();
                }
                else if (world.equals("lobbySpawn")) {
                    ConfigManager.lobby = ((Player)sender).getLocation();
                }
                else if (world.equals("winnerLobbySpawn")) {
                    ConfigManager.winnerLobby = ((Player)sender).getLocation();
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Invalid parameter");
                    return false;
                }
                sender.sendMessage(ChatColor.GREEN + "Location set.");
            }
            case "disable" -> {
                String world = args[1];
                if (world.equals("waitArea")) {
                    ConfigManager.waitArea = null;
                }
                else if (world.equals("winnerLobbySpawn")) {
                    ConfigManager.winnerLobby = null;
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Invalid parameter");
                    return false;
                }
                sender.sendMessage(ChatColor.GREEN + "World disabled.");
            }
            case "remove" -> {
                String world = args[1];
                if (ConfigManager.arenas.containsKey(world)) {
                    ConfigManager.arenas.remove(world);
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Invalid parameter");
                    return false;
                }
                sender.sendMessage(ChatColor.GREEN + "Location set");
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Invalid parameter");
                return false;
            }
        }

        ConfigManager.WriteConfig();
        sender.sendMessage(ChatColor.GREEN + "Wrote changes to file.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Arrays.asList("add", "set", "disable", "remove"));
        }
        if (args.length == 2) {
            switch (args[0]) {
                case "set" -> {
                    ArrayList<String> temp = new ArrayList<>(ConfigManager.arenas.keySet());
                    temp.addAll(Arrays.asList("waitArea", "lobbySpawn", "winnerLobbySpawn"));
                    return temp;
                }
                case "disable" -> {
                    return Arrays.asList("waitArea", "winnerLobbySpawn");
                }
                case "delete" -> {
                    return ConfigManager.arenas.keySet().stream().toList();
                }
                default -> {
                    return new ArrayList<>();
                }
            }
        }
        return new ArrayList<>();
    }
}

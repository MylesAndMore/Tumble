package com.MylesAndMore.tumble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class SetWorldConfig implements CommandExecutor {
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("tumble");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Initialize vars for their respective command arguments
        String world = args[0];
        String worldType = args[1];
        // Catch for null arguments
        if (args.length > 0) {
            // Check if sender has perms to run command
            if (!sender.hasPermission("tumble.setworld")){
                // Check if the world type is lobby
                if (Objects.equals(worldType, "lobby")) {
                    // Check if the world is actually a world on the server
                    if (Bukkit.getWorld(world) != null) {
                        // Set the specified value of the world in the config under lobbyWorld
                        plugin.getConfig().set("lobbyWorld", world);
                        // Save said config
                        plugin.saveConfig();
                        // Feedback
                        sender.sendMessage(ChatColor.GREEN + "Lobby world successfully linked: " + ChatColor.GRAY + world);
                        sender.sendMessage(ChatColor.GREEN + "Run /tumble:reload for the changes to take effect.");
                    }
                    // Feedback for if the world doesn't exist
                    else {
                        sender.sendMessage(ChatColor.RED + "Failed to find a world named " + ChatColor.GRAY + world);
                    }
                }
                // Check if the world type is game
                else if (Objects.equals(args[1], "game")) {
                    if (Bukkit.getWorld(world) != null) {
                        plugin.getConfig().set("gameWorld", world);
                        plugin.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Game world successfully linked: " + ChatColor.GRAY + world);
                        sender.sendMessage(ChatColor.GREEN + "Run /tumble:reload for the changes to take effect.");
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Failed to find a world named " + ChatColor.GRAY + world);
                    }
                }
                // Feedback for if lobby or game wasn't entered
                else {
                    sender.sendMessage(ChatColor.RED + "Allowed world types are " + ChatColor.GRAY + "lobby " + ChatColor.RED + "and " + ChatColor.GRAY + "game" + ChatColor.RED + ".");
                }
            }
            // Feedback for if sender has no perms
            else {
                sender.sendMessage(ChatColor.RED + plugin.getConfig().getString("permissionMessage"));
            }
        }
        // Feedback for if no args were entered
        else {
            sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.GRAY + "/tumble:setworld <world> lobby|game");
        }
        return true;
    }
}

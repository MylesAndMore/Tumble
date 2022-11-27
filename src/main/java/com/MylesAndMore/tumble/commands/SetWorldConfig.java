package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.PluginManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class SetWorldConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Catch for null arguments
        if (args.length == 2) {
            // Check if sender has perms to run command
            if (sender.hasPermission("tumble.setworld")){
                // Initialize vars for their respective command arguments
                String world = args[0];
                String worldType = args[1];
                // Check if the world type is lobby
                if (Objects.equals(worldType, "lobby")) {
                    // Check if the world is actually a world on the server
                    if (Bukkit.getWorld(world) != null) {
                        // Check if the world has already been configured
                        if (!Objects.equals(PluginManager.getPlugin().getConfig().getString("gameWorld"), world)) {
                            // Set the specified value of the world in the config under lobbyWorld
                            PluginManager.getPlugin().getConfig().set("lobbyWorld", world);
                            // Save said config
                            PluginManager.getPlugin().saveConfig();
                            // Feedback
                            sender.sendMessage(ChatColor.GREEN + "Lobby world successfully linked: " + ChatColor.GRAY + world);
                            sender.sendMessage(ChatColor.RED + "Please restart your server for the changes to take effect; reloading is NOT enough!");
                        }
                        // Feedback for duplicate world configuration
                        else {
                            sender.sendMessage(ChatColor.RED + "That world has already been linked, please choose/create another world!");
                        }
                    }
                    // Feedback for if the world doesn't exist
                    else {
                        sender.sendMessage(ChatColor.RED + "Failed to find a world named " + ChatColor.GRAY + world);
                    }
                }
                // Check if the world type is game
                else if (Objects.equals(args[1], "game")) {
                    if (Bukkit.getWorld(world) != null) {
                        if (!Objects.equals(PluginManager.getPlugin().getConfig().getString("lobbyWorld"), world)) {
                            PluginManager.getPlugin().getConfig().set("gameWorld", world);
                            PluginManager.getPlugin().saveConfig();
                            sender.sendMessage(ChatColor.GREEN + "Game world successfully linked: " + ChatColor.GRAY + world);
                            sender.sendMessage(ChatColor.RED + "Please restart your server for the changes to take effect; reloading is NOT enough!");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "That world has already been linked, please choose/create another world!");
                        }
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
                sender.sendMessage(ChatColor.RED + PluginManager.getPlugin().getConfig().getString("permissionMessage"));
            }
        }
        // Feedback for if no args were entered
        else {
            sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.GRAY + "/tumble:setworld <world> lobby|game");
        }
        return true;
    }
}

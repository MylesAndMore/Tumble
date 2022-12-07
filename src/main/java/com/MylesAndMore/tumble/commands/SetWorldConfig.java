package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.TumbleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
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
            if (sender.hasPermission("tumble.link")){
                // Initialize vars for their respective command arguments
                String world = args[0];
                String worldType = args[1];
                // Check if the world type is lobby
                if (Objects.equals(worldType, "lobby")) {
                    // Check if the world is actually a world on the server
                    if (Bukkit.getWorld(world) != null) {
                        // Check if the world has already been configured
                        if (!Objects.equals(TumbleManager.getGameWorld(), world)) {
                            // Set the specified value of the world in the config under lobbyWorld
                            TumbleManager.getPlugin().getConfig().set("lobbyWorld", world);
                            // Save said config
                            TumbleManager.getPlugin().saveConfig();
                            // Feedback
                            sender.sendMessage(ChatColor.GREEN + "Lobby world successfully linked: " + ChatColor.GRAY + world);
                            sender.sendMessage(ChatColor.GREEN + "Please restart your server for the changes to take effect; " + ChatColor.RED + "reloading the plugin is insufficient!");
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
                        if (!Objects.equals(TumbleManager.getLobbyWorld(), world)) {
                            TumbleManager.getPlugin().getConfig().set("gameWorld", world);
                            TumbleManager.getPlugin().saveConfig();
                            // Set the gamerule of doImmediateRespawn in the gameWorld for later
                            Bukkit.getWorld(world).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
                            sender.sendMessage(ChatColor.GREEN + "Game world successfully linked: " + ChatColor.GRAY + world);
                            sender.sendMessage(ChatColor.GREEN + "Please restart your server for the changes to take effect; " + ChatColor.RED + "reloading the plugin is insufficient!");
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
                sender.sendMessage(ChatColor.RED + TumbleManager.getPermissionMessage());
            }
        }
        // Feedback for if no args were entered
        else {
            return false;
        }
        return true;
    }
}

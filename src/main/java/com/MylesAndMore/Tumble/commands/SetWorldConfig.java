package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.plugin.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SetWorldConfig implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Catch for null arguments
        if (args.length == 2) {
            if (sender.hasPermission("tumble.link")){
                // Initialize vars for their respective command arguments
                String world = args[0];
                String worldType = args[1];
                if (Objects.equals(worldType, "lobby")) {
                    // Check if the world is actually a world on the server
                    if (Bukkit.getWorld(world) != null) {
                        // Check if the world has already been configured
                        if (!Objects.equals(Constants.getGameWorld(), world)) {
                            // Set the specified value of the world in the config under lobbyWorld
                            Constants.getPlugin().getConfig().set("lobbyWorld", world);
                            Constants.getPlugin().saveConfig();
                            sender.sendMessage(ChatColor.GREEN + "Lobby world successfully linked: " + ChatColor.GRAY + world);
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
                else if (Objects.equals(args[1], "game")) {
                    if (Bukkit.getWorld(world) != null) {
                        if (!Objects.equals(Constants.getLobbyWorld(), world)) {
                            Constants.getPlugin().getConfig().set("gameWorld", world);
                            Constants.getPlugin().saveConfig();
                            // Set the gamerule of doImmediateRespawn in the gameWorld for later
                            Objects.requireNonNull(Bukkit.getWorld(world)).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
                            Objects.requireNonNull(Bukkit.getWorld(world)).setGameRule(GameRule.KEEP_INVENTORY, true);
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
                else {
                    sender.sendMessage(ChatColor.RED + "Allowed world types are " + ChatColor.GRAY + "lobby " + ChatColor.RED + "and " + ChatColor.GRAY + "game" + ChatColor.RED + ".");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + Constants.getPermissionMessage());
            }
        }
        else {
            return false;
        }
        return true;
    }
}

package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.PluginManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartGame implements CommandExecutor {
    String gameWorld = PluginManager.getPlugin().getConfig().getString("gameWorld");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has perms to run command
        if (sender.hasPermission("tumble.startgame")) {
            // Check if there is more than one person online
            if (Bukkit.getOnlinePlayers().size() > 0) {
                // Check if there is a gameWorld specified in config
                if (gameWorld != null) {
                    sender.sendMessage("Checking world, this could take a few moments...");
                    // Use multiverse to load game world
                    // If the load was successful, start game
                    if (PluginManager.getMVWorldManager().loadWorld(gameWorld)) {
                        sender.sendMessage("Starting game, please wait.");
                        // Generate the blocks in game world

                        // Move all players in lobby to the game world

                        // Give players game item (shovels/snowballs/etc.)
                    }
                    // If load was unsuccessful, give feedback
                    // Note: this should not occur unless the config file was edited externally,
                    // because the plugin prevents adding "worlds" that are not actually present to the config.
                    else {
                        sender.sendMessage(ChatColor.RED + "Failed to find a world named " + ChatColor.GRAY + gameWorld);
                        sender.sendMessage(ChatColor.RED + "Is the configuration file correct?");
                    }
                }
                // Feedback for if there is no gameWorld in the config
                else {
                    sender.sendMessage(ChatColor.RED + "Please link a game world first!");
                }
            }
            // Feedback for if there is only one person online
            else {
                sender.sendMessage(ChatColor.RED + "You can't start a game with yourself!");
            }
        }
        // Feedback for if the sender has no perms
        else {
            sender.sendMessage(ChatColor.RED + PluginManager.getPlugin().getConfig().getString("permissionMessage"));
        }
        return true;
    }
}

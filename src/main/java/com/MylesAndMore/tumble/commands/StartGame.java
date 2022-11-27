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
            if (Bukkit.getOnlinePlayers().size() > 1) {
                sender.sendMessage("Starting game...");
                // Use multiverse to load game world
                boolean includeLoaded = false;
                boolean worldLoaded = (PluginManager.getWorldManager().hasUnloadedWorld(gameWorld, includeLoaded));
                if (worldLoaded) {
                    PluginManager.getWorldManager().loadWorld(gameWorld);
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Failed to find a world named " + ChatColor.GRAY + gameWorld);
                    sender.sendMessage(ChatColor.RED + "Is the configuration file correct?");
                }
                // Generate the blocks in game world

                // Move all players in lobby to the game world

                // Give players game item (shovels/snowballs/etc.)

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

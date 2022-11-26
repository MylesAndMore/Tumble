package com.MylesAndMore.tumble.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class StartGame implements CommandExecutor {
    // Define tumble instances
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("tumble");
    String gameWorld = plugin.getConfig().getString("gameWorld");

    // Define multiverse instances
    MultiverseCore mv = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
    MVWorldManager mvWorldManager = mv.getMVWorldManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has perms to run command
        if (sender.hasPermission("tumble.startgame")) {
            // Check if there is more than one person online
            if (Bukkit.getOnlinePlayers().size() > 1) {
                sender.sendMessage("Starting game...");
                // Use multiverse to load game world
                boolean includeLoaded = false;
                boolean worldLoaded = (mvWorldManager.hasUnloadedWorld(gameWorld, includeLoaded));
                if (worldLoaded) {
                    mvWorldManager.loadWorld(gameWorld);
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
            sender.sendMessage(ChatColor.RED + plugin.getConfig().getString("permissionMessage"));
        }
        return true;
    }
}

package com.MylesAndMore.tumble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReloadCommand implements CommandExecutor {
    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("tumble");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender has perms to run command
        if (!sender.hasPermission("tumble.reload")) {
            // If sender does not have permission, display them the permissionMessage from the config
            sender.sendMessage(ChatColor.RED + plugin.getConfig().getString("permissionMessage"));
        }
        else {
            // If sender does have permission, reload the plugin's config and display a confirmation message
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Tumble configuration reloaded successfully.");
        }
        return true;
    }
}

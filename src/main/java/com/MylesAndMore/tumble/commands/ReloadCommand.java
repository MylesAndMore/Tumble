package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.PluginManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender has perms to run command
        if (sender.hasPermission("tumble.reload")) {
            // If sender does have permission, reload the plugin's config and display a confirmation message
            PluginManager.getPlugin().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Tumble configuration reloaded successfully.");
        }
        else {
            // If sender does not have permission, display them the permissionMessage from the config
            sender.sendMessage(ChatColor.RED + PluginManager.getPermissionMessage());
        }
        return true;
    }
}

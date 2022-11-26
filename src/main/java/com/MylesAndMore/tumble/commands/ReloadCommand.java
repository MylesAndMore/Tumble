package com.MylesAndMore.tumble.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
         if (!sender.hasPermission("tumble.reload")) {
             sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
            }
         else {
             Bukkit.getServer().getPluginManager().getPlugin("tumble").reloadConfig();
             sender.sendMessage(ChatColor.GREEN + "Tumble configuration reloaded.");
            }
        return true;
    }
}

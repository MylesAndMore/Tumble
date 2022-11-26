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
             sender.sendMessage(ChatColor.RED + Bukkit.getServer().getPluginManager().getPlugin("tumble").getConfig().getString("permissionMessage"));
            }
         else {
             Bukkit.getServer().getPluginManager().getPlugin("tumble").reloadConfig();
             sender.sendMessage(ChatColor.GREEN + "Tumble configuration reloaded successfully.");
            }
        return true;
    }
}

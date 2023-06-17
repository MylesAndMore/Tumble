package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.plugin.Constants;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("tumble.reload")) {
            Constants.getPlugin().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Tumble configuration reloaded successfully.");
        }
        else {
            sender.sendMessage(ChatColor.RED + Constants.getPermissionMessage());
        }
        return true;
    }
}

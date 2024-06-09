package com.MylesAndMore.Tumble.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.MylesAndMore.Tumble.Main.plugin;

public class Reload implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("tumble.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
            return false;
        }
        plugin.onEnable();
        sender.sendMessage(ChatColor.GREEN + "Tumble configuration reloaded successfully.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }
}
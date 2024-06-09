package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.game.Game;
import com.MylesAndMore.Tumble.plugin.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ForceStop implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!sender.hasPermission("tumble.forcestop")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
            return false;
        }

        Game game;
        if (args.length < 1 || args[0] == null) {
            game = ConfigManager.findGamePlayerIsIn((Player)sender);
            if (game == null) {
                sender.sendMessage(ChatColor.RED + "Missing arena name");
                return false;
            }
        }
        else {
            game = ConfigManager.arenas.get(args[0]).game;
        }

        game.killGame();
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return ConfigManager.arenas.keySet().stream().toList();
        }
        return new ArrayList<>();
    }
}

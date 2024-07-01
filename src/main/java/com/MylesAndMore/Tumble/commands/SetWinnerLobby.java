package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.plugin.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static com.MylesAndMore.Tumble.Main.arenaManager;
import static com.MylesAndMore.Tumble.Main.languageManager;

public class SetWinnerLobby implements SubCommand, CommandExecutor, TabCompleter {
    @Override
    public String getCommandName() {
        return "setWinnerLobby";
    }

    @Override
    public String getPermission() {
        return "tumble.setWinnerLobby";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(languageManager.fromKey("not-for-console"));
            return false;
        }

        if (args.length == 0 || args[0] == null || args[0].isEmpty()) {
            sender.sendMessage(languageManager.fromKey("missing-arena-parameter"));
            return false;
        }
        String arenaName = args[0];

        if (!arenaManager.arenas.containsKey(arenaName)) {
            sender.sendMessage(languageManager.fromKey("invalid-arena").replace("%arena%",arenaName));
            return false;
        }
        Arena arena = arenaManager.arenas.get(arenaName);

        arena.winnerLobby = ((Player)sender).getLocation();
        arenaManager.WriteConfig();
        sender.sendMessage(languageManager.fromKey("set-success"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return arenaManager.arenas.keySet().stream().toList();
        }

        return Collections.emptyList();
    }
}

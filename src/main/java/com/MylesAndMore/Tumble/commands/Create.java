package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.plugin.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static com.MylesAndMore.Tumble.Main.arenaManager;
import static com.MylesAndMore.Tumble.Main.languageManager;

public class Create implements SubCommand, CommandExecutor, TabCompleter {
    @Override
    public String getCommandName() {
        return "create";
    }

    @Override
    public String getPermission() {
        return "tumble.create";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || args[0] == null || args[0].isEmpty()) {
            sender.sendMessage(languageManager.fromKey("missing-arena-parameter"));
            return false;
        }

        String arenaName = args[0];
        arenaManager.arenas.put(arenaName, new Arena(arenaName));
        arenaManager.WriteConfig();
        sender.sendMessage(languageManager.fromKey("create-success"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return Collections.emptyList();
    }
}

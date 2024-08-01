package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.config.ArenaManager;
import com.MylesAndMore.Tumble.config.LanguageManager;
import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.plugin.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.MylesAndMore.Tumble.Main.plugin;

public class Reload implements SubCommand, CommandExecutor, TabCompleter {

    @Override
    public String getCommandName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "tumble.reload";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        for (Arena a : ArenaManager.arenas.values()) {
            if (a.game != null) {
                a.game.stopGame();
            }
        }

        plugin.onEnable();
        sender.sendMessage(LanguageManager.fromKey("reload-success"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }
}

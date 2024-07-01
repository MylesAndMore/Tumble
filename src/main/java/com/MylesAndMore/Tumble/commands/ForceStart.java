package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.game.Game;
import com.MylesAndMore.Tumble.plugin.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.MylesAndMore.Tumble.Main.arenaManager;
import static com.MylesAndMore.Tumble.Main.languageManager;

public class ForceStart implements SubCommand, CommandExecutor, TabCompleter {

    @Override
    public String getCommandName() {
        return "forceStart";
    }

    @Override
    public String getPermission() {
        return "tumble.forceStart";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        Game game;
        if (args.length < 1 || args[0] == null) {
            game = arenaManager.findGamePlayerIsIn((Player)sender);
            if (game == null) {
                sender.sendMessage(languageManager.fromKey("missing-arena-parameter"));
                return false;
            }
        }
        else {
            String arenaName = args[0];
            if (!arenaManager.arenas.containsKey(arenaName)) {
                sender.sendMessage(languageManager.fromKey("invalid-arena").replace("%arena%",arenaName));
                return false;
            }
            game = arenaManager.arenas.get(arenaName).game;
        }

        if (game == null) {
            sender.sendMessage(languageManager.fromKey("no-game-in-arena"));
            return false;
        }

        game.gameStart();
        sender.sendMessage(languageManager.fromKey("forcestart-success"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return arenaManager.arenas.keySet().stream().toList();
        }
        return new ArrayList<>();
    }
}

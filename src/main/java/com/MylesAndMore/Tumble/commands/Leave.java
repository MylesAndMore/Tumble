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

public class Leave implements SubCommand, CommandExecutor, TabCompleter {

    @Override
    public String getCommandName() {
        return "leave";
    }

    @Override
    public String getPermission() {
        return "tumble.leave";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(languageManager.fromKey("not-for-console"));
            return false;
        }

        Game game = arenaManager.findGamePlayerIsIn((Player)sender);
        if (game == null) {
            sender.sendMessage(languageManager.fromKey("no-game-in-arena"));
            return false;
        }

        game.removePlayer((Player) sender);
        sender.sendMessage(languageManager.fromKey("leave-success")
                .replace("%arena%", game.arena.name)
                .replace("%type%", game.type.toString()));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }
}
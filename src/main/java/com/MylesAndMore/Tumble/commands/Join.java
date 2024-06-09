package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.plugin.ConfigManager;
import com.MylesAndMore.Tumble.game.Game;
import com.MylesAndMore.Tumble.plugin.GameState;
import com.MylesAndMore.Tumble.plugin.GameType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Join implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This cannot be run by the console");
            return false;
        }

        if (!sender.hasPermission("tumble.join")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
            return false;
        }

        if (ConfigManager.findGamePlayerIsIn((Player)sender) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a game! Leave it to join another one");
        }

        if (args.length < 1 || args[0] == null) {
            sender.sendMessage(ChatColor.RED + "Missing arena name");
            return false;
        }
        String arenaName = args[0];
        if (!ConfigManager.arenas.containsKey(arenaName))
        {
            sender.sendMessage(ChatColor.RED + "This arena does not exist");
            return false;
        }
        Arena arena = ConfigManager.arenas.get(arenaName);

        Game game;
        if (args.length < 2 || args[1] == null) {
            if (arena.game == null) {
                sender.sendMessage(ChatColor.RED + "no game is currently taking place in this arena, specify the game type to start one");
                return false;
            }
            else {
                game = arena.game;
            }
        }
        else {
            GameType type;
            switch (args[1]) {
                case "shovels", "shovel"     -> type = GameType.SHOVELS;
                case "snowballs", "snowball" -> type = GameType.SNOWBALLS;
                case "mix", "mixed"          -> type = GameType.MIXED;
                default                      -> {
                    sender.sendMessage(ChatColor.RED + "Invalid game type");
                    return false;
                }
            }

            if (arena.game == null) {
                game = arena.game = new Game(arena, type);
            }
            else {
                sender.sendMessage(ChatColor.RED + "A game of "+type+" is currently taking place in this arena, choose another arena or join it with /tumble:join "+arena.name+" "+type);
                return false;
            }
        }

        if (game.gameState != GameState.WAITING) {
            sender.sendMessage(ChatColor.RED + "This game is still in progress, wait until it finishes or join another game");
            return false;
        }

        game.addPlayer((Player)sender);
        sender.sendMessage(ChatColor.GREEN + "Joined game " + arena.name + " - " + game.type);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return ConfigManager.arenas.keySet().stream().toList();
        }
        if (args.length == 2) {
            return Arrays.stream(GameType.values()).map(Objects::toString).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
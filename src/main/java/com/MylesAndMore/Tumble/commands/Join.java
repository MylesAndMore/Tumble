package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.config.ArenaManager;
import com.MylesAndMore.Tumble.config.LanguageManager;
import com.MylesAndMore.Tumble.game.Arena;
import com.MylesAndMore.Tumble.game.Game;
import com.MylesAndMore.Tumble.plugin.GameState;
import com.MylesAndMore.Tumble.plugin.GameType;
import com.MylesAndMore.Tumble.plugin.SubCommand;
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

public class Join implements SubCommand, CommandExecutor, TabCompleter {

    @Override
    public String getCommandName() {
        return "join";
    }

    @Override
    public String getPermission() {
        return "tumble.join";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage(LanguageManager.fromKey("not-for-console"));
            return false;
        }

        if (ArenaManager.findGamePlayerIsIn((Player)sender) != null) {
            sender.sendMessage(LanguageManager.fromKey("already-in-game"));
            return false;
        }

        if (args.length < 1 || args[0] == null) {
            sender.sendMessage(LanguageManager.fromKey("missing-arena-parameter"));
            return false;
        }
        String arenaName = args[0];
        if (!ArenaManager.arenas.containsKey(arenaName)) {
            sender.sendMessage(LanguageManager.fromKey("invalid-arena").replace("%arena%", arenaName));
            return false;
        }
        Arena arena = ArenaManager.arenas.get(arenaName);

        Game game;
        if (args.length < 2 || args[1] == null) {
            // No type specified: try to infer game type from game taking place in the arena
            if (arena.game == null) {
                // Can't infer if no game is taking place
                sender.sendMessage(LanguageManager.fromKey("specify-game-type"));
                return false;
            }

            game = arena.game;
        } else {
            // Game type specified
            GameType type;
            switch (args[1]) {
                case "shovels", "shovel"     -> type = GameType.SHOVELS;
                case "snowballs", "snowball" -> type = GameType.SNOWBALLS;
                case "mix", "mixed"          -> type = GameType.MIXED;
                default                      -> {
                    sender.sendMessage(LanguageManager.fromKey("invalid-type"));
                    return false;
                }
            }

            if (arena.game == null) {
                // no game is taking place in this arena, start one
                game = arena.game = new Game(arena, type);
            } else {
                // a game is taking place in this arena, check that it is the right type
                if (arena.game.type == type) {
                    game = arena.game;
                }
                else {
                    sender.sendMessage(LanguageManager.fromKey("another-type-in-arena")
                            .replace("%type%",type.toString())
                            .replace("%arena%",arenaName));
                    return false;
                }
            }
        }

        // Check to make sure the arena has a game spawn
        if (game.arena.gameSpawn == null) {
            if (p.isOp()) {
                sender.sendMessage(LanguageManager.fromKey("arena-not-ready-op"));
            } else {
                sender.sendMessage(LanguageManager.fromKey("arena-not-ready"));
            }
            return false;
        }

        if (game.gameState != GameState.WAITING) {
            sender.sendMessage(LanguageManager.fromKey("game-in-progress"));
            return false;
        }

        game.addPlayer((Player)sender);
        sender.sendMessage(LanguageManager.fromKey("join-success")
                .replace("%type%", game.type.toString())
                .replace("%arena%", arena.name));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            return ArenaManager.arenas.keySet().stream().toList();
        }
        if (args.length == 2) {
            return Arrays.stream(GameType.values()).map(Objects::toString).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
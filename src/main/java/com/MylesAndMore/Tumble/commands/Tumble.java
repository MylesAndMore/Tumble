package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.plugin.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.MylesAndMore.Tumble.Main.languageManager;

public class Tumble implements CommandExecutor, TabCompleter {

    private static final Map<String, SubCommand> subCommands = Map.ofEntries(
            CmdNameAsKey(new Create()),
            CmdNameAsKey(new ForceStart()),
            CmdNameAsKey(new ForceStop()),
            CmdNameAsKey(new Join()),
            CmdNameAsKey(new Leave()),
            CmdNameAsKey(new Reload()),
            CmdNameAsKey(new Remove()),
            CmdNameAsKey(new SetGameSpawn()),
            CmdNameAsKey(new SetKillYLevel()),
            CmdNameAsKey(new SetLobby()),
            CmdNameAsKey(new SetWaitArea()),
            CmdNameAsKey(new SetWinnerLobby())
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!subCommands.containsKey(args[0])) {
            sender.sendMessage(languageManager.fromKey("unknown-command"));
            return true;
        }

        var subCmd = subCommands.get(args[0]);

        if (!sender.hasPermission(subCmd.getPermission())) {
            sender.sendMessage(languageManager.fromKey("no-permission").replace("%permission%", subCmd.getPermission()));
            return false;
        }

        subCmd.onCommand(sender, command, args[0], removeFirst(args));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            ArrayList<String> PermittedSubCmds = new ArrayList<>();
            for (SubCommand subCmd: subCommands.values()) {
                if (sender.hasPermission(subCmd.getPermission())) {
                    PermittedSubCmds.add(subCmd.getCommandName());
                }
            }
            return PermittedSubCmds;
        }

        if (args.length > 1) {
            if (!subCommands.containsKey(args[0])) {
                return Collections.emptyList();
            }

            if (subCommands.get(args[0]) instanceof TabCompleter tcmp) {
                return tcmp.onTabComplete(sender, command, args[0], removeFirst(args));
            }
            else {
                return null;
            }
        }

        return Collections.emptyList();
    }

    private String[] removeFirst(String[] arr) {
        ArrayList<String> tmp = new ArrayList<>(List.of(arr));
        tmp.remove(0);
        return tmp.toArray(new String[0]);
    }

    private static Map.Entry<String, SubCommand> CmdNameAsKey(SubCommand s) {
        return Map.entry(s.getCommandName(),s);
    }
}

package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.config.LanguageManager;
import com.MylesAndMore.Tumble.plugin.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Tumble implements CommandExecutor, TabCompleter {

    private static final Map<String, SubCommand> subCommands = Map.ofEntries(
        cmdNameAsKey(new Create()),
        cmdNameAsKey(new ForceStart()),
        cmdNameAsKey(new ForceStop()),
        cmdNameAsKey(new Join()),
        cmdNameAsKey(new Leave()),
        cmdNameAsKey(new Reload()),
        cmdNameAsKey(new Remove()),
        cmdNameAsKey(new SetGameSpawn()),
        cmdNameAsKey(new SetKillYLevel()),
        cmdNameAsKey(new SetLobby()),
        cmdNameAsKey(new SetWaitArea()),
        cmdNameAsKey(new SetWinnerLobby())
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || args[0] == null || args[0].isEmpty()) {
            sender.sendMessage(LanguageManager.fromKey("missing-subcommand"));
            return true;
        }
        String subCmdName = args[0];

        if (!subCommands.containsKey(subCmdName)) {
            sender.sendMessage(LanguageManager.fromKey("unknown-command").replace("%command%", subCmdName));
            return true;
        }

        var subCmd = subCommands.get(subCmdName);

        if (!sender.hasPermission(subCmd.getPermission())) {
            sender.sendMessage(LanguageManager.fromKey("no-permission").replace("%permission%", subCmd.getPermission()));
            return true;
        }

        // Pass command action through to subCommand
        subCmd.onCommand(sender, command, subCmdName, removeFirst(args));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            // Show only subCommands the user has permission for
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

            // Pass tab complete through to subCommand
            if (subCommands.get(args[0]) instanceof TabCompleter tcmp) {
                return tcmp.onTabComplete(sender, command, args[0], removeFirst(args));
            } else {
                return null;
            }
        }

        return Collections.emptyList();
    }

    /**
     * Create a copy of an array with the first element removed
     * @param arr the source array
     * @return the source without the first element
     */
    private String[] removeFirst(String[] arr) {
        ArrayList<String> tmp = new ArrayList<>(List.of(arr));
        tmp.remove(0);
        return tmp.toArray(new String[0]);
    }

    /**
     * Creates a map entry with the name of the subCommand as the key and the subCommand itself as the value
     * @param cmd The subCommand to use
     * @return A map entry from the subCommand
     */
    private static Map.Entry<String, SubCommand> cmdNameAsKey(SubCommand cmd) {
        return Map.entry(cmd.getCommandName(),cmd);
    }
}

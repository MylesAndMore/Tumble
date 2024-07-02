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

        // pass command action through to subCommand
        subCmd.onCommand(sender, command, args[0], removeFirst(args));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            // show only subCommands the user has permission for
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

            // pass tab complete through to subCommand
            if (subCommands.get(args[0]) instanceof TabCompleter tcmp) {
                return tcmp.onTabComplete(sender, command, args[0], removeFirst(args));
            }
            else {
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
    private static Map.Entry<String, SubCommand> CmdNameAsKey(SubCommand cmd) {
        return Map.entry(cmd.getCommandName(),cmd);
    }
}

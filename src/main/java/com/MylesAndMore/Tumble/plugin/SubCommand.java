package com.MylesAndMore.Tumble.plugin;

import org.bukkit.command.CommandExecutor;

/**
 * Requires that subCommands have a commandName and permission getter.
 * This allows the permission and commandName to be checked from the base command.
 */
public interface SubCommand extends CommandExecutor {
    String getCommandName();
    String getPermission();
}

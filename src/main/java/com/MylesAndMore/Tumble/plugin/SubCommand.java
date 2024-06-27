package com.MylesAndMore.Tumble.plugin;

import org.bukkit.command.CommandExecutor;

public interface SubCommand extends CommandExecutor {
    public String getCommandName();
    public String getPermission();
}

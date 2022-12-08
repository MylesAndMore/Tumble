package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.TumbleManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class SetAutoStart implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has perms to run command
        if (sender.hasPermission("autostart")) {
            // Check if game and lobby worlds are null
            if (TumbleManager.getGameWorld() != null) {
                if (TumbleManager.getLobbyWorld() != null) {
                    // Check the player # argument and parse it into an int
                    int args0 = 0;
                    try {
                        args0 = Integer.parseInt(args[0]);
                    } catch (NumberFormatException nfe){
                        sender.sendMessage(ChatColor.RED + "Player amount must be a valid number.");
                    } catch (Exception e){
                        sender.sendMessage(ChatColor.RED + "Invalid player amount.");
                    }
                    // Check the amount of args entered
                    if (args.length == 2) {
                        // PlayerAmount & enable/disable were entered
                        // Check if a playerAmount between 2-8 was entered
                        if ((args0 >= 2) && (args0 <= 8)) {
                            if (Objects.equals(args[1], "enable")) {
                                // Write values to the config
                                TumbleManager.getPlugin().getConfig().set("autoStart.players", args0);
                                TumbleManager.getPlugin().getConfig().set("autoStart.enabled", args[1]);
                                TumbleManager.getPlugin().saveConfig();
                            }
                            else if (Objects.equals(args[1], "disable")) {
                                TumbleManager.getPlugin().getConfig().set("autoStart.players", args0);
                                TumbleManager.getPlugin().getConfig().set("autoStart.enabled", args[1]);
                                TumbleManager.getPlugin().saveConfig();
                            }
                            else {
                                return false;
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Please enter a player amount between two and eight!");
                        }
                    }
                    else if (args.length == 1) {
                        // Only PlayerAmount was entered
                        if ((args0 >= 2) && (args0 <= 8)) {
                            TumbleManager.getPlugin().getConfig().set("autoStart.players", args0);
                            TumbleManager.getPlugin().saveConfig();
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Please enter a player amount between two and eight!");
                        }
                    }
                    else {
                        return false;
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Please link a lobby world first!");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Please link a game world first!");
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + TumbleManager.getPermissionMessage());
        }
        return true;
    }
}

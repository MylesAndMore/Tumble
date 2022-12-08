package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.TumbleManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetWinnerLoc implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has perms to run command
        if (sender.hasPermission("tumble.winlocation")) {
            // Check if the lobby world has been configured
            if (TumbleManager.getLobbyWorld() != null) {
                // Check if the sender is a player
                if (sender instanceof Player) {
                    Location senderPos = ((Player) sender).getLocation();
                    // if so, check if any of their locations are zero
                    if (!((senderPos.getX() == 0) || (senderPos.getY() == 0) || (senderPos.getZ() == 0))) {
                        // set the config values to their current pos
                        TumbleManager.getPlugin().getConfig().set("winnerTeleport.x", senderPos.getX());
                        TumbleManager.getPlugin().getConfig().set("winnerTeleport.y", senderPos.getY());
                        TumbleManager.getPlugin().getConfig().set("winnerTeleport.z", senderPos.getZ());
                        TumbleManager.getPlugin().saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Win location successfully set!");
                        sender.sendMessage(ChatColor.GREEN + "Run " + ChatColor.GRAY +  "/tumble:reload " + ChatColor.GREEN + "the changes to take effect.");
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Your coordinates cannot be zero!");
                        sender.sendMessage(ChatColor.RED + "Use something like 0.5 (the middle of the block) instead.");
                    }
                }
                // Check if the sender is the console
                else if (sender instanceof ConsoleCommandSender) {
                    // Check if the correct # of args were entered
                    if (args.length == 3) {
                        double args0 = 0;
                        double args1 = 0;
                        double args2 = 0;
                        try {
                            args0 = Double.parseDouble(args[0]);
                            args1 = Double.parseDouble(args[1]);
                            args2 = Double.parseDouble(args[2]);
                        } catch (NumberFormatException nfe){
                            sender.sendMessage(ChatColor.RED + "Input arguments must be valid numbers.");
                            return false;
                        } catch (Exception e){
                            sender.sendMessage(ChatColor.RED + "Invalid input arguments.");
                            return false;
                        }
                        // Check if any of the args were 0 (this will cause future problems so we prevent it here)
                        if (!((args0 == 0) || (args1 == 0) || (args2 == 0))) {
                            TumbleManager.getPlugin().getConfig().set("winnerTeleport.x", args0);
                            TumbleManager.getPlugin().getConfig().set("winnerTeleport.y", args1);
                            TumbleManager.getPlugin().getConfig().set("winnerTeleport.z", args2);
                            TumbleManager.getPlugin().saveConfig();
                            sender.sendMessage(ChatColor.GREEN + "Win location successfully set!");
                            sender.sendMessage(ChatColor.GREEN + "Run " + ChatColor.GRAY +  "/tumble:reload " + ChatColor.GREEN + "the changes to take effect.");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Your coordinates cannot be zero!");
                            sender.sendMessage(ChatColor.RED + "Use something like 0.5 (the middle of the block) instead.");
                        }
                    }
                    else {
                        return false;
                    }
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Please link a lobby world first!");
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + TumbleManager.getPermissionMessage());
        }
        return true;
    }
}

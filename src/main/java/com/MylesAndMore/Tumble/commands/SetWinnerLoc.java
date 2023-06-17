package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.plugin.Constants;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetWinnerLoc implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("tumble.winlocation")) {
            if (Constants.getLobbyWorld() != null) {
                if (sender instanceof Player) {
                    // Check the sender entered the correct number of args
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
                        } catch (Exception e){
                            sender.sendMessage(ChatColor.RED + "Invalid input arguments.");
                        }
                        // Check if any of the args were 0 (this will cause future problems, so we prevent it here)
                        if (!((args0 == 0) || (args1 == 0) || (args2 == 0))) {
                            Constants.getPlugin().getConfig().set("winnerTeleport.x", args0);
                            Constants.getPlugin().getConfig().set("winnerTeleport.y", args1);
                            Constants.getPlugin().getConfig().set("winnerTeleport.z", args2);
                            Constants.getPlugin().saveConfig();
                            sender.sendMessage(ChatColor.GREEN + "Win location successfully set!");
                            sender.sendMessage(ChatColor.GREEN + "Run " + ChatColor.GRAY +  "/tumble:reload " + ChatColor.GREEN + "the changes to take effect.");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Your coordinates cannot be zero!");
                            sender.sendMessage(ChatColor.RED + "Use something like 0.5 (the middle of the block) instead.");
                        }
                    }
                    // If the sender entered no args, use their current location
                    else if (args.length == 0) {
                        Location senderPos = ((Player) sender).getLocation();
                        // if so, check if any of their locations are zero
                        if (!((senderPos.getX() == 0) || (senderPos.getY() == 0) || (senderPos.getZ() == 0))) {
                            // set the config values to their current pos
                            Constants.getPlugin().getConfig().set("winnerTeleport.x", senderPos.getX());
                            Constants.getPlugin().getConfig().set("winnerTeleport.y", senderPos.getY());
                            Constants.getPlugin().getConfig().set("winnerTeleport.z", senderPos.getZ());
                            Constants.getPlugin().saveConfig();
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
                else if (sender instanceof ConsoleCommandSender) {
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
                        } catch (Exception e){
                            sender.sendMessage(ChatColor.RED + "Invalid input arguments.");
                        }
                        if (!((args0 == 0) || (args1 == 0) || (args2 == 0))) {
                            Constants.getPlugin().getConfig().set("winnerTeleport.x", args0);
                            Constants.getPlugin().getConfig().set("winnerTeleport.y", args1);
                            Constants.getPlugin().getConfig().set("winnerTeleport.z", args2);
                            Constants.getPlugin().saveConfig();
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
            sender.sendMessage(ChatColor.RED + Constants.getPermissionMessage());
        }
        return true;
    }
}

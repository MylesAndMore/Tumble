package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.plugin.Constants;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SetAutoStart implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("tumble.autostart")) {
            if (Constants.getGameWorld() != null) {
                if (Constants.getLobbyWorld() != null) {
                    if (args.length == 2) {
                        // Check the player # argument and parse it into an int
                        int args0;
                        try {
                            args0 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException nfe){
                            sender.sendMessage(ChatColor.RED + "Player amount must be a valid number.");
                            return true;
                        } catch (Exception e){
                            sender.sendMessage(ChatColor.RED + "Invalid player amount.");
                            return true;
                        }
                        // PlayerAmount & enable/disable were entered
                        if ((args0 >= 2) && (args0 <= 8)) {
                            if (Objects.equals(args[1], "enable")) {
                                // Write values to the config
                                Constants.getPlugin().getConfig().set("autoStart.players", args0);
                                Constants.getPlugin().getConfig().set("autoStart.enabled", true);
                                Constants.getPlugin().saveConfig();
                                sender.sendMessage(ChatColor.GREEN + "Configuration saved!");
                                sender.sendMessage(ChatColor.GREEN + "Run " + ChatColor.GRAY +  "/tumble:reload " + ChatColor.GREEN + "the changes to take effect.");
                            }
                            else if (Objects.equals(args[1], "disable")) {
                                Constants.getPlugin().getConfig().set("autoStart.players", args0);
                                Constants.getPlugin().getConfig().set("autoStart.enabled", false);
                                Constants.getPlugin().saveConfig();
                                sender.sendMessage(ChatColor.GREEN + "Configuration saved!");
                                sender.sendMessage(ChatColor.GREEN + "Run " + ChatColor.GRAY +  "/tumble:reload " + ChatColor.GREEN + "the changes to take effect.");
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
                        int args0;
                        try {
                            args0 = Integer.parseInt(args[0]);
                        } catch (NumberFormatException nfe){
                            sender.sendMessage(ChatColor.RED + "Player amount must be a valid number.");
                            return true;
                        } catch (Exception e){
                            sender.sendMessage(ChatColor.RED + "Invalid player amount.");
                            return true;
                        }
                        if ((args0 >= 2) && (args0 <= 8)) {
                            Constants.getPlugin().getConfig().set("autoStart.players", args0);
                            Constants.getPlugin().saveConfig();
                            sender.sendMessage(ChatColor.GREEN + "Configuration saved!");
                            sender.sendMessage(ChatColor.GREEN + "Run " + ChatColor.GRAY +  "/tumble:reload " + ChatColor.GREEN + "the changes to take effect.");
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
            sender.sendMessage(ChatColor.RED + Constants.getPermissionMessage());
        }
        return true;
    }
}

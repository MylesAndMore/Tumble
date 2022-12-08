package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.Game;
import com.MylesAndMore.tumble.TumbleManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class StartGame implements CommandExecutor {
    public void startGame(CommandSender sender, String[] args) {
        // Check if sender has perms to run command
        if (sender.hasPermission("tumble.start")) {
            // Check if there is a lobbyWorld specified in config
            if (TumbleManager.getLobbyWorld() != null) {
                // Check if there is more than one person in lobby
                if (TumbleManager.getPlayersInLobby().size() > 0) {
                    // Check if there is a gameWorld specified in config
                    if (TumbleManager.getGameWorld() != null) {
                        // Use multiverse to load game world
                        // If the load was successful, start game
                        if (TumbleManager.getMVWorldManager().loadWorld(TumbleManager.getGameWorld())) {
                            // Check which gamemode to initiate from the config file
                            if (!Game.getGame().startGame()) {
                                sender.sendMessage(ChatColor.BLUE + "Starting game, please wait.");
                                // Sender feedback for if the game failed to start
                                if (Objects.equals(Game.getGame().getGameState(), "starting")) {
                                    sender.sendMessage(ChatColor.RED + "A game is already starting!");
                                }
                                else if (Objects.equals(Game.getGame().getGameState(), "running")) {
                                    sender.sendMessage(ChatColor.RED + "A game is already running!");
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED + "Failed to recognize game of type " + ChatColor.GRAY + TumbleManager.getPlugin().getConfig().getString("gameMode"));
                                }
                            }
                        }
                        // If load was unsuccessful, give feedback
                        // Note: this should not occur unless the config file was edited externally,
                        // because the plugin prevents adding "worlds" that are not actually present to the config.
                        else {
                            sender.sendMessage(ChatColor.RED + "Failed to find a world named " + ChatColor.GRAY + TumbleManager.getGameWorld());
                            sender.sendMessage(ChatColor.RED + "Is the configuration file correct?");
                        }
                    }
                    // Feedback for if there is no gameWorld in the config
                    else {
                        sender.sendMessage(ChatColor.RED + "Please link a game world first!");
                    }
                }
                // Feedback for if there is only one person online
                else {
                    sender.sendMessage(ChatColor.RED + "You can't start a game with yourself!");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Please link a lobby world first!");
            }
        }
        // Feedback for if the sender has no perms
        else {
            sender.sendMessage(ChatColor.RED + TumbleManager.getPermissionMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        startGame(sender, args);
        return true;
    }
}

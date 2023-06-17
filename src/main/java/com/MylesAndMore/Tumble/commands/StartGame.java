package com.MylesAndMore.Tumble.commands;

import com.MylesAndMore.Tumble.game.Game;
import com.MylesAndMore.Tumble.plugin.Constants;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StartGame implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("tumble.start")) {
            if (Constants.getLobbyWorld() != null) {
                if (Constants.getPlayersInLobby().size() > 1) {
                    if (Constants.getGameWorld() != null) {
                        if (!Objects.equals(Game.getGame().getGameState(), "waiting")) {
                            sender.sendMessage(ChatColor.BLUE + "Generating layers, please wait.");
                            // Use multiverse to load game world--if the load was successful, start game
                            if (Constants.getMVWorldManager().loadWorld(Constants.getGameWorld())) {
                                // If there is no starting argument,
                                if (args.length == 0) {
                                    // pull which gamemode to initiate from the config file
                                    if (!Game.getGame().startGame(Constants.getGameType())) {
                                        // Sender feedback for if the game failed to start
                                        if (Objects.equals(Game.getGame().getGameState(), "starting")) {
                                            sender.sendMessage(ChatColor.RED + "A game is already starting!");
                                        }
                                        else if (Objects.equals(Game.getGame().getGameState(), "running")) {
                                            sender.sendMessage(ChatColor.RED + "A game is already running!");
                                        }
                                        else {
                                            sender.sendMessage(ChatColor.RED + "Failed to recognize game of type " + ChatColor.GRAY + Constants.getPlugin().getConfig().getString("gameMode"));
                                        }
                                    }
                                }
                                // If there was an argument for gameType, pass that instead
                                else {
                                    if (!Game.getGame().startGame(args[0])) {
                                        // Sender feedback for if the game failed to start
                                        if (Objects.equals(Game.getGame().getGameState(), "starting")) {
                                            sender.sendMessage(ChatColor.RED + "A game is already starting!");
                                        }
                                        else if (Objects.equals(Game.getGame().getGameState(), "running")) {
                                            sender.sendMessage(ChatColor.RED + "A game is already running!");
                                        }
                                        else {
                                            sender.sendMessage(ChatColor.RED + "Failed to recognize game of type " + ChatColor.GRAY + args[0]);
                                        }
                                    }
                                }
                            }
                            // If load was unsuccessful, give feedback
                            // Note: this should not occur unless the config file was edited externally,
                            // because the plugin prevents adding "worlds" that are not actually present to the config.
                            else {
                                sender.sendMessage(ChatColor.RED + "Failed to find a world named " + ChatColor.GRAY + Constants.getGameWorld());
                                sender.sendMessage(ChatColor.RED + "Is the configuration file correct?");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "A game is already queued to begin!");
                        }
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "Please link a game world first!");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "You can't start a game with yourself!");
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

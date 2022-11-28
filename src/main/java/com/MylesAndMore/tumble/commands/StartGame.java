package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.GameManager;
import com.MylesAndMore.tumble.TumbleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StartGame implements CommandExecutor {
    // Define the startGame method so that other classes can refrence it
    public void startGame(CommandSender sender, String[] args) {
        // Check if sender has perms to run command
        if (sender.hasPermission("tumble.start")) {
            // Check if there is a lobbyWorld specified in config
            if (TumbleManager.getLobbyWorld() != null) {
                // Check if there is more than one person in lobby
                if (TumbleManager.getPlayersInLobby().size() > 1) {
                    // Check if there is a gameWorld specified in config
                    if (TumbleManager.getGameWorld() != null) {
                        sender.sendMessage("Checking world, this could take a few moments...");
                        // Use multiverse to load game world
                        // If the load was successful, start game
                        if (TumbleManager.getMVWorldManager().loadWorld(TumbleManager.getGameWorld())) {
                            sender.sendMessage("Starting game, please wait.");
                            // Check which gamemode to initiate from the config file
                            if (GameManager.createGame(TumbleManager.getPlugin().getConfig().getString("gameMode"))) {
                                // If game type exists, send players to the world
                                // At this point, layers have been generated, and items have been allotted from the createGame method
                                sendWorld();
                            }
                            else {
                                // If game type does not exist, give sender feedback
                                sender.sendMessage(ChatColor.RED + "Failed to recognize game of type " + ChatColor.GRAY + TumbleManager.getPlugin().getConfig().getString("gameMode"));
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

    public void sendWorld() {
        // Create Locations to scatter players around the first layer

        // While there are still players in the lobby, send them to the gameWorld
        // This is just a way of sending everybody in the lobby to the game
        for (List<Player> playersInLobby = TumbleManager.getPlayersInLobby(); playersInLobby.size() > 0; playersInLobby = TumbleManager.getPlayersInLobby()) {
            // Get a singular player from the player list
            Player aPlayer = playersInLobby.get(0);
            // Teleport that player to the spawn of the gameWorld
            aPlayer.teleport(Bukkit.getWorld(TumbleManager.getGameWorld()).getSpawnLocation());
        }

        // Add a little break because it can take the clients a bit to load into the new world
        // Then, transition to another method because this one is getting really long
        // In that method: set a flag to monitor the playerDeathEvent so we know when all the players have died
        // Also start music
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        startGame(sender, args);
        return true;
    }
}

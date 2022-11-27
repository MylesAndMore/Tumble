package com.MylesAndMore.tumble.commands;

import com.MylesAndMore.tumble.PluginManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StartGame implements CommandExecutor {
    // Define game and lobby world vars because they get used often in this class
    String gameWorld = PluginManager.getPlugin().getConfig().getString("gameWorld");
    String lobbyWorld = PluginManager.getPlugin().getConfig().getString("lobbyWorld");
    // Define a method for getting players in the lobby because that also gets used a lot here
    public List<Player> getPlayersInLobby() { return Bukkit.getServer().getWorld(lobbyWorld).getPlayers(); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender has perms to run command
        if (sender.hasPermission("tumble.startgame")) {
            // Check if there is a lobbyWorld specified in config
            if (lobbyWorld != null) {
                // Check if there is more than one person in lobby
                if (getPlayersInLobby().size() > 0) {
                    // Check if there is a gameWorld specified in config
                    if (gameWorld != null) {
                        sender.sendMessage("Checking world, this could take a few moments...");
                        // Use multiverse to load game world
                        // If the load was successful, start game
                        if (PluginManager.getMVWorldManager().loadWorld(gameWorld)) {
                            sender.sendMessage("Starting game, please wait.");

                            // Generate the blocks in game world


                            // While there are still players in the lobby, send them to the gameWorld
                            // This is just a way of sending everybody in the lobby to the game
                            for (List<Player> playersInLobby = getPlayersInLobby(); playersInLobby.size() > 0; playersInLobby = getPlayersInLobby()) {
                                // Get a singular player from the player list
                                Player aPlayer = playersInLobby.get(0);
                                // Teleport that player to the spawn of the gameWorld
                                aPlayer.teleport(Bukkit.getWorld(gameWorld).getSpawnLocation());
                            }

                            // Give players game item (shovels/snowballs/etc.)

                            // Add a little break because it can take the clients a bit to load into the new world
                            // Then, transition to another method because this one is getting really long
                        }
                        // If load was unsuccessful, give feedback
                        // Note: this should not occur unless the config file was edited externally,
                        // because the plugin prevents adding "worlds" that are not actually present to the config.
                        else {
                            sender.sendMessage(ChatColor.RED + "Failed to find a world named " + ChatColor.GRAY + gameWorld);
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
            sender.sendMessage(ChatColor.RED + PluginManager.getPlugin().getConfig().getString("permissionMessage"));
        }
        return true;
    }
}

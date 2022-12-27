# Tumble  

## Overview  

Tumble is a Spigot/Paper plugin that aims to recreate the Tumble minigame from the bygone era of the Minecraft Legacy Console Editions.  

## What *is* Tumble?

If you've never heard of it, [Tumble](https://minecraft-archive.fandom.com/wiki/Tumble_Mode) is a twist on the classic Minecraft minigame of spleef, where the objective is to break the blocks under your opponents. But in Tumble, you play on randomly generated layers of blocks, using shovels, snowballs, or both to try and eliminate your opponents.

## Features  

- Choose from three different game modes present in the original game--shovels, snowballs, and mixed  
- Four types of random layer generation  
- 15 unique, themed layer varieties
- Quick and easy setup and use
- Support for 2-8 players  
- Highly customizable  
- Open-source codebase  

## Setup

1. Simply [download](https://github.com/MylesAndMore/tumble/releases) the plugin's JAR file and place it in your server's plugins directory.  

    - *Note: Multiverse is also required for the plugin to run, you may download it [here](https://www.spigotmc.org/resources/multiverse-core.390/).*  

2. Make sure that you have at least two worlds in your world directory! One is for your lobby world, and the other is for your game arena.  

    - If you would like an experience similar to the original game, see [my guide](https://github.com/MylesAndMore/tumble/blob/main/og-guide.md) for using the original worlds.  

3. Start your server. The plugin will generate a couple of warnings, these are normal.
4. Ensure that you have imported your worlds into Multiverse. This can be done with the command ```/mv import <your-world-name> normal```.
5. Now you need to tell Tumble which world is your lobby and which world is your game arena. You can do this with  ```/tumble:link <your-lobby-world> lobby``` and ```/tumble:link <your-game-world> game``` respectively.
6. **VERY IMPORTANT:** The plugin will teleport players to the world spawn point of each world, and generate the game's blocks around the spawn point of the game world. Ensure that your spawn points are clear of any obstructions, and that a 20x20x20 cube is cleared out from the spawn of whatever game world you are using. **Any blocks in this area will be destroyed when the game begins.**
7. You're done! You can now start games with the command ```/tumble:start```.

Scroll down for more options to configure your game.  

## Commands

- ```/tumble:reload```

  - *Description:* Reloads the plugin's configuration.
  - *Usage:* ```/tumble:reload```
  - *Permission:* ```tumble.reload```
- ```/tumble:link```
  - *Description:* Links a world on the server as a lobby or game world.
  - *Usage:* ```/tumble:link <world> (lobby|game)```
  - *Permission:* ```tumble.link```
- ```/tumble:start```
  - *Description:* Force starts a Tumble match (with an optional game type).
  - *Usage:* ```/tumble:start [game-type]```
  - *Permission:* ```tumble.start```
- ```/tumble:winlocation```
  - *Description:* Sets the location to teleport the winning player of a game. Uses the player's location if no arguments are specified.
  - *Usage:* ```/tumble:winlocation [x] [y] [z]```
  - *Permission:* ```tumble.winlocation```
- ```/tumble:autostart```
  - *Description:* Configures the auto start functions of Tumble.
  - *Usage:* ```/tumble:autostart <playerAmount> [enable|disable]```
  - *Permission:* ```tumble.autostart```

## Configuration  

- ```gameMode```  
  - Customize the default game mode of Tumble.  
  - Acceptable options include: shovels, snowballs, mixed  
  - *Default:* ```mixed```  

- ```hideJoinLeaveMessages```  
  - Hides join/leave messages in public chat.  
  - *Default:* ```false```  

- ```permissionMessage```  
  - Customize the message that displays when the player does not have permission to execute a command from this plugin.  

## Issues & Feedback  

Feel free to report any bugs, leave feedback, ask questions, or submit ideas for new features on our [GitHub issues page](https://github.com/MylesAndMore/tumble/issues/new)!  

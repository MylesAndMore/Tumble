## temporary stuff (not ready for merge)
### known issues
- [ ] file writing not fully implemented (just need to call ArenaManager.write() somewhere)
- [ ] issues with join command
- [ ] no config validation

### todo
- [ ] improve inventory saving
- [ ] improve Game.leave() method
- [ ] perhaps replace spectator mode with survival flight
- [ ] clean up and make config managers uniform in structure
- [ ] improve language flow
- [ ] put javadoc comments everywhere

# Tumble  

## Overview  

Tumble is a Spigot/Paper plugin that aims to recreate the Tumble minigame from the bygone era of the Minecraft Legacy Console Editions.  

## What *is* Tumble?

If you've never heard of it, [Tumble](https://minecraft.wiki/w/tmbl) is a twist on the classic Minecraft minigame of spleef, where the objective is to break the blocks under your opponents. But in Tumble, you play on randomly generated layers of blocks, using shovels, snowballs, or both to try and eliminate your opponents.

## Features  

- Choose from three different game modes present in the original game: shovels, snowballs, and mixed  
- Four types of random layer generation  
- 15 unique, themed layer varieties
- Quick and easy setup and use
- Support for 2-8 players  
- Highly customizable  
- Open-source codebase  
- Multiple arenas and concurrent games

## Setup

1. Simply [download](https://github.com/MylesAndMore/tmbl/releases) the plugin's JAR file and place it in your server's plugins directory.
2. Load the worlds for your lobby and arenas.  

    - If you would like an experience similar to the original game, see [my guide](https://github.com/MylesAndMore/tmbl/blob/main/OG-GUIDE.md) for using the original worlds.  

3. Start your server. The plugin will generate a couple of warnings, these are normal.
4. Ensure that you have imported your worlds using a plugin like Multiverse. This can be done with the command ```/mv import <your-world-name> normal```.
5. Now you need to tell Tumble where your lobby is and where your game arena is. You can do this by going to the center positions and running  ```/tmbl-config set lobbyWorld``` and ```/tmbl-config add <arenaname>``` respectively.
6. **VERY IMPORTANT:** The plugin will teleport players to the world and generate the game's blocks around the point you set. Ensure that your spawn points are clear of any obstructions, and that a 20x20x20 cube is cleared out **Any blocks in this area will be destroyed when the game begins.**
7. You're done! You can now start games with the command ```/tmbl start <arenaname> mixed```.

Scroll down for more options to configure your game.  

## Commands / Permissions

| Command                                        | Description                       | Permission          |
|------------------------------------------------|-----------------------------------|---------------------|
| `/tmbl join <arenaName> [gameType]`            | Join a Tumble match.              | `tumble.join`       |
| `/tmbl leave`                                  | Quit a Tumble match.              | `tumble.leave`      |
| `/tmbl forcestart [arenaName]`                 | Force start a Tumble match.       | `tumble.forcestart` |
| `/tmbl forcestop [arenaName]`                  | Force stop a Tumble match.        | `tumble.forcestop`  |
| `/tmbl config <add/set/disable/delete> <data>` | Modify arenas and worlds in game. | `tumble.config`     |
| `/tmbl reload`                                 | Reload the plugin's config.       | `tumble.reload`     |

## Configuration  
Use `/tmbl config` for modifying the config in-game. See available options with the tab auto complete feature

See the comments inside config,yml for manual editing

## Issues & Feedback  

Feel free to report any bugs, leave feedback, ask questions, or submit ideas for new features on our [GitHub issues page](https://github.com/MylesAndMore/tmbl/issues/new)!  

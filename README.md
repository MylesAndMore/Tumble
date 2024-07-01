## temporary stuff (not ready for merge)
### todo
- [ ] separate waiting state code
- [ ] fix join command
- [ ] improve inventory saving
- [ ] improve Game.leave() method
- [ ] put javadoc comments everywhere

# Tumble  

## Overview  

Tumble is a Spigot/Paper plugin that aims to recreate the Tumble minigame from the bygone era of the Minecraft Legacy Console Editions.  

## What *is* Tumble?

If you've never heard of it, [Tumble](https://minecraft.wiki/w/tumble) is a twist on the classic Minecraft minigame of spleef, where the objective is to break the blocks under your opponents. But in Tumble, you play on randomly generated layers of blocks, using shovels, snowballs, or both to try and eliminate your opponents.

## Features  

- Choose from three different game modes present in the original game: shovels, snowballs, and mixed  
- Four types of random layer generation  
- 15 unique, themed layer varieties
- Quick and easy setup and use
- Support for 2-8 players  
- Highly customizable  
- Open-source codebase  
- Multiple arenas and concurrent games
- Heavily configurable

## Setup

1. [Download](https://github.com/MylesAndMore/tmbl/releases) the plugin's JAR file and place it in your server's plugins directory.
2. Place the worlds for your lobby and arenas in your plugins worlds directory.
    - If you would like an experience similar to the original game, see [my guide](OG_GUIDE.md) for using the original worlds.  

3. Start your server.
4. Import your worlds using a plugin like Multiverse. ```/mv import myWorld normal```.
5. Create your first arena `/tmbl create myArena`
   - **Note**: The layers will generate relative to this location. Ensure that the area is clear, 20 blocks in each direction.

7. You're done! You can now join the game ```/tmbl join myArena mixed```.

Scroll down for more options to configure your game.  

## Commands / Permissions

| Command                             | Description                                                                        | Permission              |
|-------------------------------------|------------------------------------------------------------------------------------|-------------------------|
| `/tmbl join <arenaName> [gameType]` | Join a Tumble match. Can infer game type if a game is already started in the arena | `tumble.join`           |
| `/tmbl leave`                       | Quit a Tumble match                                                                | `tumble.leave`          |
| `/tmbl forcestart [arenaName]`      | Force start a Tumble match. Can infer arena if you are in one                      | `tumble.forcestart`     |
| `/tmbl forcestop [arenaName]`       | Force stop a Tumble match. Can infer arena if you are in one                       | `tumble.forcestop`      |
| `/tmbl reload`                      | Reload the plugin's configs.                                                       | `tumble.reload`         |
| `/tmbl create <arenaName>`          | Create a new arena                                                                 | `tumble.create`         |
| `/tmbl remove <arenaName>`          | Remove an arena                                                                    | `tumble.remove`         |
| `/tmbl setGameSpawn <arenaName>`    | Set game spawn to your current position                                            | `tumble.setGameSpawn`   |
| `/tmbl setKillYLevel <arenaName>`   | Set the arena's Y-level to kill players at to current Y coordinate                 | `tumble.setKillYLevel`  |
| `/tmbl setLobby <arenaName>`        | Set the arena's lobby to current location                                          | `tumble.setLobby`       |
| `/tmbl setWaitArea <arenaName>`     | Set the arena's wait area to the current location                                  | `tumble.setWaitArea`    |
| `/tmbl setWinnerLobby <arenaName>`  | Set the arena's lobby to the current location                                      | `tumble.setWinnerLobby` |


## Configuration  
Configuration for this plugin is stored in three files.

### config.yml
Stores common settings

| Option                     | Type            | Default value |
|----------------------------|-----------------|---------------|
| `hide-join-leave-messages` | `boolean`       | `false`       |
| `wait-duration`            | `int` (seconds) | `15`          |


### arenas.yml
Stores data for each arena. You may add and remove arenas as you wish.

Each arena can contain the following locations:

| Location                 | Description                                                                         |
|--------------------------|-------------------------------------------------------------------------------------|
| `game-spawn` **Required* | The location where players will be teleported, and the layers will generate around. |
| `wait-area`              | The location where players will be teleported to before the game begins             |
| `lobby`                  | The location where players will be teleported to after the game                     |
| `winner-lobby`           | The location where the winner will be teleported after the game                     |

Locations are stored using the following format:
```yaml
    location:
      x: 0.5
      y: 100
      z: 0.5
      world: worldName
```

Each arena can also contain the following option:

| Option      | Type   | Description                                                     |
|-------------|--------|-----------------------------------------------------------------|
| `kill-at-y` | double | When a player falls below this Y-level, they will be eliminated |

### language.yml
Most of this plugin's strings are configurable through this file. (Excluding from some console errors)

All plugin chat messages will have the `prefix` prepended to them. 

Colors can be added using alternate color codes:
```
&cRed Text
```


## Issues & Feedback  

Feel free to report any bugs, leave feedback, ask questions, or submit ideas for new features on our [GitHub issues page](https://github.com/MylesAndMore/tmbl/issues/new)!  

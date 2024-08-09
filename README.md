# Tumble  

## Overview  

Tumble is a Spigot/Paper plugin that aims to recreate the Tumble minigame from the bygone era of the Minecraft Legacy Console Editions.  

## What *is* Tumble?

If you've never heard of it, [Tumble](https://minecraft.wiki/w/tumble) is a twist on the classic Minecraft minigame of spleef, where the objective is to break the blocks under your opponents. 
But in Tumble, you play on randomly generated layers of blocks, using shovels, snowballs, or both to try and eliminate your opponents.

## Features  

- Choose from three different game modes present in the original game: shovels, snowballs, and mixed
- Four types of random layer generation
- 15 unique, themed layer varieties
- Quick and easy setup and use
- Support for 2-8 players
- Multiple arenas and concurrent games
- Highly customizable, heavily configurable
- Open-source codebase

## Setup

1. [Download](https://github.com/MylesAndMore/Tumble/releases) the plugin's JAR file and place it in your server's plugins directory.
2. Place the worlds for your lobby and arenas in your server's worlds directory.
    - If you would like an experience similar to the original game, see [my guide](OG_GUIDE.md) for using the original worlds.  

3. Start your server.
4. Import your worlds using a plugin like Multiverse. ```/mv import myWorld normal```.
5. Create your first arena `/tumble create myArena`
6. Set the spawn point of the arena `/tumble setgamespawn myArena`
   - **Note**: The layers will generate relative to this location. Ensure that the area is clear, 20 blocks in each direction.

7. You're done! You can now join the game ```/tumble join myArena mixed```.

Scroll down for more options to configure your game.  

## Commands / Permissions

| Command                               | Description                                                                         | Permission              |
|---------------------------------------|-------------------------------------------------------------------------------------|-------------------------|
| `/tumble join <arenaName> [gameType]` | Join a Tumble match. Can infer game type if a game is already started in the arena. | `tumble.join`           |
| `/tumble leave`                       | Leave a Tumble match.                                                               | `tumble.leave`          |
| `/tumble forcestart [arenaName]`      | Force start a Tumble match. Can infer arena if you are in one.                      | `tumble.forcestart`     |
| `/tumble forcestop [arenaName]`       | Force stop a Tumble match. Can infer arena if you are in one.                       | `tumble.forcestop`      |
| `/tumble reload`                      | Reload the plugin's configuration.                                                  | `tumble.reload`         |
| `/tumble create <arenaName>`          | Create a new arena.                                                                 | `tumble.create`         |
| `/tumble remove <arenaName>`          | Remove an arena.                                                                    | `tumble.remove`         |
| `/tumble setgamespawn <arenaName>`    | Set the arena's game spawn to your current position.                                | `tumble.setgamespawn`   |
| `/tumble setkillylevel <arenaName>`   | Set the arena's Y-level to kill players at to current Y coordinate.                 | `tumble.setkillylevel`  |
| `/tumble setlobby <arenaName>`        | Set the arena's lobby to current location.                                          | `tumble.setlobby`       |
| `/tumble setwaitarea <arenaName>`     | Set the arena's wait area to the current location.                                  | `tumble.setwaitarea`    |
| `/tumble setwinnerlobby <arenaName>`  | Set the arena's lobby to the current location.                                      | `tumble.setwinnerlobby` |

Note that the `/tmbl` command can be used as a shorter alias to `/tumble`.

## Configuration  
Configuration for this plugin is stored in three files:

### settings.yml
Stores general settings.

| Option                     | Type    | Description                                                                    | Default value |
|----------------------------|---------|--------------------------------------------------------------------------------|---------------|
| `hide-join-leave-messages` | Boolean | Hides player join and leave messages in public chat.                           | `false`       |
| `hide-death-messages`      | Boolean | Hides player death messages in the public chat                                 |               |
| `wait-duration`            | Integer | Duration (in seconds) to wait for more players to join a game before starting. | `15`          |

### arenas.yml
Stores data about each arena.
Arenas may be added and removed as you wish, either via the commands detailed above or by editing the `arenas.yml` file directly.

Each arena can contain the following locations:

| Location                 | Description                                                                         |
|--------------------------|-------------------------------------------------------------------------------------|
| `game-spawn` **Required* | The location where players will be teleported, and the layers will generate around. |
| `wait-area`              | The location where players will be teleported to before the game begins.            |
| `lobby`                  | The location where players will be teleported to after the game.                    |
| `winner-lobby`           | The location where the winner will be teleported after the game.                    |

Locations are stored using the following format:
```yaml
location:
   x: 0.5
   y: 100
   z: 0.5
   world: worldName
```
If a location is not specified, players will not be teleported by the plugin.

Each arena can also contain the following option:

| Option      | Type    | Description                                                      |
|-------------|---------|------------------------------------------------------------------|
| `kill-at-y` | Integer | When a player falls below this Y-level, they will be eliminated. |

### language.yml
Most of this plugin's messages are configurable through this file (excluding some console errors).

All plugin chat messages will have the `prefix` prepended to them. 

Colors can be added using alternate color codes; for example, `&cRed Text` will appear red in-game.

### layers.yml
Stores data about the layers that will be generated during gameplay.

Each layer contains a weight and a list of materials (blocks).

| Option                  | Type              | Description                                                                                                                                                                                                                                |
|-------------------------|-------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `weight`                | Integer           | A weight to influence how often the layer is randomly chosen. Default: 1                                                                                                                                                                   |
| `materials` **Required* | List of Materials | The palette of blocks that the layer will be composed of. Use the block names [listed here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html). Optionally, a weight can be added after the block name like so: `STONE 5`. |`


## Issues & Feedback  

Feel free to report any bugs, leave feedback, ask questions, or submit ideas for new features on the Tumble [GitHub issues page](https://github.com/MylesAndMore/tumble/issues/new)!  

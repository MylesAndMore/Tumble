# Tumble  

## Guide for original worlds  

In this guide, I'll go over how to set up the Tumble plugin with the original game worlds from the Legacy Console Editions.

## Steps  

1. Download this plugin and [Multiverse-Core](https://www.spigotmc.org/resources/multiverse-core.390/). Place them in your plugins directory
2. Download the worlds and unzip them into your server's worlds directory.
    - [Lobby](https://www.theminecraftarchitect.com/mini-game-maps/2017-mini-game-lobby) (Rename folder to 'lobby' after unzipping)
    - [Normal Arena](https://publicfiles.sowgro.net/console-minigame-maps/java/tumble/)
    - [Festive Arena](https://publicfiles.sowgro.net/console-minigame-maps/java/tumble/)
    - [Halloween Arena](https://publicfiles.sowgro.net/console-minigame-maps/java/tumble/)
    - [Birthday Arena](https://publicfiles.sowgro.net/console-minigame-maps/java/tumble/)

    Tip: set a specific directory to store your worlds in with the `world-container` setting in `bukkit.yml`

3. Set `level-name` in server.properties to `lobby`
4. Take note of the names of the world folders, we will need this in a moment.
5. Start and join your server.
6. Import your arena worlds. This can be done with the multiverse command `/mv import <your-world-name> normal`

7. Paste the arena config below into `plugins/tumble/arenas.yml`:
   ```yaml
   arenas:
     basic:
       kill-at-y: 24
       game-spawn:
         x: 0.5
         y: 60.0
         z: 0.5
         world: basic
       lobby:
         x: -341.5
         y: 58
         z: -340.5
         world: lobby
       winner-lobby:
         x: -362.5
         y: 76
         z: -340.5
         world: lobby
     birthday:
       kill-at-y: 27
       game-spawn:
         x: 0.5
         y: 60
         z: 0.5
         world: birthday
       lobby:
         x: -341.5
         y: 58
         z: -340.5
         world: lobby
       winner-lobby:
         x: -362.5
         y: 76
         z: -340.5
         world: lobby
     festive:
       kill-at-y: 20
       game-spawn:
         x: 0.5
         y: 60
         z: 0.5
         world: festive
       lobby:
         x: -341.5
         y: 58
         z: -340.5
         world: lobby
       winner-lobby:
         x: -362.5
         y: 76
         z: -340.5
         world: lobby
     halloween:
       kill-at-y: 23
       game-spawn:
         x: 0.5
         y: 60
         z: -0.5
         world: halloween
       lobby:
         x: -341.5
         y: 58
         z: -340.5
         world: lobby
       winner-lobby:
         x: -362.5
         y: 76
         z: -340.5
         world: lobby
   ```
8. Reload the plugin with `/tumble reload`.

9. Join the game using `/tumble join basic mixed`
(swap the arena and game type for whichever one you want to play).

You're done! Happy playing!

## Recommended plugins

These plugins, while not required, can help to provide a more thorough minigame experience.

- [WorldGuard](https://dev.bukkit.org/projects/worldguard) and [CyberWorldReset](https://www.spigotmc.org/resources/cyberworldreset-standard-%E2%9C%A8-regenerate-worlds-scheduled-resets-lag-optimized%E3%80%8C1-8-1-19%E3%80%8D.96834/)
   - Protect players from breaking blocks in the lobby and reset any redstone they activated.

- [ViaVersion](https://www.spigotmc.org/resources/viaversion.19254/) and [ViaBackwards](https://www.spigotmc.org/resources/viabackwards.27448/)
   - Allow older and newer players to connect to your server.

- [Geyser](https://geysermc.org/download#geyser) and [Floodgate](https://geysermc.org/download#floodgate)
   - Allow Bedrock clients to connect to your server.

- [ProtectEnvironment](https://www.spigotmc.org/resources/protectenvironment.82736/)
   - Stop water and lava flow (useful for Halloween map).

# Tumble  

## Guide for original worlds  

In this guide, I'll go over how to set up the Tumble plugin with the original game worlds from the Legacy Console Editions.

## Steps  

1. Download the worlds and unzip them into your server's worlds directory.
    - [Lobby]()
    - [Normal Arena]()
    - [Festive Arena]()
    - [Halloween Arena]()
    - [Birthday Arena]()

2. Take note of the names of the world folders, we will need this in a moment.
3. Start and join your server.
4. Import your arena worlds. This can be done with the multiverse command `/mv import <your-world-name> normal`

5. Paste the arena config below into `plugins/tumble/arenas.yml`:
   ```yaml
   arenas:
     basic:
       kill-at-y: 24
       game-spawn:
         x: 0.5
         y: 60.0
         z: 0.5
         world: tmbl-basic
       lobby:
         x: -341.5
         y: 58
         z: -340.5
         world: lobby
       winner-lobby:
         x: -362.5
         y: 76
         Z: -340.5
         world: lobby
     birthday:
       kill-at-y: 27
       game-spawn:
         x: 0.5
         y: 60
         z: 0.5
         world: tmbl-birthday
       lobby:
         x: -341.5
         y: 58
         z: -340.5
         world: lobby
       winner-lobby:
         x: -362.5
         y: 76
         Z: -340.5
         world: lobby
     festive:
       kill-at-y: 20
       game-spawn:
         x: 0.5
         y: 60
         z: 0.5
         world: tmbl-festive
       lobby:
         x: -341.5
         y: 58
         z: -340.5
         world: lobby
       winner-lobby:
         x: -362.5
         y: 76
         Z: -340.5
         world: lobby
     halloween:
       kill-at-y: 23
       game-spawn:
         x: 0.5
         y: 60
         z: -0.5
         world: tmbl-halloween
       lobby:
         x: -341.5
         y: 58
         z: -340.5
         world: lobby
       winner-lobby:
         x: -362.5
         y: 76
         Z: -340.5
         world: lobby
   ```
6. Reload the plugin with `/tmbl reload`.

7. Join the game by using `/tmbl join basic Mixed`
(swap the arena and game type for whichever one you want to play).

You're done!

## Suggestions

With this, the setup for this plugin is complete, but there still may be more for you to do. 
There are other plugins out there to fine-tune your experience even more. Plugins like [WorldGuard](https://dev.bukkit.org/projects/worldguard) and [CyberWorldReset](https://www.spigotmc.org/resources/cyberworldreset-standard-%E2%9C%A8-regenerate-worlds-scheduled-resets-lag-optimized%E3%80%8C1-8-1-19%E3%80%8D.96834/) can protect players from breaking blocks in the lobby and reset any redstone they activated, while others like [ViaVersion](https://www.spigotmc.org/resources/viaversion.19254/) can allow you to play Tumble from your favorite Minecraft version (yes, you, 1.8.9 players).  

Whatever you choose, the experience is up to you.

Happy playing!

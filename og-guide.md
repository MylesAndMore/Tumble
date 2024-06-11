# Tumble  

## Guide for original worlds  

In this guide, I'll go over how to set up the Tumble plugin with the original game worlds from the Legacy Console Editions.

## Steps  

1. Download the worlds and unzip them into your server's main/root directory. **Ensure you download the Java and not the Bedrock versions**!  
A huge thanks to *Catmanjoe* for porting these worlds! This game would not be the same without you!

    - [Lobby (2017)](https://www.theminecraftarchitect.com/mini-game-maps/2017-mini-game-lobby)  
    - [Lobby (2016)](https://www.theminecraftarchitect.com/mini-game-maps/2016-mini-game-lobby)  
    - [Normal Arena](https://www.planetminecraft.com/project/minecraft-classic-tumble-mode-arena-download-java/)  
    - [Festive Arena (Download coming soon)]()
    - [Halloween Arena (Download coming soon)]()
    - [Birthday Arena (Download coming soon)]()
2. Take note of the names of the world folders (you may rename them), we will need this in a moment.
3. Start and join your server.
4. Set your lobby spawn by going to the location and running `/tumble-config set lobbySpawn`. In the 2017 console lobby this is at `-341.5 58 -340.5`
5. If you want to have a separate lobby spawn for the winner, set it with `/tumble-config set winnerLobbySpawn`. In the 2017 console lobby this is at `-362.5 76 -340.5`
6. Import your arena world into Multiverse. You can do this by running the command `/mv import <your-world-name> normal`
7. Teleport to the arena world. Use `/mvtp <your-game world>`.
8. Now you can create the arena! Do this by going to the spawn location and running `/tumble-config add <arena-name>`. In the console arena this is at `0 60 0`
9. Repeat steps 4, 5 and 6 for each arena
10. Join the game by using `/tumble-join <arena-name> Mixed`(or whichever game mode you want).

You're done!

## Continuation  

With this, the setup for this plugin is complete, but there still may be more for you to do. There are other plugins out there to fine-tune your experience even more. Plugins like [WorldGuard](https://dev.bukkit.org/projects/worldguard) and [CyberWorldReset](https://www.spigotmc.org/resources/cyberworldreset-standard-%E2%9C%A8-regenerate-worlds-scheduled-resets-lag-optimized%E3%80%8C1-8-1-19%E3%80%8D.96834/) can protect players from breaking blocks in the lobby and reset any redstone they activated, while others like [ViaVersion](https://www.spigotmc.org/resources/viaversion.19254/) can allow you to play Tumble from your favorite Minecraft version (yes, you, 1.8.9 players).  

Whatever you choose, the experience is up to you.

Happy playing!

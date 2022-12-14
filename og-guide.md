# Tumble  

## Guide for original worlds  

In this guide, I'll go over how to set up the Tumble plugin with the original game worlds from the Legacy Console Editions.

## Steps  

1. Download the worlds and unzip them into your server's main/root directory. **Ensure you download the Java and not the Bedrock versions**!  
A huge thanks to *Catmanjoe* for porting these worlds! This game would not be the same without you!

    - [Lobby (new edition)](https://mcpedl.com/mc-2017-new-mini-games-lobby-download-map/)  
    - [Lobby (old edition)](https://mcpedl.com/minecraft-2016-classic-mini-games-lobby-map/)  
    - [Arena](https://www.planetminecraft.com/project/minecraft-2016-classic-mini-games-lobby-download-bedrock-edition/)  
2. Take note of the names of the world folders (you may rename them), we will need this in a moment.
3. Start and join your server.
4. Import both worlds into Multiverse. You can do this by running the command ```/mv import <your-world-name> normal``` for both worlds.
5. Now you can link each world! Do this with  ```/tumble:link <your-lobby-world> lobby``` and ```/tumble:link <your-game-world> game``` respectively.  
6. Teleport to your new lobby world by using ```/mvtp <your-lobby-world>```.  
7. Set the correct spawn location in this world using ```/setworlspawn```. For me, the correct coordinates were ```/setworldspawn place holder L```, but your results may vary.  
8. Set the location that the winner will be teleported using ```tumble:winloc```. Again, the correct coordinates were ```tumble:winloc wait no u``` in my case.  
9. Now, teleport to the game world. Use ```/mvtp <your-game world>```.  
10. Set the correct spawn point of this world. This is also where the game will generate its blocks. My preferred position is ```/setworldspawn 0 60 0```, but you may place the spawn whereever you like.  

You're done!

## Continuation  

With this, the setup for this plugin is complete, but there still may be more for you to do. There are other plugins out there to fine-tune your experience even more. Plugins like [WorldGuard](https://dev.bukkit.org/projects/worldguard) and [CyberWorldReset](https://www.spigotmc.org/resources/cyberworldreset-standard-%E2%9C%A8-regenerate-worlds-scheduled-resets-lag-optimized%E3%80%8C1-8-1-19%E3%80%8D.96834/) can protect players from breaking blocks in the lobby and reset any redstone they activated, while others like [ViaVersion](https://www.spigotmc.org/resources/viaversion.19254/) can allow you to play Tumble from your favorite Minecraft version (yes, you, 1.8.9 players).  

Whatever you choose, the experience is up to you.

Happy playing!

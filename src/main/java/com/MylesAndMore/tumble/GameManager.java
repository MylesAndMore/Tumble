package com.MylesAndMore.tumble;

import com.MylesAndMore.tumble.api.Generator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class GameManager {
    public static boolean createGame(String gameType) {
        if (Objects.equals(gameType, "shovels")) {
            // Generate layers
            Location layer = Bukkit.getWorld(TumbleManager.getGameWorld()).getSpawnLocation();
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 17, 1, Material.SNOW_BLOCK);
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 13, 1, Material.GRASS_BLOCK);
            layer.setY(layer.getY() - 1);
            Generator.generateLayer(layer, 4, 1, Material.PODZOL);
            layer.setY(layer.getY() + 1);
            Generator.generateLayer(layer, 4, 2, Material.GRASS);
            // Give players diamond shovels
            giveItems(new ItemStack(Material.DIAMOND_SHOVEL));
            // Pass on the game type

        }
        else if (Objects.equals(gameType, "snowballs")) {
            // Generate three layers
            // (Will make this customizable in later versions)
            // Remember, the snowballs don't interact with players!

            // Give players infinite snowballs

            // Pass on the game type
        }
        else if (Objects.equals(gameType, "mixed")) {
            // Randomly select rounds from above

            // Pass on the game type
        }
        else {
            return false;
        }
        return true;
    }

    public static void giveItems(ItemStack itemStack) {
        for (List<Player> playersWithoutItem = TumbleManager.getPlayersInLobby(); playersWithoutItem.size() > 0; playersWithoutItem.remove(0)) {
            // Get a singular player from the player list
            Player playerWithoutItem = playersWithoutItem.get(0);
            // Give that player the specified item
            playerWithoutItem.getInventory().addItem(itemStack);
        }
    }
}

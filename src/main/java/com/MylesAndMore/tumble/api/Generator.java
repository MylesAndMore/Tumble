package com.MylesAndMore.tumble.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
    /**
     * Generates a layer (bascally just a cylinder) as best as it can w/ blocks
     * 
     * @return A list of Blocks containing all the blocks it just changed
     * 
     * @param center The center of the layer (Location)
     * @param radius The whole number radius of the circle
     * @param height The whole number height of the circle (1 for a flat layer)
     * @param material The Material to use for generation
     */
    public static List<Block> generateLayer(Location center, int radius, int height, Material material) {
        int Cx = center.getBlockX();
        int Cy = center.getBlockY();
        int Cz = center.getBlockZ();
        World world = center.getWorld();
        List<Block> blocks = new ArrayList<>();

        int rSq = radius * radius;

        for (int y = Cy; y < Cy + height; y++) {
            for (int x = Cx - radius; x <= Cx + radius; x++) {
                for (int z = Cz - radius; z <= Cz + radius; z++) {
                    if ((Cx - x) * (Cx - x) + (Cz - z) * (Cz - z) <= rSq) {
                        world.getBlockAt(x, y, z).setType(material);
                        blocks.add(world.getBlockAt(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * Generates clumps in a pre-generated layer.
     * @param blocks A list of block Locations that this method is allowed to edit
     * @param materials A list of Materials for the generator to randomly choose from.
     * Keep in mind that not all Materials may be used, the amount used depends on the size of the layer.
     * More Materials = more randomization
     */
    public static void generateClumps(List<Block> blocks, List<Material> materials) {
        // Define random class
        Random random = new Random();
        // This for loop will run until there are no blocks left to change
        for (Block aBlock : blocks) {
            // Get a random Material from the provided materials list
            Material randomMaterial = materials.get(random.nextInt(materials.size()));
            aBlock.setType(randomMaterial);
            // Get the blocks around that and change it to that same material
            // ...
        }
    }

    /**
     * Generates a cubiod (literally just a ripoff fill command)
     * @param firstPos The first Location to fill (first three coords in a fill command)
     * @param secondPos The second Location to fill to (second three coords)
     * @param material The Material to fill
     */
    public static void generateCuboid(Location firstPos, Location secondPos, Material material) {
        World world = firstPos.getWorld();
        int fX = firstPos.getBlockX();
        int fY = firstPos.getBlockY();
        int fZ = firstPos.getBlockZ();
        int sX = secondPos.getBlockX();
        int sY = secondPos.getBlockY();
        int sZ = secondPos.getBlockZ();

        for (int x = fX; x <= sX; x++) {
            for (int y = fY; y <= sY; y++) {
                for (int z = fZ; z <= sZ; z++) {
                    world.getBlockAt(x, y, z).setType(material);
                }
            }
        }
    }
}

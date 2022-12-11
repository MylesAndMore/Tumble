package com.MylesAndMore.tumble.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.Collections;
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
     * Generates a cubiod (literally just a ripoff fill command)
     * @param firstPos The first Location to fill (first three coords in a fill command)
     * @param secondPos The second Location to fill to (second three coords)
     * @param material The Material to fill
     */
    public static List<Block> generateCuboid(Location firstPos, Location secondPos, Material material) {
        World world = firstPos.getWorld();
        List<Block> blocks = new ArrayList<>();
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
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    /**
     * Generates clumps in a pre-generated layer.
     * @param blockList A list of block Locations that this method is allowed to edit
     * @param materialList A list of Materials for the generator to randomly choose from.
     * Keep in mind that not all Materials may be used, the amount used depends on the size of the layer.
     * More Materials = more randomization
     */
    public static void generateClumps(List<Block> blockList, List<Material> materialList) {
        // Define random class
        Random random = new Random();
        // Define new blocks list so we can manipulate it
        List<Block> blocks = new ArrayList<>(blockList);
        // Define new shuffled Materials list
        List<Material> materials = new ArrayList<>(materialList);
        Collections.shuffle(materials);
        // This loop will run until there are no blocks left to change
        while (blocks.size() > 0) {
            // Get a random Material from the provided materials list
            Material randomMaterial = materials.get(random.nextInt(materials.size()));
            // Gets the first Block from the list, to modify
            Block aBlock = blocks.get(0);
            // Modifies the block
            aBlock.setType(randomMaterial);
            // Get the blocks around that and change it to that same material
            if (blocks.contains(aBlock.getRelative(BlockFace.NORTH))) {
                aBlock.getRelative(BlockFace.NORTH).setType(randomMaterial);
                blocks.remove(aBlock.getRelative(BlockFace.NORTH));
            }
            if (blocks.contains(aBlock.getRelative(BlockFace.SOUTH))) {
                aBlock.getRelative(BlockFace.SOUTH).setType(randomMaterial);
                blocks.remove(aBlock.getRelative(BlockFace.SOUTH));
            }
            if (blocks.contains(aBlock.getRelative(BlockFace.EAST))) {
                aBlock.getRelative(BlockFace.EAST).setType(randomMaterial);
                blocks.remove(aBlock.getRelative(BlockFace.EAST));
            }
            if (blocks.contains(aBlock.getRelative(BlockFace.WEST))) {
                aBlock.getRelative(BlockFace.WEST).setType(randomMaterial);
                blocks.remove(aBlock.getRelative(BlockFace.WEST));
            }
            blocks.remove(aBlock);
        }
    }
}

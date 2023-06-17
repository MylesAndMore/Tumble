package com.MylesAndMore.Tumble.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;

/**
 * Holds the methods that generate blocks in-game such as cylinders, cuboids, and block clumps.
 */
public class Generator {
    /**
     * Generates a layer (basically just a cylinder) as good as possible with blocks
     * @param center The center of the layer (Location)
     * @param radius The whole number radius of the circle
     * @param height The whole number height of the circle (1 for a flat layer)
     * @param material The Material to use for generation
     *
     * @return A list of Blocks containing all the blocks it just changed
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
                        Objects.requireNonNull(world).getBlockAt(x, y, z).setType(material);
                        blocks.add(world.getBlockAt(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * Generates a cuboid (literally just a ripoff fill command)
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
                    Objects.requireNonNull(world).getBlockAt(x, y, z).setType(material);
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
        Random random = new Random();
        // Make new lists so we can manipulate them
        List<Block> blocks = new ArrayList<>(blockList);
        List<Material> materials = new ArrayList<>(materialList);
        Collections.shuffle(materials);
        while (blocks.size() > 0) {
            Material randomMaterial = materials.get(random.nextInt(materials.size()));
            Block aBlock = blocks.get(0);
            aBlock.setType(randomMaterial);
            // Get the blocks around that and change it to that same material (this is the basis of "clumps")
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

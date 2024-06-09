package com.MylesAndMore.Tumble.game;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;

/**
 * Holds the methods that generate blocks in-game such as cylinders, cuboids, and block clumps.
 */
public class Generator {

    /**
     * Generates layers for a round of type shovels
     * @param layer Location where the layers should start
     */
    public static void generateLayersShovels(Location layer) {
        Random random = new Random();
        Layers layers = new Layers();

        layer.setY(layer.getY() - 1);
        // Choose a random type of generation; a circular layer, a square layer, or a multi-tiered layer of either variety
        if (random.nextInt(4) == 0) {
            // Circular layer
            Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.SNOW_BLOCK), layers.getSafeMaterialList());
        }
        else if (random.nextInt(4) == 1) {
            // Square layer
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.SNOW_BLOCK), layers.getSafeMaterialList());
        }
        else if (random.nextInt(4) == 2) {
            // Multi-tiered circle
            Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.SNOW_BLOCK), layers.getSafeMaterialList());
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateLayer(layer, 13, 1, Material.GRASS_BLOCK), layers.getMaterialList());
            Generator.generateLayer(layer, 4, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateLayer(layer, 4, 1, Material.PODZOL), layers.getMaterialList());
        }
        else {
            // Multi-tiered square
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.SNOW_BLOCK), layers.getSafeMaterialList());
            Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.GRASS_BLOCK), layers.getMaterialList());
            Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.PODZOL), layers.getMaterialList());
        }
    }

    /**
     * Generates layers for round of type snowballs
     * @param layer Location where the layers should start
     */
    public static void generateLayersSnowballs(Location layer) {
        Random random = new Random();
        Layers layers = new Layers();

        layer.setY(layer.getY() - 1);
        // Similar generation to shovels, except there are three layers
        if (random.nextInt(4) == 0) {
            // Circular layer
            Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getSafeMaterialList());
            layer.setY(layer.getY() - 6);
            Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getMaterialList());
            layer.setY(layer.getY() - 6);
            Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getMaterialList());
        }
        else if (random.nextInt(4) == 1) {
            // Square layer
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getSafeMaterialList());
            layer.setY(layer.getY() - 6);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getMaterialList());
            layer.setY(layer.getY() - 6);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getMaterialList());
        }
        else if (random.nextInt(4) == 2) {
            // Multi-tiered circle
            Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getSafeMaterialList());
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateLayer(layer, 13, 1, Material.GRANITE), layers.getMaterialList());
            Generator.generateLayer(layer, 4, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
            layer.setY(layer.getY() - 6);

            Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getSafeMaterialList());
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateLayer(layer, 13, 1, Material.GRANITE), layers.getMaterialList());
            Generator.generateLayer(layer, 4, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
            layer.setY(layer.getY() - 6);

            Generator.generateClumps(Generator.generateLayer(layer, 17, 1, Material.STONE), layers.getSafeMaterialList());
            Generator.generateLayer(layer, 13, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateLayer(layer, 13, 1, Material.GRANITE), layers.getMaterialList());
            Generator.generateLayer(layer, 4, 1, Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateLayer(layer, 4, 1, Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
        }
        else {
            // Multi-tiered square
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getSafeMaterialList());
            Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.GRANITE), layers.getMaterialList());
            Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
            layer.setY(layer.getY() - 6);

            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getSafeMaterialList());
            Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.GRANITE), layers.getMaterialList());
            Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
            layer.setY(layer.getY() - 6);

            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 17, layer.getY(), layer.getZ() - 17), new Location(layer.getWorld(), layer.getX() + 17, layer.getY(), layer.getZ() + 17), Material.STONE), layers.getSafeMaterialList());
            Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 13, layer.getY(), layer.getZ() - 13), new Location(layer.getWorld(), layer.getX() + 13, layer.getY(), layer.getZ() + 13), Material.GRANITE), layers.getMaterialList());
            Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.AIR);
            layer.setY(layer.getY() - 1);
            Generator.generateClumps(Generator.generateCuboid(new Location(layer.getWorld(), layer.getX() - 7, layer.getY(), layer.getZ() - 7), new Location(layer.getWorld(), layer.getX() + 7, layer.getY(), layer.getZ() + 7), Material.LIME_GLAZED_TERRACOTTA), layers.getMaterialList());
        }
    }

    /**
     * Generates a layer (basically just a cylinder) as good as possible with blocks
     * @param center The center of the layer (Location)
     * @param radius The whole number radius of the circle
     * @param height The whole number height of the circle (1 for a flat layer)
     * @param material The Material to use for generation
     *
     * @return A list of Blocks containing all the blocks it just changed
     */
    private static List<Block> generateLayer(Location center, int radius, int height, Material material) {
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
    private static void generateClumps(List<Block> blockList, List<Material> materialList) {
        Random random = new Random();
        // Make new lists so we can manipulate them
        List<Block> blocks = new ArrayList<>(blockList);
        List<Material> materials = new ArrayList<>(materialList);
        Collections.shuffle(materials);
        while (!blocks.isEmpty()) {
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

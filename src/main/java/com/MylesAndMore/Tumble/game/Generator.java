package com.MylesAndMore.Tumble.game;

import com.MylesAndMore.Tumble.config.LayerManager;
import com.MylesAndMore.Tumble.plugin.GameType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;

/**
 * The Generator can generate basic shapes and layers for the game
 */
public class Generator {

    private static final int CIRCLE_RADIUS = 17;
    private static final int SQUARE_RADIUS = 17;
    private static final int MULTI_TIER_RADIUS1 = 17;
    private static final int MULTI_TIER_RADIUS2 = 13;
    private static final int MULTI_TIER_RADIUS3_CIRCULAR = 4;
    private static final int MULTI_TIER_RADIUS3_SQUARE = 7;
    private static final int LAYER_DROP_HEIGHT = 6; // How far down the next layer should be generated in multi-layer generation

    /**
     * Generates layers for a round
     * @param center The center of the layers
     * @param type The type of the round (either shovels or snowballs)
     */
    public static void generateLayers(Location center, GameType type) {
        if (type == GameType.MIXED) { return; } // Cannot infer generation type from mixed
        Random random = new Random();
        Location layer = center.clone();
        // The only difference between shovel and snowball generation is the amount of layers
        int numLayers = type == GameType.SNOWBALLS ? 3 : 1;
        // Move down one block before generating
        layer.setY(layer.getY() - 1);
        switch (random.nextInt(4)) {
            case 0 -> generateCircularLayers(layer, new int[]{CIRCLE_RADIUS}, numLayers); // Single circular layer
            case 1 -> generateSquareLayers(layer, new int[]{SQUARE_RADIUS}, numLayers); // Single square layer
            case 2 -> generateCircularLayers(layer, new int[]{MULTI_TIER_RADIUS1, MULTI_TIER_RADIUS2, MULTI_TIER_RADIUS3_CIRCULAR}, numLayers); // Multi-tiered circular layer
            case 3 -> generateSquareLayers(layer, new int[]{MULTI_TIER_RADIUS1, MULTI_TIER_RADIUS2, MULTI_TIER_RADIUS3_SQUARE}, numLayers); // Multi-tiered square layer
        }
    }

    /**
     * Generates a cylinder
     * @param center The center of the layer (Location)
     * @param radius The radius of the layer
     * @param height The height of the layer (1 for a flat layer)
     * @param material The Material to use for generation
     * @return A list containing all changed blocks
     */
    public static List<Block> generateCylinder(Location center, int radius, int height, Material material) {
        int Cx = center.getBlockX();
        int Cy = center.getBlockY();
        int Cz = center.getBlockZ();
        int rSq = radius * radius;
        World world = center.getWorld();
        List<Block> blocks = new ArrayList<>();

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
     * Generates a cuboid
     * @param firstPos The first Location to fill from (first three coords in a fill command)
     * @param secondPos The second Location to fill to (second three coords)
     * @param material The Material to fill
     * @return A list containing all changed blocks
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
     * Generates clumps in a pre-generated layer
     * @param blockList A list of Blocks that this method is allowed to edit
     * @param materialList A list of Materials for the generator to randomly choose from.
     * Keep in mind that not all Materials may be used, the amount used depends on the size of the layer.
     * More Materials = more randomization
     */
    private static void generateClumps(List<Block> blockList, List<Material> materialList) {
        Random random = new Random();
        List<Block> blocks = new ArrayList<>(blockList);
        List<Material> materials = new ArrayList<>(materialList);
        Collections.shuffle(materials);

        while (!blocks.isEmpty()) {
            Material randomMaterial = materials.get(random.nextInt(materials.size()));
            Block block = blocks.get(random.nextInt(blocks.size()));
            block.setType(randomMaterial);
            List<Block> modifiedBlocks = setRelativeBlocks(blocks, block);
            blocks.removeAll(modifiedBlocks);
            // There is a 50% (then 25%, 12.5%, ...) chance to continue modifying blocks aka growing the clump
            double probability = 0.5;
            while (!modifiedBlocks.isEmpty() && random.nextDouble() < probability) {
                Block nextBlock = modifiedBlocks.get(random.nextInt(modifiedBlocks.size()));
                nextBlock.setType(randomMaterial);
                modifiedBlocks = setRelativeBlocks(blocks, nextBlock);
                blocks.removeAll(modifiedBlocks);
                probability /= 2;
            }
        }
    }

    /**
     * Sets all Blocks adjacent to `block` in `blocks` to the same Material as `block`
     * @param blocks The list of blocks to modify
     * @param block The reference block
     * @return A list of all modified blocks, including `block`
     */
    private static List<Block> setRelativeBlocks(List<Block> blocks, Block block) {
        List<Block> modifiedBlocks = new ArrayList<>();
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (BlockFace face : faces) {
            Block relativeBlock = block.getRelative(face);
            if (blocks.contains(relativeBlock)) {
                relativeBlock.setType(block.getBlockData().getMaterial());
                modifiedBlocks.add(relativeBlock);
            }
        }
        modifiedBlocks.add(block);
        return modifiedBlocks;
    }

    /**
     * Generates a (optionally multi-tiered) circular layer
     * @param center The center of the layer
     * @param radii The radii of the layer(s)
     */
    private static void generateCircularLayer(Location center, int[] radii) {
        for (int i = 0; i < radii.length; i++) {
            // First generate the basic shape (in this case a circle),
            // then fill that shape with clumps from a randomly selected Material list
            generateClumps(generateCylinder(center, radii[i], 1, Material.AIR), LayerManager.getRandomLayer());
            if (i < radii.length - 1) {
                // Another layer will be generated below the current one
                // Set that area to AIR on the current level...
                generateCylinder(center, radii[i + 1], 1, Material.AIR);
                // ...then move down one block to prepare for the next layer
                center.setY(center.getY() - 1);
            }
        }
    }

    /**
     * Generates a (optionally multi-tiered) square layer
     * @param center The center of the layer
     * @param radii The radii of the layer(s)
     */
    private static void generateSquareLayer(Location center, int[] radii) {
        for (int i = 0; i < radii.length; i++) {
            // Square generation is similar to circle generation, just with a bit more math
            Location pos1 = new Location(center.getWorld(), center.getX() - radii[i], center.getY(), center.getZ() - radii[i]);
            Location pos2 = new Location(center.getWorld(), center.getX() + radii[i], center.getY(), center.getZ() + radii[i]);
            generateClumps(generateCuboid(pos1, pos2, Material.AIR), LayerManager.getRandomLayer());
            if (i < radii.length - 1) {
                pos1 = new Location(center.getWorld(), center.getX() - radii[i + 1], center.getY(), center.getZ() - radii[i + 1]);
                pos2 = new Location(center.getWorld(), center.getX() + radii[i + 1], center.getY(), center.getZ() + radii[i + 1]);
                generateCuboid(pos1, pos2, Material.AIR);
                center.setY(center.getY() - 1);
            }
        }
    }

    /**
     * Generates multiple circular layer(s), each seperated by `LAYER_DROP_HEIGHT`
     * @param center The center of the layer(s)
     * @param radii The radii of the layer(s)
     * @param layers The amount of layers to generate
     */
    private static void generateCircularLayers(Location center, int[] radii, int layers) {
        for (int i = 0; i < layers; i++) {
            generateCircularLayer(center, radii);
            center.setY(center.getY() - Generator.LAYER_DROP_HEIGHT);
        }
    }

    /**
     * Generates multiple square layer(s), each seperated by `LAYER_DROP_HEIGHT`
     * @param center The center of the layer(s)
     * @param radii The radii of the layer(s)
     * @param layers The amount of layers to generate
     */
    private static void generateSquareLayers(Location center, int[] radii, int layers) {
        for (int i = 0; i < layers; i++) {
            generateSquareLayer(center, radii);
            center.setY(center.getY() - Generator.LAYER_DROP_HEIGHT);
        }
    }
}

package com.MylesAndMore.Tumble.game;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stores the different types of layers that can be generated
 */
public class Layers {

    public Layers() {
        List<Material> gen2 = new ArrayList<>() {{
            add(Material.PINK_TERRACOTTA);
            add(Material.PURPLE_TERRACOTTA);
            add(Material.GRAY_TERRACOTTA);
            add(Material.BLUE_TERRACOTTA);
            add(Material.LIGHT_BLUE_TERRACOTTA);
            add(Material.WHITE_TERRACOTTA);
            add(Material.BROWN_TERRACOTTA);
            add(Material.GREEN_TERRACOTTA);
            add(Material.YELLOW_TERRACOTTA);
            add(Material.PINK_TERRACOTTA);
            add(Material.PURPLE_TERRACOTTA);
            add(Material.GRAY_TERRACOTTA);
            add(Material.BLUE_TERRACOTTA);
            add(Material.LIGHT_BLUE_TERRACOTTA);
            add(Material.WHITE_TERRACOTTA);
            add(Material.BROWN_TERRACOTTA);
            add(Material.GREEN_TERRACOTTA);
            add(Material.YELLOW_TERRACOTTA);
            add(Material.WHITE_STAINED_GLASS);
            add(Material.HONEYCOMB_BLOCK);
            add(Material.HONEYCOMB_BLOCK);
        }};
        List<Material> gen4 = new ArrayList<>() {{
            add(Material.DIAMOND_BLOCK);
            add(Material.GOLD_BLOCK);
            add(Material.REDSTONE_BLOCK);
            add(Material.REDSTONE_BLOCK);
            add(Material.LAPIS_BLOCK);
            add(Material.LAPIS_BLOCK);
            add(Material.IRON_BLOCK);
            add(Material.COAL_BLOCK);
            add(Material.IRON_BLOCK);
            add(Material.COAL_BLOCK);
            add(Material.IRON_BLOCK);
            add(Material.COAL_BLOCK);
            add(Material.COAL_BLOCK);
        }};
        List<Material> gen5 = new ArrayList<>() {{
            add(Material.WHITE_TERRACOTTA);
            add(Material.BLUE_ICE);
            add(Material.SOUL_SAND);
            add(Material.STONE_SLAB);
            add(Material.WHITE_TERRACOTTA);
            add(Material.BLUE_ICE);
            add(Material.SOUL_SAND);
            add(Material.STONE_SLAB);
            add(Material.WHITE_TERRACOTTA);
            add(Material.BLUE_ICE);
            add(Material.SOUL_SAND);
            add(Material.STONE_SLAB);
            add(Material.GLOWSTONE);
            add(Material.GLOWSTONE);
            add(Material.HONEY_BLOCK);
            add(Material.SLIME_BLOCK);
        }};
        List<Material> gen7 = new ArrayList<>() {{
            add(Material.END_STONE);
            add(Material.END_STONE_BRICKS);
            add(Material.END_STONE);
            add(Material.END_STONE_BRICKS);
            add(Material.END_STONE);
            add(Material.END_STONE_BRICKS);
            add(Material.END_STONE);
            add(Material.END_STONE_BRICKS);
            add(Material.OBSIDIAN);
            add(Material.PURPUR_BLOCK);
            add(Material.PURPUR_PILLAR);
            add(Material.COBBLESTONE);
        }};
        List<Material> gen9 = new ArrayList<>() {{
            add(Material.PRISMARINE);
            add(Material.DARK_PRISMARINE);
            add(Material.BLUE_STAINED_GLASS);
            add(Material.WET_SPONGE);
            add(Material.PRISMARINE_BRICKS);
            add(Material.PRISMARINE_BRICK_SLAB);
            add(Material.DARK_PRISMARINE);
            add(Material.SEA_LANTERN);
            add(Material.TUBE_CORAL_BLOCK);
            add(Material.BRAIN_CORAL_BLOCK);
            add(Material.BUBBLE_CORAL_BLOCK);
        }};
        List<Material> gen10 = new ArrayList<>() {{
            add(Material.OAK_LOG);
            add(Material.SPRUCE_LOG);
            add(Material.ACACIA_LOG);
            add(Material.STRIPPED_OAK_LOG);
            add(Material.STRIPPED_SPRUCE_LOG);
            add(Material.STRIPPED_ACACIA_LOG);
            add(Material.OAK_WOOD);
            add(Material.SPRUCE_WOOD);
            add(Material.ACACIA_WOOD);
            add(Material.OAK_LEAVES);
            add(Material.SPRUCE_LEAVES);
            add(Material.ACACIA_LEAVES);
            add(Material.OAK_LEAVES);
            add(Material.SPRUCE_LEAVES);
            add(Material.ACACIA_LEAVES);
        }};
        List<Material> gen1 = new ArrayList<>() {{
            add(Material.YELLOW_GLAZED_TERRACOTTA);
            add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
            add(Material.GRAY_GLAZED_TERRACOTTA);
            add(Material.PODZOL);
            add(Material.PODZOL);
            add(Material.PODZOL);
            add(Material.ORANGE_GLAZED_TERRACOTTA);
        }};
        for (int i = 0; i < 3; i++) {
            List<Material> gen0 = new ArrayList<>() {{
                add(Material.COAL_ORE);
                add(Material.COAL_ORE);
                add(Material.COAL_ORE);
                add(Material.COAL_ORE);
                add(Material.COAL_ORE);
                add(Material.IRON_ORE);
                add(Material.REDSTONE_ORE);
                add(Material.EMERALD_ORE);
                add(Material.GOLD_ORE);
                add(Material.LAPIS_ORE);
                add(Material.DIAMOND_ORE);
                add(Material.GRASS_BLOCK);
                add(Material.GRASS_BLOCK);
                add(Material.GRASS_BLOCK);
                add(Material.GRASS_BLOCK);
                add(Material.COBWEB);
            }};
            matList.add(gen0);
            matList.add(gen1);
            matList.add(gen2);
            List<Material> gen3 = new ArrayList<>() {{
                add(Material.PACKED_ICE);
                add(Material.PACKED_ICE);
                add(Material.NOTE_BLOCK);
                add(Material.TNT);
                add(Material.LIGHT_BLUE_CONCRETE);
                add(Material.GLASS);
                add(Material.PACKED_ICE);
                add(Material.PACKED_ICE);
                add(Material.NOTE_BLOCK);
                add(Material.TNT);
                add(Material.LIGHT_BLUE_CONCRETE);
                add(Material.GLASS);
                add(Material.SOUL_SAND);
            }};
            matList.add(gen3);
            matList.add(gen4);
            matList.add(gen5);
            List<Material> gen6 = new ArrayList<>() {{
                add(Material.NETHERRACK);
                add(Material.NETHERRACK);
                add(Material.NETHERRACK);
                add(Material.NETHER_BRICKS);
                add(Material.NETHER_BRICKS);
                add(Material.NETHERRACK);
                add(Material.NETHERRACK);
                add(Material.NETHERRACK);
                add(Material.NETHER_BRICKS);
                add(Material.NETHER_BRICKS);
                add(Material.NETHER_GOLD_ORE);
                add(Material.NETHER_GOLD_ORE);
                add(Material.CRIMSON_NYLIUM);
                add(Material.WARPED_NYLIUM);
                add(Material.SOUL_SOIL);
                add(Material.CRACKED_NETHER_BRICKS);
                add(Material.RED_NETHER_BRICKS);
                add(Material.NETHER_WART_BLOCK);
                add(Material.CRYING_OBSIDIAN);
                add(Material.MAGMA_BLOCK);
            }};
            matList.add(gen6);
            matList.add(gen7);
            List<Material> gen8 = new ArrayList<>() {{
                add(Material.REDSTONE_BLOCK);
                add(Material.REDSTONE_BLOCK);
                add(Material.REDSTONE_LAMP);
                add(Material.TARGET);
                add(Material.PISTON);
                add(Material.SLIME_BLOCK);
                add(Material.OBSERVER);
            }};
            matList.add(gen8);
            matList.add(gen9);
            matList.add(gen10);
            List<Material> gen12 = new ArrayList<>() {{
                add(Material.DIRT);
                add(Material.GRASS_PATH);
                add(Material.GRASS_BLOCK);
                add(Material.OAK_SLAB);
                add(Material.BRICK_WALL);
                add(Material.BRICK_STAIRS);
            }};
            matList.add(gen12);
            List<Material> gen14 = new ArrayList<>() {{
                add(Material.LECTERN);
                add(Material.OBSIDIAN);
                add(Material.SPONGE);
                add(Material.BEEHIVE);
                add(Material.DRIED_KELP_BLOCK);
            }};
            matList.add(gen14);
            List<Material> gen15 = new ArrayList<>() {{
                add(Material.SANDSTONE);
                add(Material.SANDSTONE_SLAB);
                add(Material.RED_SANDSTONE);
                add(Material.RED_SANDSTONE_SLAB);
                add(Material.RED_TERRACOTTA);
                add(Material.TERRACOTTA);
                add(Material.YELLOW_TERRACOTTA);
            }};
            matList.add(gen15);
            List<Material> gen16 = new ArrayList<>() {{
                add(Material.JUNGLE_LOG);
                add(Material.STRIPPED_JUNGLE_LOG);
                add(Material.JUNGLE_WOOD);
                add(Material.STRIPPED_JUNGLE_WOOD);
                add(Material.MOSSY_COBBLESTONE);
                add(Material.MOSSY_COBBLESTONE);
                add(Material.MOSSY_COBBLESTONE);
                add(Material.JUNGLE_LEAVES);
                add(Material.JUNGLE_SLAB);
                add(Material.JUNGLE_TRAPDOOR);
            }};
            matList.add(gen16);
        }
        List<Material> gen11 = new ArrayList<>() {{
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.GLASS);
            add(Material.WHITE_STAINED_GLASS);
        }};
        matList.add(gen11); // Troll glass layer

        for (int i = 0; i < 2; i++) {
            safeMatList.add(gen1);
            safeMatList.add(gen2);
            safeMatList.add(gen4);
            safeMatList.add(gen5);
            safeMatList.add(gen7);
            safeMatList.add(gen9);
            safeMatList.add(gen10);
        }
        safeMatList.add(gen11); // Troll glass layer
    }

    // Define Random class
    Random random = new Random();
    /**
     * @return A random predefined List of Materials that are okay to use in the clump generator
     */
    public List<Material> getMaterialList() {
        return matList.get(random.nextInt(matList.size()));
    }
    
    /**
     * @return A random predefined List of Materials that are okay to spawn players on top of
     */
    public List<Material> getSafeMaterialList() { return safeMatList.get(random.nextInt(safeMatList.size())); }

    // Template:
    // private final List<Material> gen = new ArrayList<>() {{
        // add(Material.
    // }};

    private final List<List<Material>> matList = new ArrayList<>();

    private final List<List<Material>> safeMatList = new ArrayList<>();

}

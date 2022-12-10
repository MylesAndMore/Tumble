package com.MylesAndMore.tumble.api;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is dedicated to storing the different types of layers that can be generated.
 */
public class Layers {

    public Layers(){
        matList.add(gen0);
        matList.add(gen0);
        matList.add(gen0);
        matList.add(gen1);
        matList.add(gen1);
        matList.add(gen0);
        matList.add(gen0);
        matList.add(gen0);
        matList.add(gen1);
        matList.add(gen1);
        matList.add(gen2);
    }

    // Define Random class
    Random random = new Random();
    /**
     * @return A random predefined List of Materials that are okay to use in the clump generator
     */
    public List<Material> getMaterialList() {
        return matList.get(random.nextInt(matList.size()));
    }


    // Begin lists

    // private final List<Material> gen = new ArrayList<>() {{
        // add(Material.);
    // }};

    private final List<Material> gen0 = new ArrayList<>() {{
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
        add(Material.COBWEB);
        add(Material.GRASS_BLOCK);
        add(Material.GRASS_BLOCK);
    }};

    private final List<Material> gen1 = new ArrayList<>() {{
        add(Material.YELLOW_GLAZED_TERRACOTTA);
        add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        add(Material.GRAY_GLAZED_TERRACOTTA);
        add(Material.PODZOL);
        add(Material.PODZOL);
        add(Material.PODZOL);
        add(Material.ORANGE_GLAZED_TERRACOTTA);
    }};

    private final List<Material> gen2 = new ArrayList<>() {{
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
    }};

    private final List<List<Material>> matList = new ArrayList<>();

}

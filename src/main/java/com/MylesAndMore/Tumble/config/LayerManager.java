package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static com.MylesAndMore.Tumble.Main.plugin;

public class LayerManager {
    public static List<List<Material>> layers;
    private static int layerCount;

    private static final CustomConfig layersYml = new CustomConfig("layers.yml");
    private static final Configuration config = layersYml.getConfig();
    private static final Configuration defaultConfig = Objects.requireNonNull(config.getDefaults());

    private static final List<Material> unsafeMaterials = List.of(
        Material.COBWEB,
        Material.MAGMA_BLOCK,
        Material.CAMPFIRE,
        Material.VINE,
        Material.AIR
    );

    /**
     * Read layers from layers.yml and populate this.layers
     */
    public static void readConfig() {
        layers = new ArrayList<>();
        layerCount = 0;
        layersYml.saveDefaultConfig();

        readLayers(config.getConfigurationSection("layers"));
        if (layers.isEmpty()) {
            plugin.getLogger().warning("layers.yml: No layers were found, using defaults");
            readLayers(defaultConfig.getConfigurationSection("layers"));
        }

        // Don't use layers.size() because it includes duplicates for weighted layers
        plugin.getLogger().info("layers.yml: Loaded " + layerCount + (layerCount == 1 ? " layer" : " layers"));
    }

    /**
     * Read the layers from the layers.yml file
     * @param section The 'layers' section of the config
     */
    public static void readLayers(ConfigurationSection section) {
        if (section == null) {
            plugin.getLogger().warning("layers.yml is missing section 'layers', using defaults");
            return;
        }

        for (String layerPath : section.getKeys(false)) {
            ConfigurationSection layerSection = section.getConfigurationSection(layerPath);
            if (layerSection == null) {
                plugin.getLogger().warning("layers.yml: Layer '" + layerPath + "' is null");
                continue;
            }
            List<Material> layer = readLayer(layerSection);
            if (layer == null) {
                plugin.getLogger().warning("layers.yml: Failed to load layer '" + layerPath + "'");
                continue;
            }

            int weight = layerSection.getInt("weight", 1);
            layerCount++;
            for (int i = 0; i < weight; i++) {
                layers.add(layer);
            }
        }
    }

    /**
     * Read the list of materials for a layer
     * @param section The path of the layer in the config
     * @return The list of materials for the layer to be composed of
     */
    public static List<Material> readLayer(ConfigurationSection section) {
        List<String> materialsList = section.getStringList("materials");
        if (materialsList.isEmpty()) {
            plugin.getLogger().warning("layers.yml: Layer '" + section.getCurrentPath() + "' is missing section 'materials'");
            return null;
        }

        List<Material> materials = new ArrayList<>();
        for (String s : materialsList) {
            String[] sp = s.split(" ");

            if (sp.length < 1) {
                plugin.getLogger().warning("layers.yml: Invalid format in layer '" + section.getCurrentPath() + "'");
                continue;
            }
            String matName = sp[0];
            Material mat = Material.getMaterial(matName);
            if (mat == null) {
                plugin.getLogger().warning("layers.yml: Invalid material '" + matName + "' in layer '" + section.getCurrentPath() + "'");
                continue;
            }

            int matWeight;
            if (sp.length < 2) {
                matWeight = 1;
            } else {
                try {
                    matWeight = Integer.parseInt(sp[1]);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("layers.yml: Invalid weight '" + sp[1] + "' in layer '" + section.getCurrentPath() + "'");
                    matWeight = 1;
                }
            }

            for (int i = 0; i < matWeight; i++) {
                materials.add(mat);
            }
        }
        return materials;
    }

    /**
     * Selects a random layer for use in the generator.
     * @return A random layer
     */
    public static List<Material> getRandomLayer() {
        return layers.get(new Random().nextInt(layers.size()));
    }

    /**
     * Selects a random layer and removes materials that are unsafe for players to stand on.
     * @return A random safe layer
     */
    public static List<Material> getRandomLayerSafe() {
        List<Material> ret = new ArrayList<>(getRandomLayer()); // Deep copy
        ret.removeAll(unsafeMaterials);
        return ret;
    }
}

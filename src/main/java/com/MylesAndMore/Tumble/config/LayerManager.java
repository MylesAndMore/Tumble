package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.MylesAndMore.Tumble.Main.plugin;

public class LayerManager {
    public static List<List<Material>> layers = new ArrayList<>();

    private static final CustomConfig layersYml = new CustomConfig("layers.yml");
    private static final FileConfiguration config = layersYml.getConfig();

    /**
     * Read layers from layers.yml and populate this.layers
     */
    public static void readConfig() throws InvalidConfigurationException {
        layersYml.saveDefaultConfig();

        ConfigurationSection layersSection = config.getConfigurationSection("layers");
        if (layersSection == null) {
            throw new InvalidConfigurationException("layers.yml is missing section 'layers'");
        }
        for (String layerPath : layersSection.getKeys(false)) {
            List<Material> layer = readLayer(layerPath);
            if (layer == null) {
                plugin.getLogger().warning("layers.yml: Failed to load layer '" + layerPath + "'");
            } else {
                int weight = getLayerWeight(layerPath);
                for (int i = 0; i < weight; i++) {
                    layers.add(layer);
                }
            }
        }

        if (layers.isEmpty()) {
            throw new InvalidConfigurationException("No layers were found in layers.yml");
        }
        int numLayers = layersSection.getKeys(false).size(); // Don't use layers.size() because it includes duplicates for weighted layers
        plugin.getLogger().info("layers.yml: Loaded " + numLayers + (numLayers > 1 ? " layers" : " layer"));
    }

    /**
     * Read the list of materials for a layer.
     * @param path The path of the layer in the config
     * @return The list of materials for the layer to be composed of
     */
    public static List<Material> readLayer(String path) {
        List<Map<?, ?>> materialsSection = config.getMapList("layers." + path + ".materials");
        if (materialsSection.isEmpty()) {
            plugin.getLogger().warning("layers.yml: Layer '" + path + "' is missing section 'materials'");
            return null;
        }
        List<Material> materials = new ArrayList<>();

        for (Map<?, ?> materialMap : materialsSection) {
            String matName = (String)materialMap.get("material");
            Material mat = Material.getMaterial(matName);

            Object weightObj = materialMap.get("weight");
            int weight = 1;
            if (weightObj != null) {
                if (weightObj instanceof Integer) {
                    weight = (Integer)weightObj;
                } else {
                    plugin.getLogger().warning("layers.yml: Invalid weight in layer '" + path + "'");
                    return null;
                }
            }

            if (mat == null) {
                plugin.getLogger().warning("layers.yml: Invalid material '" + matName + "' in layer '" + path + "'");
                return null;
            }
            if (weight < 1) {
                plugin.getLogger().warning("layers.yml: Invalid weight '" + weight + "' in layer '" + path + "'");
                return null;
            }
            for (int i = 0; i < weight; i++) {
                materials.add(mat);
            }
        }
        return materials;
    }

    public static int getLayerWeight(String path) {
        return config.getInt("layers." + path + ".weight", 1);
    }

    public static List<Material> getRandom() {
        return layers.get(new Random().nextInt(layers.size()));
    }
}

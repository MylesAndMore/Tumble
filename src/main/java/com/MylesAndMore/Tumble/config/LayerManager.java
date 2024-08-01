package com.MylesAndMore.Tumble.config;

import com.MylesAndMore.Tumble.plugin.CustomConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.MylesAndMore.Tumble.Main.plugin;

public class LayerManager {
    public static List<List<Material>> layers;

    private static final CustomConfig layersYml = new CustomConfig("layers.yml");
    private static final FileConfiguration config = layersYml.getConfig();

    /**
     * Read layers from layers.yml and populate this.layers
     */
    public static void readConfig() {
        layersYml.saveDefaultConfig();

        ConfigurationSection layersSection = config.getConfigurationSection("layers");
        if (layersSection == null) {
            plugin.getLogger().warning("layers.yml is missing section 'layers'");
            return;
        }
        for (String layerPath : layersSection.getKeys(false)) {
            List<Material> layer = readLayer(layerPath);
            if (layer == null) {
                plugin.getLogger().warning("layers.yml: error loading layer at'"+layerPath+"'");
            } else {
                layers.add(layer);
            }
        }
    }

    /**
     * Read the list of materials for a layer.
     * @param path The path of the layer in the config
     * @return The list of materials for the layer to be composed of
     */
    public static List<Material> readLayer(String path) {
        List<String> list = config.getStringList(path);
        List<Material> layer = new ArrayList<>();
        for (String entry : list) {
            Material tmp = Material.getMaterial(entry);
            if (tmp == null) {

                return null;
            }
            layer.add(tmp);
        }
        return layer;
    }

    public static List<Material> getRandom() {
        return layers.get(new Random().nextInt(layers.size()));
    }
}

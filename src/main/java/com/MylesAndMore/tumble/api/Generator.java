package com.MylesAndMore.tumble.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Generator {
    public static void generateLayer(Location center, int radius, Material material) {
        int Cx = center.getBlockX();
        int Cy = center.getBlockY();
        int Cz = center.getBlockZ();
        World world = center.getWorld();

        int rSq = radius * radius;

        for (int x = Cx - radius; x <= Cx + radius; x++) {
            for (int z = Cz - radius; z <= Cz + radius; z++) {
                if ((Cx - x) * (Cx - x) + (Cz - z) * (Cz - z) <= rSq) {
                    Location block = new Location(world, x, Cy, z);
                    world.getBlockAt(x, Cy, z).setType(material);
                }
            }
        }
    }
}

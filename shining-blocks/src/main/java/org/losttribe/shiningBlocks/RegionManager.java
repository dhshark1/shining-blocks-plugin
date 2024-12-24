package org.losttribe.shiningBlocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Stores the top-left and bottom-right corners of the wall region.
 * Now includes load/save from config.yml.
 */
public class RegionManager {

    private Location corner1;
    private Location corner2;

    private JavaPlugin plugin;

    public RegionManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setCorner1(Location loc) {
        this.corner1 = loc;
    }

    public void setCorner2(Location loc) {
        this.corner2 = loc;
    }

    public boolean isRegionDefined() {
        return (corner1 != null && corner2 != null);
    }

    public boolean isWithinRegion(Location loc) {
        if (!isRegionDefined()) return false;
        if (!corner1.getWorld().equals(loc.getWorld())) return false;

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return (loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ);
    }

    /**
     * Load corners from config.yml
     */
    public void loadRegionFromConfig(FileConfiguration config) {
        if (!config.contains("region")) {
            // No region data
            return;
        }
        String worldName = config.getString("region.world");
        double x1 = config.getDouble("region.corner1.x");
        double y1 = config.getDouble("region.corner1.y");
        double z1 = config.getDouble("region.corner1.z");
        double x2 = config.getDouble("region.corner2.x");
        double y2 = config.getDouble("region.corner2.y");
        double z2 = config.getDouble("region.corner2.z");

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("RegionManager: World '" + worldName + "' not found. Region not loaded.");
            return;
        }
        corner1 = new Location(world, x1, y1, z1);
        corner2 = new Location(world, x2, y2, z2);
        plugin.getLogger().info("Region loaded from config: " + corner1 + " to " + corner2);
    }

    /**
     * Save corners to config.yml
     */
    public void saveRegionToConfig(FileConfiguration config) {
        if (!isRegionDefined()) return;

        config.set("region.world", corner1.getWorld().getName());
        config.set("region.corner1.x", corner1.getX());
        config.set("region.corner1.y", corner1.getY());
        config.set("region.corner1.z", corner1.getZ());

        config.set("region.corner2.x", corner2.getX());
        config.set("region.corner2.y", corner2.getY());
        config.set("region.corner2.z", corner2.getZ());
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }
}

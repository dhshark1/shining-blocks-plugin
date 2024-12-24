package org.losttribe.shiningBlocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Convenience class for storing block positions (world, x, y, z).
 */
public class BlockPosition {
    private String worldName;
    private int x;
    private int y;
    private int z;

    public BlockPosition(String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Block toBlock() {
        World w = Bukkit.getWorld(worldName);
        if (w == null) return null;
        return w.getBlockAt(x, y, z);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BlockPosition)) return false;
        BlockPosition o = (BlockPosition) other;
        return o.worldName.equals(this.worldName)
                && o.x == this.x
                && o.y == this.y
                && o.z == this.z;
    }

    @Override
    public int hashCode() {
        return (worldName + x + y + z).hashCode();
    }
}

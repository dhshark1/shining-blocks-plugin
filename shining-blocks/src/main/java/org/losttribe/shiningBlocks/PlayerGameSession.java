package org.losttribe.shiningBlocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Manages a single player's 4-wave game session and input checks.
 */
public class PlayerGameSession {

    private JavaPlugin plugin;
    private Player player;
    private RegionManager regionManager;
    private PatternManager patternManager;

    private int currentWave;         // which wave the player is on
    private boolean waitingForClicks; // is the player expected to click
    private int clickIndex;          // next index in the pattern

    public PlayerGameSession(JavaPlugin plugin, Player player,
                             RegionManager regionManager,
                             PatternManager patternManager) {
        this.plugin = plugin;
        this.player = player;
        this.regionManager = regionManager;
        this.patternManager = patternManager;
        this.currentWave = 0;
        this.waitingForClicks = false;
        this.clickIndex = 0;
    }

    public void beginWave(int wave) {
        // Ensure correct wave progression
        if (wave != currentWave + 1) {
            player.sendMessage("You cannot start wave " + wave + " yet. "
                    + "Complete wave " + (currentWave + 1) + " first.");
            return;
        }

        currentWave = wave;
        clickIndex = 0;
        waitingForClicks = true;

        List<Location> pattern = patternManager.getWavePattern(wave);
        if (pattern == null || pattern.isEmpty()) {
            player.sendMessage("Wave " + wave + " pattern is not set!");
            return;
        }

        // Highlight the entire pattern so the player sees which blocks to click
        highlightPattern(pattern);

        player.sendMessage("Wave " + wave + " has begun! Click the blocks in the correct order.");
    }

    public void handleBlockClick(BlockPosition blockPos) {
        if (!waitingForClicks) return; // no wave in progress

        Block clickedBlock = blockPos.toBlock();
        if (clickedBlock == null) return;

        // Must be inside the defined region (the "wall")
        if (!regionManager.isWithinRegion(clickedBlock.getLocation())) {
            return;
        }

        List<Location> pattern = patternManager.getWavePattern(currentWave);
        Location expectedLoc = pattern.get(clickIndex);

        if (isSameBlock(expectedLoc, clickedBlock.getLocation())) {
            // Correct
            clickIndex++;
            player.sendMessage("Correct block " + clickIndex + "/" + pattern.size() + "!");

            // If wave is completed
            if (clickIndex >= pattern.size()) {
                waitingForClicks = false;
                if (currentWave == 4) {
                    player.sendMessage("Congratulations! You've completed wave 4 and the entire challenge!");
                } else {
                    player.sendMessage("Wave " + currentWave + " complete! "
                            + "Use /light" + (currentWave + 1) + " to start the next wave.");
                }
            }
        } else {
            // Wrong block
            turnBlockRed(clickedBlock);
            player.sendMessage("That was the wrong block! Memorize the pattern again!");

            waitingForClicks = false;
            clickIndex = 0;

            // After 2s, revert to lamp, then re-light the pattern
            new BukkitRunnable() {
                @Override
                public void run() {
                    revertToLamp(clickedBlock);
                    highlightPattern(pattern);
                    waitingForClicks = true;
                }
            }.runTaskLater(plugin, 40L); // 2 seconds
        }
    }

    private boolean isSameBlock(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ()
                && loc1.getWorld().equals(loc2.getWorld());
    }

    private void highlightPattern(List<Location> pattern) {
        // Turn them 'on' (for demonstration, use REDSTONE_BLOCK)
        for (Location loc : pattern) {
            Block b = loc.getBlock();
            if (b != null) {
                b.setType(Material.REDSTONE_BLOCK);
            }
        }
    }

    private void turnBlockRed(Block block) {
        block.setType(Material.RED_WOOL);
    }

    private void revertToLamp(Block block) {
        block.setType(Material.REDSTONE_LAMP);
    }
}


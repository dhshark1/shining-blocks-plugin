package org.losttribe.shiningBlocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerGameSession {

    private JavaPlugin plugin;
    private Player player;
    private RegionManager regionManager;
    private PatternManager patternManager;

    private int currentWave;
    private boolean waitingForClicks;
    private int clickIndex;
    private Set<Location> clickedThisWave = new HashSet<>();

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
        if (wave != currentWave + 1) {
            player.sendMessage("You cannot start wave " + wave + " yet. Complete wave " + (currentWave + 1) + " first.");
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

        highlightPattern(pattern, wave);

        player.sendMessage("Wave " + wave + " has begun! Click the blocks in the correct order.");
    }

    private void highlightPattern(List<Location> pattern, int waveNumber) {
        for (Location loc : pattern) {
            Block b = loc.getBlock();
            if (b != null) {
                b.setType(Material.REDSTONE_BLOCK);
            }
        }

        long revertTicks;
        switch (waveNumber) {
            case 1: revertTicks = 100L; break;
            case 2: revertTicks = 80L;  break;
            case 3: revertTicks = 40L;  break;
            case 4: revertTicks = 20L;  break;
            default: revertTicks = 100L; break;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : pattern) {
                    Block b = loc.getBlock();
                    if (b != null) {
                        b.setType(Material.REDSTONE_LAMP);
                    }
                }
            }
        }.runTaskLater(plugin, revertTicks);
    }

    public void handleBlockClick(BlockPosition blockPos) {
        if (!waitingForClicks) return;

        Block clickedBlock = blockPos.toBlock();
        if (clickedBlock == null) return;

        if (!regionManager.isWithinRegion(clickedBlock.getLocation())) {
            return;
        }

        List<Location> pattern = patternManager.getWavePattern(currentWave);
        Location expectedLoc = pattern.get(clickIndex);

        if (isSameBlock(expectedLoc, clickedBlock.getLocation())) {
            if (clickedThisWave.contains(clickedBlock.getLocation())) {
                player.sendMessage("You already clicked this block; ignoring.");
                return;
            }
            clickIndex++;
            player.sendMessage("Correct block " + clickIndex + "/" + pattern.size() + "!");

            if (clickIndex >= pattern.size()) {
                waitingForClicks = false;
                if (currentWave == 4) {
                    player.sendMessage("Congratulations! You've completed wave 4 and the entire challenge!");
                } else {
                    player.sendMessage("Wave " + currentWave + " complete! ");
                }
            }
        } else {
            turnBlockRed(clickedBlock);
            player.sendMessage("That was the wrong block! Memorize the pattern again!");

            waitingForClicks = false;
            clickIndex = 0;

            new BukkitRunnable() {
                @Override
                public void run() {
                    revertToLamp(clickedBlock);
                    highlightPattern(pattern, currentWave);
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

    private void turnBlockRed(Block block) {
        block.setType(Material.RED_WOOL);
    }

    private void revertToLamp(Block block) {
        block.setType(Material.REDSTONE_LAMP);
    }
}


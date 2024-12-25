package org.losttribe.shiningBlocks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PatternManager {

    private List<Location>[] wavePatterns;
    private JavaPlugin plugin;

    @SuppressWarnings("unchecked")
    public PatternManager(JavaPlugin plugin) {
        this.plugin = plugin;
        wavePatterns = (List<Location>[]) new List[4];
        for (int i = 0; i < 4; i++) {
            wavePatterns[i] = new ArrayList<>();
        }
    }

    public boolean addBlockToWave(int wave, Location loc) {
        if (wave < 1 || wave > 4) return false;
        List<Location> pattern = wavePatterns[wave - 1];

        if (pattern.contains(loc)) {
            return false;
        }

        pattern.add(loc);
        return true;
    }

    public List<Location> getWavePattern(int wave) {
        if (wave < 1 || wave > 4) return null;
        return wavePatterns[wave - 1];
    }

    public boolean isAllWavesDefined() {
        for (int i = 0; i < 4; i++) {
            if (wavePatterns[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void loadPatternsFromConfig(FileConfiguration config) {
        for (int wave = 1; wave <= 4; wave++) {
            String path = "patterns.wave" + wave;
            if (!config.isList(path)) {
                continue;
            }

            List<String> locStrings = config.getStringList(path);
            for (String locString : locStrings) {
                String[] parts = locString.split(":");
                if (parts.length < 4) {
                    plugin.getLogger().warning("Invalid pattern location string: " + locString);
                    continue;
                }
                String worldName = parts[0];
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);

                World w = Bukkit.getWorld(worldName);
                if (w == null) {
                    plugin.getLogger().warning("PatternManager: World '" + worldName + "' not found.");
                    continue;
                }
                Location loc = new Location(w, x, y, z);
                wavePatterns[wave - 1].add(loc);
            }
        }
    }

    public void savePatternsToConfig(FileConfiguration config) {
        for (int wave = 1; wave <= 4; wave++) {
            List<String> locStrings = new ArrayList<>();
            for (Location loc : wavePatterns[wave - 1]) {
                String locString = String.format("%s:%.2f:%.2f:%.2f",
                        loc.getWorld().getName(),
                        loc.getX(), loc.getY(), loc.getZ());
                locStrings.add(locString);
            }
            config.set("patterns.wave" + wave, locStrings);
        }
    }
}


package org.losttribe.shiningBlocks;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public final class ShiningBlocks extends JavaPlugin {

    private static ShiningBlocks instance;

    private RegionManager regionManager;
    private PatternManager patternManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;

        FileConfiguration config = loadGameData();

        // 2) Initialize managers
        regionManager = new RegionManager(this);
        patternManager = new PatternManager(this);
        gameManager = new GameManager(this, regionManager, patternManager);

        // Region corners
        regionManager.loadRegionFromConfig(config);

        // Patterns
        patternManager.loadPatternsFromConfig(config);

        // 4) Register commands
        getCommand("setcorner1").setExecutor(new CornerCommand(regionManager, true));
        getCommand("setcorner2").setExecutor(new CornerCommand(regionManager, false));

        getCommand("setone").setExecutor(new SetPatternCommand(this, patternManager, 1));
        getCommand("settwo").setExecutor(new SetPatternCommand(this, patternManager, 2));
        getCommand("setthree").setExecutor(new SetPatternCommand(this, patternManager, 3));
        getCommand("setfour").setExecutor(new SetPatternCommand(this, patternManager, 4));

        getCommand("shiningblocks").setExecutor(new ShiningBlocksCommand(gameManager));

        getCommand("lightone").setExecutor(new LightCommand(gameManager, 1));
        getCommand("lighttwo").setExecutor(new LightCommand(gameManager, 2));
        getCommand("lightthree").setExecutor(new LightCommand(gameManager, 3));
        getCommand("lightfour").setExecutor(new LightCommand(gameManager, 4));

        // 5) Register event listener for block clicks
        getServer().getPluginManager().registerEvents(new PlayerClickListener(gameManager), this);

        getLogger().info("ShiningBlocks plugin enabled with YAML config support.");
    }

    @Override
    public void onDisable() {
        // 6) Optionally save again onDisable (to ensure no data loss)
        saveDataToConfig();
        getLogger().info("ShiningBlocks plugin disabled.");
    }


    /**
     * Saves corner and pattern data to config.yml
     */
    public void saveDataToConfig() {
        FileConfiguration config = getConfig();

        // Region corners
        regionManager.saveRegionToConfig(config);

        // Patterns
        patternManager.savePatternsToConfig(config);

        // Write to file
        saveConfig();
    }

    public FileConfiguration loadGameData() {
        File configFile = new File(getDataFolder(), "config.yml");

        // Create plugin folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // If config.yml doesn't exist, create it with default content
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    String defaultContent =
                            "wall:\n" +
                                    "  topLeft: null\n" +
                                    "  bottomRight: null\n" +
                                    "patterns:\n" +
                                    "  wave1: []\n" +
                                    "  wave2: []\n" +
                                    "  wave3: []\n" +
                                    "  wave4: []\n";

                    Files.write(configFile.toPath(), defaultContent.getBytes(StandardCharsets.UTF_8));
                    getLogger().info("Created new config.yml with default ShiningBlocks content.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Tells Spigot to reload (or load) the plugin’s config.yml into memory
        reloadConfig();

        // Now getConfig() will point to config.yml
        return getConfig();
    }


    public static ShiningBlocks getInstance() {
        return instance;
    }
}

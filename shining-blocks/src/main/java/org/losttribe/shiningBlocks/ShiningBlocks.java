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

        regionManager = new RegionManager(this);
        patternManager = new PatternManager(this);
        gameManager = new GameManager(this, regionManager, patternManager);

        regionManager.loadRegionFromConfig(config);

        patternManager.loadPatternsFromConfig(config);

        getCommand("setwall").setExecutor(new SetWallCommand(this, regionManager));

        getCommand("setone").setExecutor(new SetPatternCommand(this, patternManager, 1, regionManager));
        getCommand("settwo").setExecutor(new SetPatternCommand(this, patternManager, 2, regionManager));
        getCommand("setthree").setExecutor(new SetPatternCommand(this, patternManager, 3, regionManager));
        getCommand("setfour").setExecutor(new SetPatternCommand(this, patternManager, 4, regionManager));

        getCommand("shiningblocks").setExecutor(new ShiningBlocksCommand(gameManager));

        getCommand("lightone").setExecutor(new LightCommand(gameManager, 1));
        getCommand("lighttwo").setExecutor(new LightCommand(gameManager, 2));
        getCommand("lightthree").setExecutor(new LightCommand(gameManager, 3));
        getCommand("lightfour").setExecutor(new LightCommand(gameManager, 4));

        getServer().getPluginManager().registerEvents(new PlayerClickListener(gameManager, regionManager), this);

    }

    @Override
    public void onDisable() {
        saveDataToConfig();
        getLogger().info("ShiningBlocks plugin disabled.");
    }


    public void saveDataToConfig() {
        FileConfiguration config = getConfig();

        regionManager.saveRegionToConfig(config);

        patternManager.savePatternsToConfig(config);

        saveConfig();
    }

    public FileConfiguration loadGameData() {
        File configFile = new File(getDataFolder(), "config.yml");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

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

        reloadConfig();

        return getConfig();
    }


    public static ShiningBlocks getInstance() {
        return instance;
    }
}

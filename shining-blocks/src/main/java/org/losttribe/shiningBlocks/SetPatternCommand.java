package org.losttribe.shiningBlocks;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SetPatternCommand implements CommandExecutor {

    private final PatternManager patternManager;
    private final int waveNumber;
    private final ShiningBlocks plugin;
    private RegionManager regionManager;

    private static final int MAX_TARGET_DISTANCE = 10;

    public SetPatternCommand(ShiningBlocks plugin, PatternManager patternManager, int waveNumber, RegionManager regionManager) {
        this.plugin = plugin;
        this.patternManager = patternManager;
        this.waveNumber = waveNumber;
        this.regionManager = regionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }
        Player player = (Player) sender;

        if (!regionManager.isRegionDefined()) {
            player.sendMessage(ChatColor.RED + "You must select a wall region first before setting a pattern.");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(MAX_TARGET_DISTANCE);
        if (targetBlock == null) {
            player.sendMessage("You're not looking at a valid block within " + MAX_TARGET_DISTANCE + " blocks.");
            return true;
        }

        Location loc = targetBlock.getLocation();
        boolean added = patternManager.addBlockToWave(waveNumber, loc);
        if (!added) {
            player.sendMessage("This block is already in the pattern for wave " + waveNumber + ". Ignoring.");
        } else {
            player.sendMessage("Block added to wave " + waveNumber + "!");
        }
        plugin.saveDataToConfig();
        return true;
    }
}

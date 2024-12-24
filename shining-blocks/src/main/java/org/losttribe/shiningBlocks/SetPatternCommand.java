package org.losttribe.shiningBlocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to add the block (the player is looking at) to one wave’s pattern.
 */
public class SetPatternCommand implements CommandExecutor {

    private final PatternManager patternManager;
    private final int waveNumber;
    private final ShiningBlocks plugin; // to call saveDataToConfig

    private static final int MAX_TARGET_DISTANCE = 10;

    public SetPatternCommand(ShiningBlocks plugin, PatternManager patternManager, int waveNumber) {
        this.plugin = plugin;
        this.patternManager = patternManager;
        this.waveNumber = waveNumber;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }
        Player player = (Player) sender;

        Block targetBlock = player.getTargetBlockExact(MAX_TARGET_DISTANCE);
        if (targetBlock == null) {
            player.sendMessage("You're not looking at a valid block within " + MAX_TARGET_DISTANCE + " blocks.");
            return true;
        }

        Location loc = targetBlock.getLocation();
        patternManager.addBlockToWave(waveNumber, loc);
        player.sendMessage("Added the block you're looking at ("
                + loc.getBlockX() + ", "
                + loc.getBlockY() + ", "
                + loc.getBlockZ() + ") to wave " + waveNumber + " pattern.");

        // Save to config right away
        plugin.saveDataToConfig();
        return true;
    }
}

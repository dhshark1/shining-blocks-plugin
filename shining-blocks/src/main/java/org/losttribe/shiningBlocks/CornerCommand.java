package org.losttribe.shiningBlocks;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CornerCommand implements CommandExecutor {

    private RegionManager regionManager;
    private boolean isFirstCorner;
    private ShiningBlocks plugin; // to call saveDataToConfig

    public CornerCommand(RegionManager regionManager, boolean isFirstCorner) {
        this.regionManager = regionManager;
        this.isFirstCorner = isFirstCorner;
        // We'll grab the plugin via the static getter
        this.plugin = ShiningBlocks.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }
        Player player = (Player) sender;
        Location loc = player.getLocation(); // or getTargetBlockExact(...) if you prefer
        if (isFirstCorner) {
            regionManager.setCorner1(loc);
            player.sendMessage("Top-left corner set to " + formatLocation(loc));
        } else {
            regionManager.setCorner2(loc);
            player.sendMessage("Bottom-right corner set to " + formatLocation(loc));
        }

        // Save to config right away
        plugin.saveDataToConfig();
        return true;
    }

    private String formatLocation(Location loc) {
        return String.format("[%s] (%.0f, %.0f, %.0f)",
                loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
    }
}



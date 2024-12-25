package org.losttribe.shiningBlocks;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWallCommand implements CommandExecutor {

    private final ShiningBlocks plugin;
    private final RegionManager regionManager;

    public SetWallCommand(ShiningBlocks plugin, RegionManager regionManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /setwall <top|bottom>");
            return true;
        }

        String position = args[0].toLowerCase();

        Block targetBlock = player.getTargetBlockExact(50);
        if (targetBlock == null) {
            player.sendMessage(ChatColor.RED + "You are not looking at any block within range.");
            return true;
        }

        Location loc = targetBlock.getLocation();

        switch (position) {
            case "top":
                regionManager.setCorner1(loc); // top-left corner
                player.sendMessage(ChatColor.GREEN +
                        "Top-left corner of the wall set at the block you're looking at.");
                break;

            case "bottom":
                regionManager.setCorner2(loc); // bottom-right corner
                player.sendMessage(ChatColor.GREEN +
                        "Bottom-right corner of the wall set at the block you're looking at.");
                break;

            default:
                player.sendMessage(ChatColor.RED +
                        "Invalid argument. Use '/setwall <top|bottom>'.");
                return true;
        }

        plugin.saveDataToConfig();

        return true;
    }
}

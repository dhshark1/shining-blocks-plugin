package org.losttribe.shiningBlocks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShiningBlocksCommand implements CommandExecutor {

    private GameManager gameManager;

    public ShiningBlocksCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can start the ShiningBlocks game.");
            return true;
        }
        Player player = (Player) sender;
        gameManager.startGameForPlayer(player);
        return true;
    }
}


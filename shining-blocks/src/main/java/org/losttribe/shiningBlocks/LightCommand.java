package org.losttribe.shiningBlocks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LightCommand implements CommandExecutor {

    private GameManager gameManager;
    private int waveNumber;

    public LightCommand(GameManager gameManager, int waveNumber) {
        this.gameManager = gameManager;
        this.waveNumber = waveNumber;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can trigger waves.");
            return true;
        }
        Player player = (Player) sender;
        gameManager.startWave(player, waveNumber);
        return true;
    }
}


package org.losttribe.shiningBlocks;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;

/**
 * Listens for player block interactions and notifies GameManager about them.
 */
public class PlayerClickListener implements Listener {

    private final GameManager gameManager;

    public PlayerClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only handle block clicks
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Player player = event.getPlayer();
        // Create a BlockPosition object to pass into the GameManager
        BlockPosition blockPos = new BlockPosition(
                clickedBlock.getWorld().getName(),
                clickedBlock.getX(),
                clickedBlock.getY(),
                clickedBlock.getZ()
        );

        // Let the GameManager handle the logic of “this player clicked this block”
        gameManager.handleBlockClick(player, blockPos);
    }
}

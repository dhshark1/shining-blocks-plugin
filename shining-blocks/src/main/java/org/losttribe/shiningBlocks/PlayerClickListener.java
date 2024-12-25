package org.losttribe.shiningBlocks;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;

public class PlayerClickListener implements Listener {

    private GameManager gameManager;
    private RegionManager regionManager;

    public PlayerClickListener(GameManager gameManager, RegionManager regionManager) {
        this.gameManager = gameManager;
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (gameManager.isPlayerEditingWave(player)) {
                event.setCancelled(true);
                if (!regionManager.isRegionDefined()) {
                    return;
                }
                int wave = gameManager.getWaveBeingEdited(player);
                if (wave > 0) {
                    gameManager.getPatternManager().addBlockToWave(
                            wave,
                            clickedBlock.getLocation()
                    );
                    player.sendMessage("Added block at ("
                            + clickedBlock.getX() + ", "
                            + clickedBlock.getY() + ", "
                            + clickedBlock.getZ() + ") to wave " + wave + " pattern!");

                    if (ShiningBlocks.getInstance() != null) {
                        ShiningBlocks.getInstance().saveDataToConfig();
                    }
                }
            } else if (gameManager.isPlayerInShiningBlocks(player)) {
                event.setCancelled(true);
                gameManager.handleBlockClick(player, new BlockPosition(
                        clickedBlock.getWorld().getName(),
                        clickedBlock.getX(),
                        clickedBlock.getY(),
                        clickedBlock.getZ()
                ));
            }
        }
    }
}

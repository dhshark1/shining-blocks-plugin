package org.losttribe.shiningBlocks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages the overall ShiningBlocks game sessions and wave progression.
 */
public class GameManager {

    private JavaPlugin plugin;
    private RegionManager regionManager;
    private PatternManager patternManager;

    private Map<UUID, PlayerGameSession> sessions;

    public GameManager(JavaPlugin plugin, RegionManager regionManager, PatternManager patternManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
        this.patternManager = patternManager;
        this.sessions = new HashMap<>();
    }

    public void startGameForPlayer(Player player) {
        if (!regionManager.isRegionDefined()) {
            player.sendMessage("The wall region is not defined yet! Set both corners first.");
            return;
        }

        if (!patternManager.isAllWavesDefined()) {
            player.sendMessage("Not all wave patterns are set. Please set all 4 patterns first.");
            return;
        }

        sessions.remove(player.getUniqueId());

        PlayerGameSession session = new PlayerGameSession(plugin, player, regionManager, patternManager);
        sessions.put(player.getUniqueId(), session);

        player.sendMessage("ShiningBlocks game initialized. Use /lightone to begin wave 1.");
    }

    public void startWave(Player player, int wave) {
        PlayerGameSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            player.sendMessage("No active ShiningBlocks game. Use /shiningblocks first.");
            return;
        }
        session.beginWave(wave);
    }

    public void handleBlockClick(Player player, BlockPosition blockPos) {
        PlayerGameSession session = sessions.get(player.getUniqueId());
        if (session != null) {
            session.handleBlockClick(blockPos);
        }
    }
}

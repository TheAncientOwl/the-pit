package com.buha007.thepit.listeners;

import java.util.UUID;

import com.buha007.thepit.playerData.Data;
import com.buha007.thepit.playerData.PlayerData;
import com.buha007.thepit.pvpmanager.PvPManager;
import com.buha007.thepit.tab.Tab;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

    public Quit() {
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Tab.clear(player);
        PlayerData.clearScoreboard(player);
        UUID playerID = player.getUniqueId();

        if (!Data.isSaving()) {
            Data.get(playerID).saveData();
            Data.remove(playerID);
        }

        if (!PvPManager.isInCombat(playerID))
            return;
        PvPManager.turnOffCombat(playerID);
        // player.setHealth(0);
    }
}
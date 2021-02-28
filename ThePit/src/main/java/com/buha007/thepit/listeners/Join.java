package com.buha007.thepit.listeners;

import java.util.UUID;

import com.buha007.thepit.equipment.Equipment;
import com.buha007.thepit.playerData.Data;
import com.buha007.thepit.playerData.PlayerData;
import com.buha007.thepit.tab.Tab;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

    public Join() {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID playerID = player.getUniqueId();
        PlayerData playerData = new PlayerData(player);

        Data.put(playerID, playerData);
        player.getInventory().clear();
        Equipment.equip(player, playerData);
        Tab.setPrefix(player, playerData.getLevelPrefix());
        PlayerData.createScoreboard(player, playerData);
    }

}
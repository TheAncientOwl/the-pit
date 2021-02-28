package com.buha007.thepit.pvpmanager;

import com.buha007.thepit.main.Main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlocker implements Listener {
    public CommandBlocker() {
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if (!PvPManager.isInCombat(player.getUniqueId()))
            return;

        if (player.hasPermission("thepit.allowcommandsincombat"))
            return;

        String message = e.getMessage();
        for (String allowedCommand : Main.getPlugin().getConfig().getStringList("whitelistedCommands")) {
            if (message.startsWith(allowedCommand)) {
                return;
            }
        }
        e.setCancelled(true);
    }

}
package com.buha007.thepit.listeners;

import com.buha007.thepit.playerData.Data;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {

    public Chat() {
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setFormat(e.getFormat().replace("{PITLVL}", Data.get(e.getPlayer().getUniqueId()).getLevelPrefix()));
    }

}
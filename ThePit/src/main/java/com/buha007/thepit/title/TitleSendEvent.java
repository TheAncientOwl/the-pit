package com.buha007.thepit.title;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TitleSendEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private String title;
    private String subtitle;
    private boolean cancelled = false;

    protected TitleSendEvent(Player player, String title, String subtitle) {
        this.player = player;
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    protected static HandlerList getHandlerList() {
        return handlers;
    }

    protected Player getPlayer() {
        return player;
    }

    protected String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected String getSubtitle() {
        return subtitle;
    }

    protected void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    protected boolean isCancelled() {
        return cancelled;
    }

    protected void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}

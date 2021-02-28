package com.buha007.thepit.pvpmanager;

import net.md_5.bungee.api.ChatColor;

public class Status {
    public static final String IDLING = ChatColor.WHITE + "Status: " + ChatColor.GREEN + "Idling";
    public static final String FIGHTING = ChatColor.WHITE + "Status: " + ChatColor.RED + "Fighting";
    public static final String BOUNTIED = ChatColor.WHITE + "Status: " + ChatColor.RED + "Bountied";

    private Status() {
    }
}

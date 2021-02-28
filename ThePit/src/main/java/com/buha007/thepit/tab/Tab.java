package com.buha007.thepit.tab;

import java.util.ArrayList;

import com.buha007.thepit.main.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Tab {
    private static String TAB_PREFIX, TAB_SUFFIX, TAG_PREFIX, TAG_SUFFIX, CLEAR;
    private static ConsoleCommandSender console;
    private static ArrayList<Prefix> prefixes;

    public static void load(Main instance) {
        console = instance.getServer().getConsoleSender();
        FileConfiguration cfg = instance.getConfig();
        TAB_PREFIX = cfg.getString("tabPrefix");
        TAG_PREFIX = cfg.getString("tagPrefix");
        TAB_SUFFIX = cfg.getString("tabSuffix");
        TAG_SUFFIX = cfg.getString("tagSuffix");
        CLEAR = cfg.getString("clear");
        prefixes = new ArrayList<Prefix>();
        prefixes.clear();
        for (String key : cfg.getStringList("registeredTabPrefixes")) {
            String path = "customTabPrefix." + key + ".";
            Prefix prefix = new Prefix(cfg.getString(path + "prefix"), cfg.getString(path + "permission"));
            prefixes.add(prefix);
        }
    }

    public static void setPrefix(Player player, String levelPrefix) {
        String tab = TAB_PREFIX;
        String prefix = levelPrefix;
        String playerName = player.getName();

        String rank = " &7";

        for (Prefix pref : prefixes)
            if (pref.canUse(player)) {
                rank = pref.getPrefix();
                break;
            }

        tab = tab.replace("{name}", playerName);
        tab = tab.replace("{value}", prefix + rank);

        String tag = TAG_PREFIX;
        tag = tag.replace("{name}", playerName);
        tag = tag.replace("{value}", prefix + rank);

        Bukkit.dispatchCommand(console, tab);
        Bukkit.dispatchCommand(console, tag);
    }

    public static void setSuffix(Player player, String suffix) {
        String tab = TAB_SUFFIX;
        tab = tab.replace("{name}", player.getName());
        tab = tab.replace("{value}", suffix);

        String tag = TAG_SUFFIX;
        tag = tag.replace("{name}", player.getName());
        tag = tag.replace("{value}", suffix);

        Bukkit.dispatchCommand(console, tab);
        Bukkit.dispatchCommand(console, tag);
    }

    public static void clear(Player player) {
        String clear = CLEAR;
        clear = clear.replace("{name}", player.getName());

        Bukkit.dispatchCommand(console, clear);
    }

    protected static class Prefix {
        private String prefix, permission;

        public Prefix(String prefix, String permission) {
            this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            this.permission = permission;
        }

        public boolean canUse(Player player) {
            return player.hasPermission(permission);
        }

        public String getPrefix() {
            return this.prefix;
        }

    }

    private Tab() {
    }
}
package com.buha007.thepit.utilities;

import com.buha007.thepit.main.Main;
import com.buha007.thepit.title.Title;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Msg {
    private static ConfigAccessor messages;
    public static String KILL, DOUBLE_KILL, TRIPLE_KILL, QUADRUPLE_KILL, PENTA_KILL, MULTI_KILL, ASSIST, LEVEL_UP_TITLE,
            LEVEL_UP_CHAT, STREAK, BOUNTY, BOUNTY_CLAIMED;

    public static void init(Main instance) {
        messages = new ConfigAccessor(instance, "messages.yml");
        reloadMessages();
    }

    public static void reloadMessages() {
        messages.reloadConfig();
        final FileConfiguration cfg = messages.getConfig();
        KILL = color(cfg.getString("kill"));
        DOUBLE_KILL = color(cfg.getString("doubleKill"));
        TRIPLE_KILL = color(cfg.getString("tripleKill"));
        QUADRUPLE_KILL = color(cfg.getString("quadrupleKill"));
        PENTA_KILL = color(cfg.getString("pentaKill"));
        MULTI_KILL = color(cfg.getString("multiKill"));
        ASSIST = color(cfg.getString("assist"));
        LEVEL_UP_TITLE = color(cfg.getString("levelUpTitle"));
        LEVEL_UP_CHAT = color(cfg.getString("levelUpChat"));
        STREAK = color(cfg.getString("streak"));
        BOUNTY = color(cfg.getString("bounty"));
        BOUNTY_CLAIMED = color(cfg.getString("bountyClaimed"));
    }

    public static FileConfiguration getMessages() {
        return messages.getConfig();
    }

    public static void printConsole(String message) {
        Main.getPlugin().getServer().getConsoleSender()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void titleChatLevelUp(Player player, String oldLevelPrefix, String newLevelPrefix) {
        String transitMessage = oldLevelPrefix + ChatColor.GRAY + "" + ChatColor.BOLD + " -> " + newLevelPrefix;
        Title.sendTitleSubtitle(player, 10, 10, 10, Msg.LEVEL_UP_TITLE, transitMessage);
        player.sendMessage(Msg.LEVEL_UP_CHAT + " " + transitMessage);
    }

    private static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getKillMessage(int streak) {
        if (streak == 1)
            return KILL;
        if (streak == 2)
            return DOUBLE_KILL;
        if (streak == 3)
            return TRIPLE_KILL;
        if (streak == 4)
            return QUADRUPLE_KILL;
        if (streak == 5)
            return PENTA_KILL;
        return MULTI_KILL;
    }

    private Msg() {
    }
}
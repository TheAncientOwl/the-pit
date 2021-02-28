package com.buha007.thepit.listeners;

import java.util.HashMap;
import java.util.UUID;

import com.buha007.thepit.equipment.Equipment;
import com.buha007.thepit.main.Main;
import com.buha007.thepit.playerData.Data;
import com.buha007.thepit.playerData.PlayerData;
import com.buha007.thepit.pvpmanager.PvPManager;
import com.buha007.thepit.pvpmanager.Status;
import com.buha007.thepit.tab.Tab;
import com.buha007.thepit.utilities.Msg;
import com.buha007.thepit.utilities.Rewards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.md_5.bungee.api.ChatColor;

public class Death implements Listener {

    public Death() {
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        UUID victimID = victim.getUniqueId();
        PlayerData victimData = Data.get(victimID);
        HashMap<UUID, Integer> assistsPercentages = victimData.getAssists();

        // <KILLER>
        Player killer = e.getEntity().getKiller();
        if (killer instanceof Player) {
            UUID killerID = killer.getUniqueId();
            assistsPercentages.remove(killerID);
            PlayerData killerData = Data.get(killerID);
            Rewards.dropArmor(killerData, victim.getInventory(), victim.getWorld(), victim.getLocation());
            solveKiller(killer, killerID, killerData, victim, victimData);
        } else if (assistsPercentages.containsValue(100)) {
            for (UUID killerID : assistsPercentages.keySet()) {
                killer = Bukkit.getPlayer(killerID);
                solveKiller(killer, killerID, Data.get(killerID), victim, victimData);
                break;
            }
            assistsPercentages.clear();
        }

        // <ASSISTS>
        String message = Msg.ASSIST;
        message = message.replace("{player}", victimData.getLevelPrefix() + " " + victim.getDisplayName());
        for (UUID playerID : assistsPercentages.keySet()) {
            Player player = Bukkit.getPlayer(playerID);
            if (!(player instanceof Player))
                continue;
            // <get data>
            PlayerData playerData = Data.get(playerID);
            int percentage = assistsPercentages.get(playerID);
            // <add gold>
            double goldAssist = Rewards.getGoldAssist(victimData, percentage);
            playerData.setGold(playerData.getGold() + goldAssist);
            // <add xp>
            double xpAssist = Rewards.getXpAssist(victimData, percentage);
            addXP(player, playerData, xpAssist);
            // <send message>
            String copyMessage = message;
            copyMessage = copyMessage.replace("{xp-amount}", "" + xpAssist);
            copyMessage = copyMessage.replace("{gold-amount}", "" + goldAssist);
            copyMessage = copyMessage.replace("{percentage}", "" + percentage);
            player.sendMessage(copyMessage);
        }

        // <VICTIM>
        // <deal with bounty and scoreboard>
        final boolean isInCombat = PvPManager.isInCombat(victimID);
        if (victimData.isBountied()) {
            victimData.setStatus(Status.BOUNTIED, Status.IDLING);
            Tab.setSuffix(victim, "");
        } else if (isInCombat)
            victimData.setStatus(Status.FIGHTING, Status.IDLING);
        // <turn off combat>
        if (isInCombat)
            PvPManager.turnOffCombat(victim.getUniqueId());
        // <equip, reset -> streak, assist uuids, damage received>
        victim.getInventory().clear();
        Equipment.equip(victim, victimData);
        victimData.resetStreak();
        victimData.clearAssists();
        victimData.resetDamageReceived();
        // <respawn>
        victim.spigot().respawn();
    }

    /**
     * Drop armor; <br>
     * Give golden apple; <br>
     * Add gold + bounty; <br>
     * Add XP; Message kill; <br>
     * Broadcast bounty claim;
     */
    private void solveKiller(Player killer, UUID killerID, PlayerData killerData, Player victim,
            PlayerData victimData) {
        Rewards.giveGoldenApple(killer, killerData);

        int newStreak = killerData.getStreak() + 1;
        if (newStreak >= Main.MIN_STREAK_TO_BE_BOUNTIED) {
            killerData.updateBounty();
            if (killerData.isBountied()) {
                if (newStreak == Main.MIN_STREAK_TO_BE_BOUNTIED) {
                    String bountyMessage = Msg.BOUNTY;
                    bountyMessage = bountyMessage.replace("{value}", "" + killerData.getBounty());
                    bountyMessage = bountyMessage.replace("{player}",
                            killerData.getLevelPrefix() + " " + killer.getDisplayName());
                }
                Tab.setSuffix(killer, ChatColor.GOLD + " " + ChatColor.BOLD + killerData.getBounty() + "g");
            }
            killerData.setStatus(Status.FIGHTING, Status.BOUNTIED);
        }

        if (newStreak % 5 == 0) {
            String streakMessage = Msg.STREAK;
            streakMessage = streakMessage.replace("{number}", "" + newStreak);
            streakMessage = streakMessage.replace("{player}",
                    killerData.getLevelPrefix() + " " + killer.getDisplayName());
            Bukkit.broadcastMessage(streakMessage);
        }

        killerData.setStreak(newStreak);

        // <Gold + Bounty Claim>
        double goldReward = Rewards.getGold(victimData);
        int bountyReward = victimData.getBounty();
        double newGold = killerData.getGold() + goldReward + bountyReward;
        killerData.setGold(newGold);

        // <XP>
        double xpReward = Rewards.getXP(victimData);
        addXP(killer, killerData, xpReward);

        // <Messages>
        String message = Msg.getKillMessage(newStreak);
        message = message.replace("{player}", victimData.getLevelPrefix() + " " + victim.getDisplayName());
        message = message.replace("{xp-amount}", "" + xpReward);
        message = message.replace("{gold-amount}", "" + goldReward);
        killer.sendMessage(message);

        if (bountyReward != 0) {
            message = Msg.BOUNTY_CLAIMED;
            message = message.replace("{killer}", killerData.getLevelPrefix() + " " + killer.getDisplayName());
            message = message.replace("{victim}", victimData.getLevelPrefix() + " " + victim.getDisplayName());
            message = message.replace("{bounty}", "" + bountyReward);
            Bukkit.broadcastMessage(message);
        }
    }

    private void addXP(Player player, PlayerData playerData, double XP) {
        if (playerData.getLevel() != 120) {
            double newNeededXP = playerData.getNeededXP() - XP;
            if (newNeededXP <= 0) {
                String oldLevelPrefix = playerData.getLevelPrefix();
                playerData.setNeededXP(0);
                Msg.titleChatLevelUp(player, oldLevelPrefix, playerData.getLevelPrefix());
                Tab.setPrefix(player, playerData.getLevelPrefix());
            } else
                playerData.setNeededXP(newNeededXP);
        }
    }

}
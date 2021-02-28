package com.buha007.thepit.pvpmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.buha007.thepit.main.Main;
import com.buha007.thepit.playerData.Data;
import com.buha007.thepit.playerData.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PvPManager {
    private static HashMap<UUID, Long> combatStartTime = new HashMap<UUID, Long>();
    private static int combatTimeMillis;
    private static int checkInterval;
    private static BukkitTask checkTask;

    public static void init(Main instance) {
        reload();
        PluginManager pm = instance.getServer().getPluginManager();
        pm.registerEvents(new PlayerHurtEvent(), instance);
        pm.registerEvents(new CommandBlocker(), instance);
    }

    public static void reload() {
        combatTimeMillis = Main.getPlugin().getConfig().getInt("combatTime") * 1000;
        checkInterval = Main.getPlugin().getConfig().getInt("combatCheckInterval");
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
        checkTask = new CombatChecker().runTaskTimerAsynchronously(Main.getPlugin(), checkInterval, checkInterval);
    }

    /**
     * @deprecated
     */
    public static boolean isInCombat(Player player) {
        return combatStartTime.containsKey(player.getUniqueId());
    }

    public static boolean isInCombat(UUID playerID) {
        return combatStartTime.containsKey(playerID);
    }

    /**
     * Check if victim != attacker; <br>
     * Update scoreboard for both; <br>
     * Put both in internal HashMap; <br>
     * Disable fly for both;
     */
    public static void turnOnCombat(Player victim, Player attacker, double damageReceivedByVictim) {
        UUID victimID = victim.getUniqueId();
        UUID attackerID = attacker.getUniqueId();

        if (victimID.compareTo(attackerID) == 0)
            return;

        long currentTime = System.currentTimeMillis();

        PlayerData victimData = Data.get(victimID);
        PlayerData attackerInfo = Data.get(attackerID);

        if (combatStartTime.containsKey(victimID)) {
            if (!victimData.isBountied())
                victimData.setStatus(Status.IDLING, Status.FIGHTING);
            victim.setFlying(false);
            victim.setAllowFlight(false);
            victimData.increaseDamage(attackerID, currentTime, damageReceivedByVictim);
        }

        if (combatStartTime.containsKey(attackerID)) {
            if (!attackerInfo.isBountied())
                attackerInfo.setStatus(Status.IDLING, Status.FIGHTING);
            attacker.setFlying(false);
            attacker.setAllowFlight(false);
        }

        combatStartTime.put(victimID, currentTime);
        combatStartTime.put(attackerID, currentTime);
    }

    /**
     * Remove given player from internal HashMap
     */
    public static void turnOffCombat(UUID playerID) {
        combatStartTime.remove(playerID);
    }

    public static class CombatChecker extends BukkitRunnable {
        /**
         * Check if player's combat has ended; <br>
         * If ended, add player's uuid to an array list in order to remove it; <br>
         * Removal can't be done during iterating due to HashMap limitations
         */
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            ArrayList<UUID> uuidsToRemove = new ArrayList<UUID>();
            for (UUID uuid : PvPManager.combatStartTime.keySet()) {
                if (PvPManager.combatStartTime.get(uuid) + combatTimeMillis <= currentTime) {
                    uuidsToRemove.add(uuid);
                }
            }
            for (UUID playerID : uuidsToRemove) {
                // Player player = Bukkit.getPlayer(playerID);
                PvPManager.combatStartTime.remove(playerID);
                PlayerData playerData = Data.get(playerID);
                if (!playerData.isBountied()) {
                    playerData.setStatus(Status.FIGHTING, Status.IDLING);
                    // Scoreboard scoreboard = player.getScoreboard();
                    // Board.setStatus(scoreboard, scoreboard.getObjective(Board.OBJECTIVE),
                    // Status.FIGHTING,Status.IDLING);
                }
            }
        }
    }

    private PvPManager() {
    }
}
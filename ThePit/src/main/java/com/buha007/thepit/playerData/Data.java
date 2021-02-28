package com.buha007.thepit.playerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.buha007.thepit.main.Main;
import com.buha007.thepit.utilities.Msg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Data {
    private static HashMap<UUID, PlayerData> data;
    private static int saveInterval;
    private static BukkitTask saveTask;
    private static boolean saving;

    /**
     * Start data saver; <br>
     * Load online players data;
     */
    public static void reload() {
        data = new HashMap<UUID, PlayerData>();
        data.clear();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            PlayerData playerData = new PlayerData(player);
            PlayerData.createScoreboard(player, playerData);
            data.put(player.getUniqueId(), playerData);
        }

        saving = false;
        saveInterval = Main.getPlugin().getConfig().getInt("dataSaveInterval");
        if (saveTask != null) {
            saveTask.cancel();
            saveTask = null;
        }
        saveTask = new DataSaver().runTaskTimerAsynchronously(Main.getPlugin(), saveInterval, saveInterval);
    }

    /**
     * Check if given player's info is loaded
     */
    public static boolean contains(UUID playerID) {
        return data.containsKey(playerID);
    }

    /**
     * Use to put given player's info into internal HashMap <br>
     */
    public static void put(UUID playerID, PlayerData playerData) {
        data.put(playerID, playerData);
    }

    /**
     * @return player's info
     */
    public static PlayerData get(UUID playerID) {
        return data.get(playerID);
    }

    /**
     * Erase given player's data
     */
    public static void remove(UUID playerID) {
        data.remove(playerID);
    }

    /**
     * Used to save data into files
     */
    public static void save() {
        Msg.printConsole("&8[&eThePit&8] &7Saving players data&8!");
        saving = true;
        ArrayList<UUID> uuidToRemove = new ArrayList<UUID>();
        for (UUID uuid : data.keySet()) {
            data.get(uuid).saveData();
            Player player = Bukkit.getPlayer(uuid);
            if (!(player instanceof Player))
                uuidToRemove.add(uuid);
        }
        for (UUID uuid : uuidToRemove)
            data.remove(uuid);
        saving = false;
        Msg.printConsole("&8[&eThePit&8] &7Players data saved&8!");
    }

    public static class DataSaver extends BukkitRunnable {
        @Override
        public void run() {
            save();
        }
    }

    public static boolean isSaving() {
        return saving;
    }

    private Data() {
    }
}
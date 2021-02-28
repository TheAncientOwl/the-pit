package com.buha007.thepit.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.buha007.thepit.commands.ThePitCommand;
import com.buha007.thepit.equipment.Unbreakable;
import com.buha007.thepit.listeners.Chat;
import com.buha007.thepit.listeners.Death;
import com.buha007.thepit.listeners.Join;
import com.buha007.thepit.listeners.Pickup;
import com.buha007.thepit.listeners.Quit;
import com.buha007.thepit.playerData.Data;
import com.buha007.thepit.pvpmanager.PvPManager;
import com.buha007.thepit.tab.Tab;
import com.buha007.thepit.utilities.ConfigAccessor;
import com.buha007.thepit.utilities.Msg;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

/**
 * @author Bufnita
 */
public class Main extends JavaPlugin {

    private ConfigAccessor config;
    private static Main instance;
    public static final int MAX_CURRENCY = 9999999;
    public static int ARMOR_DROP_CHANCE = 100;
    public static int MIN_STREAK_TO_BE_BOUNTIED = 5;
    public static int CHANCE_TO_BE_BOUNTIED = 40;
    public static String TAB_MESSAGE_PREFIX = "tab";
    public static String DATE;

    @Override
    public void onEnable() {
        instance = this;

        config = new ConfigAccessor(this, "config.yml");
        File dataFolder = new File(getDataFolder(), "data");
        if (!dataFolder.exists())
            dataFolder.mkdirs();

        this.loadConstants();
        Msg.init(this);
        Data.reload();
        PvPManager.init(this);
        Tab.load(this);
        Unbreakable.createItems();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new Join(), this);
        pm.registerEvents(new Quit(), this);
        pm.registerEvents(new Chat(), this);
        pm.registerEvents(new Death(), this);
        pm.registerEvents(new Pickup(), this);

        getCommand("thepit").setExecutor(new ThePitCommand(this));

        Msg.printConsole("&6___       _    __");
        Msg.printConsole("&6 |  |__  |_|  |__) * _|_  &eThePit v1.2.0");
        Msg.printConsole("&6 |  |  | |__  |    |  |_  &7Developed by Bufnita");
        Msg.printConsole("&6 ");
    }

    public void loadConstants() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        DATE = ChatColor.GRAY + format.format(now).toString();
        FileConfiguration cfg = config.getConfig();
        MIN_STREAK_TO_BE_BOUNTIED = cfg.getInt("minStreakToBeBountied");
        CHANCE_TO_BE_BOUNTIED = cfg.getInt("chanceToBeBountied");
        ARMOR_DROP_CHANCE = cfg.getInt("armorDropChance");
        TAB_MESSAGE_PREFIX = cfg.getString("tabMessagePrefix");
    }

    @Override
    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    @Override
    public void reloadConfig() {
        config.reloadConfig();
    }

    @Override
    public void saveConfig() {
        config.saveConfig();
    }

    public static Main getPlugin() {
        return instance;
    }

    @Override
    public void onDisable() {
        Data.save();
        Msg.printConsole("&8[&eThePit&8] &7Goodbye&8!");
    }

}
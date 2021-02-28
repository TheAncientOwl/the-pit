package com.buha007.thepit.playerData;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.buha007.thepit.main.Main;
import com.buha007.thepit.pvpmanager.PvPManager;
import com.buha007.thepit.pvpmanager.Status;
import com.buha007.thepit.utilities.Msg;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;

public class PlayerData {
    private File dataFile;
    private Scoreboard scoreboard;
    private Objective objective;
    private HashMap<UUID, Assist> assists;
    private double gold, neededXP, damageReceived;
    private int level, prestige, renown, streak, bounty;
    private String prefix;

    /**
     * Create a playerData object from a given player; <br>
     * Load data from player's config file (if exists, if not, create a new one);
     */
    public PlayerData(Player player) {
        dataFile = new File(Main.getPlugin().getDataFolder() + File.separator + "data",
                player.getUniqueId().toString() + ".yml");
        if (dataFile.exists())
            loadData();
        else
            createData();
        updateLevelPrefix();
    }

    // ################### //
    // GETTERS AND SETTERS //
    // ################### //

    // ==============================================================================================================
    public int getBounty() {
        return bounty;
    }

    public boolean isBountied() {
        return bounty != 0;
    }

    /**
     * If bounty is 0 use resetBounty();
     */
    public void setBounty(int newBounty) {
        scoreboard.resetScores(ChatColor.WHITE + "Bounty: " + ChatColor.GOLD + this.bounty + "g");
        this.bounty = newBounty;
        setLine(3, ChatColor.WHITE + "Bounty: " + ChatColor.GOLD + this.bounty + "g");
    }

    public void resetBounty() {
        scoreboard.resetScores(ChatColor.WHITE + "Bounty: " + ChatColor.GOLD + this.bounty + "g");
        this.bounty = 0;
    }

    public void updateBounty() {
        if (this.bounty == 0) {
            if (streak >= Main.MIN_STREAK_TO_BE_BOUNTIED) {
                Random random = new Random();
                if (random.nextInt(101) <= Main.CHANCE_TO_BE_BOUNTIED) {
                    this.bounty = this.streak / 5 * 100;
                    setLine(3, ChatColor.WHITE + "Bounty: " + ChatColor.GOLD + this.bounty + "g");
                } else
                    return;
            }
        } else {
            scoreboard.resetScores(ChatColor.WHITE + "Bounty: " + ChatColor.GOLD + this.bounty + "g");
            this.bounty = this.streak / 5 * 100;
            setLine(3, ChatColor.WHITE + "Bounty: " + ChatColor.GOLD + this.bounty + "g");
        }
    }

    // ==============================================================================================================
    /**
     * Returns HashMap<UUID, Integer> with uuids of players that <br>
     * dealt damage with max 30 seconds before player's death and damage dealth <br>
     * (30s = 30000 ms)
     */
    public HashMap<UUID, Integer> getAssists() {
        Long currentTime = System.currentTimeMillis() - 30000;
        HashMap<UUID, Integer> result = new HashMap<UUID, Integer>();
        for (UUID playerID : assists.keySet()) {
            Assist assist = assists.get(playerID);
            if (assist.getTimeStarted() >= currentTime)
                result.put(playerID, new Integer((int) (assist.getDamageDealt() * 100 / damageReceived)));
        }
        return result;
    }

    public void clearAssists() {
        this.assists.clear();
    }

    /**
     * Increase damage dealt from attacker to this player <br>
     * Also increase damage received in total by this player;
     */
    public void increaseDamage(UUID playerID, Long moment, double damage) {
        damageReceived += damage;
        if (assists.containsKey(playerID)) {
            Assist assist = assists.get(playerID);
            assist.setTimeStarted(moment);
            assist.increaseDamageDealt(damage);
            assists.put(playerID, assist);
        } else {
            Assist assist = new Assist(moment, damage);
            assists.put(playerID, assist);
        }
    }

    public void resetDamageReceived() {
        this.damageReceived = 0;
    }

    // ==============================================================================================================
    public double getGold() {
        return this.gold;
    }

    /**
     * Set player's gold; <br>
     * Update scoreboard;
     */
    public void setGold(double newGold) {
        scoreboard.resetScores(ChatColor.WHITE + "Gold: " + ChatColor.GOLD + this.gold + "g");
        setLine(6, ChatColor.WHITE + "Gold: " + ChatColor.GOLD + newGold + "g");
        this.gold = newGold;
    }

    // ==============================================================================================================
    public double getNeededXP() {
        return neededXP;
    }

    /**
     * Set player's needed XP; <br>
     * If needed XP == 0 then levelUp; <br>
     * Update scoreboard;
     */
    public void setNeededXP(double newNeededXP) {
        scoreboard.resetScores(ChatColor.WHITE + "Needed XP: " + ChatColor.AQUA + this.neededXP);
        if (newNeededXP != 0) {
            setLine(8, ChatColor.WHITE + "Needed XP: " + ChatColor.AQUA + newNeededXP);
            this.neededXP = newNeededXP;
        } else {
            if (this.level != 120)
                setLevel(this.level + 1);
            else {
                setLine(8, ChatColor.WHITE + "XP: " + ChatColor.AQUA + "MAXED!");
                this.neededXP = newNeededXP;
            }

        }
    }

    // ==============================================================================================================
    public int getPrestige() {
        return this.prestige;
    }

    /**
     * Set player's prestige; <br>
     * Reset player's level to 1; <br>
     * Reset player's needed XP; <br>
     * Update scoreboard;
     */
    public void setPrestige(Player player, int newPrestige) {
        this.neededXP = getNeededXpToBypassLevel(1, newPrestige);
        this.level = 1;
        this.prestige = newPrestige;
        updateLevelPrefix();
        createScoreboard(player, this);
    }

    // ==============================================================================================================
    public int getStreak() {
        return this.streak;
    }

    /**
     * Set player's streak; <br>
     * If newStreak is 0 use resetStreak(); <br>
     * Update scoreboard;
     */
    public void setStreak(int newStreak) {
        scoreboard.resetScores(ChatColor.WHITE + "Streak: " + ChatColor.GREEN + this.streak);
        setLine(3, ChatColor.WHITE + "Streak: " + ChatColor.GREEN + newStreak);
        this.streak = newStreak;
    }

    public void resetStreak() {
        scoreboard.resetScores(ChatColor.WHITE + "Streak: " + ChatColor.GREEN + this.streak);
        this.streak = 0;
    }

    // ==============================================================================================================
    public int getLevel() {
        return this.level;
    }

    /**
     * Set player's level; <br>
     * Update player's prefix; <br>
     * Update scoreboard;
     */
    public void setLevel(int newLevel) {
        scoreboard.resetScores(ChatColor.WHITE + "Level: " + this.prefix);
        this.level = newLevel;
        updateLevelPrefix();
        setLine(9, ChatColor.WHITE + "Level: " + this.prefix);
        setNeededXP(getNeededXpToBypassLevel(newLevel, this.prestige));
    }

    public String getLevelPrefix() {
        return this.prefix;
    }

    private void updateLevelPrefix() {
        String prestigeColor = getPrestigeColor();
        this.prefix = ChatColor.translateAlternateColorCodes('&',
                prestigeColor + "[" + getLevelColor() + this.level + prestigeColor + "]");
    }

    // ==============================================================================================================
    public int getRenown() {
        return this.renown;
    }

    public void setRenown(int newRenown) {
        this.renown = newRenown;
    }

    // ==============================================================================================================
    public void setStatus(String oldStatus, String newStatus) {
        scoreboard.resetScores(oldStatus);
        setLine(4, newStatus);
    }

    // ==============================================================================================================

    // ################# //
    // CREATE SCOREBOARD //
    // ################# //
    public static void createScoreboard(Player player, PlayerData playerData) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        playerData.scoreboard = manager.getNewScoreboard();
        playerData.objective = playerData.scoreboard.registerNewObjective("thepit", "dummy");
        playerData.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        playerData.objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "THE ATLANTIS PIT");

        playerData.setLine(1, ChatColor.YELLOW + "mc.atlantis-nt.com");
        playerData.setLine(2, ChatColor.WHITE + " ");

        if (playerData.streak > 1) {
            playerData.setLine(3, ChatColor.WHITE + "Streak: " + ChatColor.GREEN + playerData.streak);
            if (playerData.isBountied())
                playerData.setLine(3, ChatColor.WHITE + "Bounty: " + ChatColor.GOLD + playerData.bounty + "g");
        }

        if (playerData.isBountied())
            playerData.setLine(4, ChatColor.WHITE + "Status: " + Status.BOUNTIED);
        else if (PvPManager.isInCombat(player.getUniqueId()))
            playerData.setLine(4, ChatColor.WHITE + "Status: " + Status.FIGHTING);
        else
            playerData.setLine(4, ChatColor.WHITE + "Status: " + Status.IDLING);

        playerData.setLine(5, ChatColor.YELLOW + " ");
        playerData.setLine(6, ChatColor.WHITE + "Gold: " + ChatColor.GOLD + playerData.gold + "g");
        playerData.setLine(7, ChatColor.GREEN + " ");
        playerData.setLine(8, ChatColor.WHITE + "Needed XP: " + ChatColor.AQUA + playerData.neededXP);
        playerData.setLine(9, ChatColor.WHITE + "Level: " + playerData.prefix);

        if (playerData.prestige > 0) {
            playerData.setLine(10,
                    ChatColor.WHITE + "Prestige: " + ChatColor.YELLOW + numeralToRoman(playerData.prestige));
            playerData.setLine(11, ChatColor.BLACK + " ");
            playerData.setLine(12, Main.DATE);
        } else {
            playerData.setLine(10, ChatColor.BLACK + " ");
            playerData.setLine(11, Main.DATE);
        }

        player.setScoreboard(playerData.scoreboard);
    }

    public static void clearScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        player.setScoreboard(scoreboard);
    }

    // ################### //
    // PLAYER DATA SECTION //
    // ################### //
    private void loadData() {
        final FileConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        this.gold = cfg.getDouble("gold");
        this.neededXP = cfg.getDouble("neededXP");
        this.level = cfg.getInt("level");
        this.prestige = cfg.getInt("prestige");
        this.renown = cfg.getInt("renown");
        this.streak = 0;
        this.bounty = 0;
        this.damageReceived = 0;
        this.assists = new HashMap<UUID, Assist>();
    }

    private void createData() {
        Msg.printConsole("&8[&eThePit&8] &6Creating new data file &e" + dataFile + "&8!");
        try {
            dataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        cfg.set("gold", 0.0);
        gold = 0;
        cfg.set("level", 1);
        level = 1;
        cfg.set("neededXP", 15);
        neededXP = 15;
        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        cfg.set("gold", gold);
        cfg.set("level", level);
        cfg.set("neededXP", neededXP);
        if (prestige != 0)
            cfg.set("prestige", prestige);
        if (renown != 0)
            cfg.set("renown", renown);
        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ############ //
    // ASSIST CLASS //
    // ############ //
    private class Assist {
        private Long timeStarted;
        private double damageDealt;

        public Assist(Long timeStarted, double damageDealt) {
            this.timeStarted = timeStarted;
            this.damageDealt = damageDealt;
        }

        public Long getTimeStarted() {
            return timeStarted;
        }

        public void setTimeStarted(Long newTimeStarted) {
            timeStarted = newTimeStarted;
        }

        public double getDamageDealt() {
            return damageDealt;
        }

        public void increaseDamageDealt(double value) {
            damageDealt += value;
        }
    }

    // ################# //
    // PRIVATE FUNCTIONS //
    // ################# //

    /**
     * -> Levels XP needed: <br>
     * 1-9 -> 15 XP <br>
     * 10-19 -> 30 XP <br>
     * 20-29 -> 50 XP <br>
     * 30-39 -> 75 XP <br>
     * 40-49 -> 125 XP <br>
     * 50-59 -> 250 XP <br>
     * 60-69 -> 600 XP <br>
     * 70-79 -> 800 XP <br>
     * 80-89 -> 900 XP <br>
     * 90-99 -> 1000 XP <br>
     * 100-109 -> 1200 XP <br>
     * 110-120 -> 1500 XP <br>
     * 
     * -> Leveling is different once you've prestiged; <br>
     * You need 10% more XP each time you prestige
     */
    private int getNeededXpToBypassLevel(int level, int prestige) {
        float percentage = prestige * 0.1f;
        if (level < 10)
            return Math.round(percentage * 15 + 15);
        if (level < 20)
            return Math.round(percentage * 30 + 30);
        if (level < 30)
            return Math.round(percentage * 50 + 50);
        if (level < 40)
            return Math.round(percentage * 75 + 75);
        if (level < 50)
            return Math.round(percentage * 125 + 125);
        if (level < 60)
            return Math.round(percentage * 250 + 250);
        if (level < 70)
            return Math.round(percentage * 600 + 600);
        if (level < 80)
            return Math.round(percentage * 800 + 800);
        if (level < 90)
            return Math.round(percentage * 900 + 900);
        if (level < 100)
            return Math.round(percentage * 1000 + 1000);
        if (level < 110)
            return Math.round(percentage * 1200 + 1200);
        if (level < 120)
            return Math.round(percentage * 1500 + 1500);
        return 0;
    }

    private final String getPrestigeColor() {
        if (prestige == 0)
            return "&7";
        if (prestige <= 4)
            return "&9";
        if (prestige <= 9)
            return "&e";
        if (prestige <= 14)
            return "&6";
        if (prestige <= 19)
            return "&c";
        if (prestige <= 24)
            return "&5";
        if (prestige <= 29)
            return "&4";
        return "&d";
    }

    private final String getLevelColor() {
        if (level <= 9)
            return "&7";
        if (level <= 19)
            return "&1";
        if (level <= 29)
            return "&3";
        if (level <= 39)
            return "&2";
        if (level <= 49)
            return "&a";
        if (level <= 59)
            return "&e";
        if (level <= 69)
            return "&6&l";
        if (level <= 79)
            return "&c&l";
        if (level <= 89)
            return "&4&l";
        if (level <= 99)
            return "&5&l";
        if (level <= 109)
            return "&d&l";
        if (level <= 119)
            return "&f&l";
        return "&b&l";
    }

    private void setLine(int line, String string) {
        Score score = objective.getScore(string);
        score.setScore(line);
    }

    private static String numeralToRoman(int number) {
        if (number == 0)
            return "";
        if (number >= 30)
            return "MAX";
        List<String> numerals = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII",
                "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXV",
                "XXVII", "XXVIII", "XXIX");
        return numerals.get(number - 1);
    }

}
package com.buha007.thepit.utilities;

import java.util.Random;

import com.buha007.thepit.main.Main;
import com.buha007.thepit.playerData.PlayerData;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Rewards {
    public static double getGold(PlayerData victimData) {
        return 5;
    }

    public static int getXP(PlayerData victimData) {
        return 5;
    }

    public static double getGoldAssist(PlayerData victimData, int percentage) {
        return (getGold(victimData) * percentage) / 100;
    }

    public static double getXpAssist(PlayerData victimData, int percentage) {
        return (getXP(victimData) * percentage) / 100;
    }

    public static void giveGoldenApple(Player player, PlayerData playerData) {
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
    }

    public static void dropArmor(PlayerData killerData, PlayerInventory victimInventory, World world,
            Location location) {
        Random random = new Random();
        int chance = random.nextInt(101);
        if (chance > Main.ARMOR_DROP_CHANCE)
            return;

        ItemStack[] armor = victimInventory.getArmorContents();
        for (ItemStack piece : armor) {
            if (piece.getType().toString().contains("IRON")) {
                world.dropItem(location, piece);
                return;
            }
        }
    }

    private Rewards() {
    }
}
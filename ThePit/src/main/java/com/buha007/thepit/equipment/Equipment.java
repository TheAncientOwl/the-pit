package com.buha007.thepit.equipment;

import java.util.Random;

import com.buha007.thepit.playerData.PlayerData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Equipment {

    public static void equip(Player player, PlayerData playerData) {
        PlayerInventory inv = player.getInventory();

        inv.setHelmet(new ItemStack(Material.AIR));

        Random random = new Random();
        int chance = random.nextInt(3);

        if (chance == 2) {// iron chestplate, chain leggings, chain boots
            inv.setChestplate(Unbreakable.IRON_CHESTPLATE);
            inv.setLeggings(Unbreakable.CHAINMAIL_LEGGINGS);
            inv.setBoots(Unbreakable.CHAINMAIL_BOOTS);
        } else if (chance == 1) {// chain chestplate, iron leggings, chain boots
            inv.setChestplate(Unbreakable.CHAINMAIL_CHESTPLATE);
            inv.setLeggings(Unbreakable.IRON_LEGGINGS);
            inv.setBoots(Unbreakable.CHAINMAIL_BOOTS);
        } else {// chain chestplate, chain leggings, iron boots
            inv.setChestplate(Unbreakable.CHAINMAIL_CHESTPLATE);
            inv.setLeggings(Unbreakable.CHAINMAIL_LEGGINGS);
            inv.setBoots(Unbreakable.IRON_BOOTS);
        }

        inv.setItem(0, Unbreakable.IRON_SWORD);
        inv.setItem(1, Unbreakable.BOW);
        inv.setItem(8, getArrows());
    }

    private static final ItemStack getArrows() {
        ItemStack arrows = new ItemStack(Material.ARROW);
        arrows.setAmount(32);
        return arrows;
    }

    private Equipment() {
    }
}
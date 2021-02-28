package com.buha007.thepit.equipment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Unbreakable {
    protected static ItemStack CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS;
    protected static ItemStack IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS;
    protected static ItemStack DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS;
    protected static ItemStack IRON_SWORD, DIAMOND_SWORD, BOW;

    public static void createItems() {
        // <chainmail>
        CHAINMAIL_HELMET = unbreakable(Material.CHAINMAIL_HELMET);
        CHAINMAIL_CHESTPLATE = unbreakable(Material.CHAINMAIL_CHESTPLATE);
        CHAINMAIL_LEGGINGS = unbreakable(Material.CHAINMAIL_LEGGINGS);
        CHAINMAIL_BOOTS = unbreakable(Material.CHAINMAIL_BOOTS);

        // <iron>
        IRON_HELMET = unbreakable(Material.IRON_HELMET);
        IRON_CHESTPLATE = unbreakable(Material.IRON_CHESTPLATE);
        IRON_LEGGINGS = unbreakable(Material.IRON_LEGGINGS);
        IRON_BOOTS = unbreakable(Material.IRON_BOOTS);

        // <diamond>
        DIAMOND_HELMET = unbreakable(Material.DIAMOND_HELMET);
        DIAMOND_CHESTPLATE = unbreakable(Material.DIAMOND_CHESTPLATE);
        DIAMOND_LEGGINGS = unbreakable(Material.DIAMOND_LEGGINGS);
        DIAMOND_BOOTS = unbreakable(Material.DIAMOND_BOOTS);

        // <swords>
        IRON_SWORD = unbreakable(Material.IRON_SWORD);
        DIAMOND_SWORD = unbreakable(Material.DIAMOND_SWORD);
        BOW = unbreakable(Material.BOW);
    }

    protected static final ItemStack unbreakable(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }
}
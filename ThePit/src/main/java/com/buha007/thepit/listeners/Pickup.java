package com.buha007.thepit.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Pickup armor;
 */
public class Pickup implements Listener {

    public Pickup() {
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        Item item = e.getItem();
        ItemStack itemStack = item.getItemStack();
        String material = itemStack.getType().toString();

        // <diamond>
        if (material.contains("DIAMOND")) {
            // =================================================================================
            if (material.contains("HELMET")) {
                PlayerInventory inv = e.getPlayer().getInventory();
                Material armor;
                try {
                    armor = inv.getHelmet().getType();
                } catch (NullPointerException npe) {
                    armor = Material.STICK;
                }
                if (!armor.equals(Material.DIAMOND_HELMET)) {
                    item.remove();
                    e.setCancelled(true);
                    inv.setHelmet(itemStack);
                }
                return;
            }

            // =================================================================================
            if (material.contains("CHESTPLATE")) {
                PlayerInventory inv = e.getPlayer().getInventory();
                Material armor;
                try {
                    armor = inv.getChestplate().getType();
                } catch (NullPointerException npe) {
                    armor = Material.STICK;
                }
                if (!armor.equals(Material.DIAMOND_CHESTPLATE)) {
                    item.remove();
                    e.setCancelled(true);
                    inv.setChestplate(itemStack);
                }
                return;
            }

            // =================================================================================
            if (material.contains("LEGGINGS")) {
                PlayerInventory inv = e.getPlayer().getInventory();
                Material armor;
                try {
                    armor = inv.getLeggings().getType();
                } catch (NullPointerException npe) {
                    armor = Material.STICK;
                }
                if (!armor.equals(Material.DIAMOND_LEGGINGS)) {
                    item.remove();
                    e.setCancelled(true);
                    inv.setLeggings(itemStack);
                }
                return;
            }

            // =================================================================================
            if (material.contains("BOOTS")) {
                PlayerInventory inv = e.getPlayer().getInventory();
                Material armor;
                try {
                    armor = inv.getBoots().getType();
                } catch (NullPointerException npe) {
                    armor = Material.STICK;
                }
                if (!armor.equals(Material.DIAMOND_BOOTS)) {
                    item.remove();
                    e.setCancelled(true);
                    inv.setBoots(itemStack);
                }
                return;
            }

            return;
        }

        // <iron>
        if (material.contains("IRON")) {
            // =================================================================================
            if (material.contains("HELMET")) {
                PlayerInventory inv = e.getPlayer().getInventory();
                Material armor;
                try {
                    armor = inv.getHelmet().getType();
                } catch (NullPointerException npe) {
                    armor = Material.STICK;
                }

                if (!armor.equals(Material.IRON_HELMET) && !armor.equals(Material.DIAMOND_HELMET)) {
                    item.remove();
                    e.setCancelled(true);
                    inv.setHelmet(itemStack);
                    return;
                }
                if (inv.contains(itemStack)) {
                    e.setCancelled(true);
                }
                return;
            }

            // =================================================================================

            if (material.contains("CHESTPLATE")) {
                PlayerInventory inv = e.getPlayer().getInventory();
                Material armor;
                try {
                    armor = inv.getChestplate().getType();
                } catch (NullPointerException npe) {
                    armor = Material.STICK;
                }

                if (!armor.equals(Material.IRON_CHESTPLATE) && !armor.equals(Material.DIAMOND_CHESTPLATE)) {
                    item.remove();
                    e.setCancelled(true);
                    inv.setChestplate(itemStack);
                    return;
                }
                if (inv.contains(itemStack)) {
                    e.setCancelled(true);
                }
                return;
            }
            // =================================================================================

            if (material.contains("LEGGINGS")) {
                PlayerInventory inv = e.getPlayer().getInventory();
                Material armor;
                try {
                    armor = inv.getLeggings().getType();
                } catch (NullPointerException npe) {
                    armor = Material.STICK;
                }

                if (!armor.equals(Material.IRON_LEGGINGS) && !armor.equals(Material.DIAMOND_LEGGINGS)) {
                    item.remove();
                    e.setCancelled(true);
                    inv.setLeggings(itemStack);
                    return;
                }
                if (inv.contains(itemStack)) {
                    e.setCancelled(true);
                }
                return;
            }
            // =================================================================================

            if (material.contains("BOOTS")) {
                PlayerInventory inv = e.getPlayer().getInventory();
                Material armor;
                try {
                    armor = inv.getBoots().getType();
                } catch (NullPointerException npe) {
                    armor = Material.STICK;
                }

                if (!armor.equals(Material.IRON_BOOTS) && !armor.equals(Material.DIAMOND_BOOTS)) {
                    item.remove();
                    e.setCancelled(true);
                    inv.setBoots(itemStack);
                    return;
                }
                if (inv.contains(itemStack)) {
                    e.setCancelled(true);
                }
                return;
            }
            // =================================================================================

            return;
        }

    }

}
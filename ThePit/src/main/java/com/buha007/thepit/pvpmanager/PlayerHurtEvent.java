package com.buha007.thepit.pvpmanager;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerHurtEvent implements Listener {

    public PlayerHurtEvent() {
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;

        Entity victimEntity = e.getEntity();
        if (!(victimEntity instanceof Player))
            return;

        Entity damagerEntity = e.getDamager();
        Player damager;
        if (damagerEntity instanceof Player) {
            damager = (Player) damagerEntity;
        } else {
            if (damagerEntity instanceof Projectile) {
                Projectile projectile = (Projectile) damagerEntity;
                if (projectile.getShooter() instanceof Player) {
                    damager = (Player) projectile.getShooter();
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        PvPManager.turnOnCombat((Player) victimEntity, damager, e.getFinalDamage());
    }
}
package com.joiubas.prisongangs.utility.interaction;

import com.joiubas.prisongangs.utility.PrisonGang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class Events implements Listener {

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getType().equals(InventoryType.CHEST)) {
            if (e.getInventory().getTitle().equals(ChatColor.DARK_GRAY + "Gang Members")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak (BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (PrisonGang.inGang(p)) {
            PrisonGang pGang = PrisonGang.getGang(p);
            pGang.addXP(1);
        }
    }

}

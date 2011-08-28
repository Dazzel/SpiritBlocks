/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dazzel.spiritblocks.listeners;

import com.dazzel.spiritblocks.SpiritBlocks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class SBEntityListener extends EntityListener {    
    private SpiritBlocks plugin;

    public SBEntityListener(SpiritBlocks plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player) { 
            Player player = (Player) event.getEntity();
            Location loc = player.getLocation();
            
            if(!plugin.ps.hasSpirits(player)) return; 

            plugin.lastLoc.put(player, loc);            
        }
    }
}

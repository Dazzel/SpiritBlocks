package com.dazzel.spiritblocks.listeners;

import com.dazzel.spiritblocks.Constants;
import com.dazzel.spiritblocks.SpiritBlocks;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SBPlayerListener extends PlayerListener {    
    private final SpiritBlocks plugin;

    public SBPlayerListener(SpiritBlocks plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        int newSpirit;
        Player player = event.getPlayer();
        
        if(plugin.isInCommand(player)) {
            Location loc = event.getClickedBlock().getLocation();        
            
            newSpirit = plugin.ps.newSpirit(player, loc, plugin.spirits.get(player));
            plugin.setInCommand(player, null, false);
            // rest bei UserCommand getestet
            if(plugin.econEnabled && Constants.economyCreate) plugin.econ.getAccount(player.getName()).subtract(Constants.economyCreateCosts);
            if(newSpirit == 0) player.sendMessage(Constants.messagesSuccess);
            else if(newSpirit == 1) player.sendMessage(Constants.messagesUnallowedBlock);
            else if(newSpirit == 2) player.sendMessage(Constants.messagesNoSuccess);
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        double dif = 0, x = 0, y = 0, z = 0, xyzS, xyzP;
        String world = "";
        boolean firstRun = true;
        Location loc = plugin.lastLoc.get(player);
        
        plugin.lastLoc.remove(player);

        if(plugin.econEnabled && Constants.economyRespwan) {
            if(plugin.econ.hasAccount(player.getName())) {
                if(plugin.econ.getAccount(player.getName()).hasEnough(Constants.economyRespawnCosts)) {
                    plugin.econ.getAccount(player.getName()).subtract(Constants.economyRespawnCosts);
                }
                else {
                    player.sendMessage(Constants.messagesNoMoney);
                    return;
                }
            }
            else {
                player.sendMessage(Constants.messagesNoMoney);
                return;
            }
        }        
        
        ResultSet res = plugin.ps.getSpirits(player);   
        xyzP = loc.getX() + loc.getY() + loc.getZ();
        
        try {
            while(res.next()) {
                xyzS = res.getDouble("x") + res.getDouble("y") + res.getDouble("y");                
                if(firstRun || Math.abs(xyzP-xyzS) < dif) {
                    dif = Math.abs(xyzP-xyzS);
                    x = res.getDouble("x");
                    y = res.getDouble("y");
                    z = res.getDouble("z");
                    world = res.getString("world");
                    firstRun = false;
                }         
            }
        } catch (SQLException ex) {
            System.out.print(plugin.logPrefix + "Error: " + ex.getMessage());
        }        
        if(!firstRun) event.setRespawnLocation(new Location(plugin.getServer().getWorld(world), x, y, z));        
    }  
}

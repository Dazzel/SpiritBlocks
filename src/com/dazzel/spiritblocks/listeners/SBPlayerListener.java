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
        int newSpirit, newShrine;
        Player player = event.getPlayer();
        Location loc = event.getClickedBlock().getLocation();
        
        if(plugin.isInCommand(player)) {    
            newSpirit = plugin.ps.newSpirit(player, loc, plugin.spirits.get(player));
            plugin.setInCommand(player, null, false);
            // rest bei UserCommand getestet
            if(plugin.econEnabled && Constants.economyCreate) plugin.econ.getAccount(player.getName()).subtract(Constants.economyCreateCosts);
            if(newSpirit == 0) player.sendMessage(Constants.messagesSuccess);
            else if(newSpirit == 1) player.sendMessage(Constants.messagesUnallowedBlock);
            else if(newSpirit == 2) player.sendMessage(Constants.messagesNoSuccess);
            else if(newSpirit == 3) player.sendMessage(Constants.messagesNoShrine);
        }
        else if(plugin.isInAdminCommand(player)) {
            newShrine = plugin.sh.newShrine(loc, plugin.shrines.get(player));
            plugin.setInAdminCommand(player, null, false);
            if(newShrine == 0) player.sendMessage(Constants.messagesSuccess);
            else if(newShrine == 1) player.sendMessage(Constants.messagesUnallowedBlock);
            else if(newShrine == 2) player.sendMessage(Constants.messagesNoSuccess);            
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        double dif = 0, x = 0, y = 0, z = 0, xyzS, xyzP;
        int sID = 0;
        String world = "";
        boolean firstRun = true;
        Location loc = plugin.lastLoc.get(player);
        
        if(loc == null) return;
        
        plugin.lastLoc.remove(player);
        
        if(!player.hasPermission("spirit.respawn")) {
            player.sendMessage(Constants.messagesNoPermission);
            return;
        }
        
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
                if(Constants.shrineEnabled) {
                    sID = res.getInt("sID");
                    xyzS = plugin.sh.getX(sID) + plugin.sh.getY(sID) + plugin.sh.getZ(sID);
                    if(firstRun || Math.abs(xyzP-xyzS) < dif) {
                        dif = Math.abs(xyzP-xyzS);
                        x = plugin.sh.getX(sID);
                        y = plugin.sh.getY(sID);
                        z = plugin.sh.getZ(sID);
                        world = plugin.sh.getWorld(sID);
                        firstRun = false;                       
                    }
                }
                else {
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
        
            }
        } catch (SQLException ex) {
            System.out.print(plugin.logPrefix + "Error: " + ex.getMessage());
        }        
        if(!firstRun) event.setRespawnLocation(new Location(plugin.getServer().getWorld(world), x, y, z));
    }  
}

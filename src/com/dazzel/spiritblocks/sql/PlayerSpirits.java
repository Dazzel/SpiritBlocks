package com.dazzel.spiritblocks.sql;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.dazzel.spiritblocks.Constants;
import com.dazzel.spiritblocks.SpiritBlocks;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerSpirits {
    private final SpiritBlocks plugin;
    private sqlCore db;

    public PlayerSpirits(SpiritBlocks plugin) {
        this.plugin = plugin;  
    }
    
    public void init() {
        db = new sqlCore(plugin.log, plugin.logPrefix, "spiritblocks", plugin.folder.getPath());
        db.initialize();
        if(!db.checkTable("spirits")) {
            System.out.println(plugin.logPrefix + "Creating table for spirits...");
            String query = "CREATE TABLE 'spirits' "
                        + "('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                        + "'sID' INTEGER DEFAULT 0, "
                        + "'player' VARCHAR, "
                        + "'name' VARCHAR, "
                        + "'world' VARCHAR, "
                        + "'x' DOUBLE DEFAULT 0, "
                        + "'y' DOUBLE DEFAULT 0, "
                        + "'z' DOUBLE DEFAULT 0)";
            db.createTable(query);
        }
    }
    
    private boolean sameLocation(Player player, Location loc) {
        if(!hasSpirits(player)) return false;
        
        String query = "SELECT * FROM spirits "
                    + "WHERE player = %player% "
                    + "AND world = %world% "
                    + "AND x = %x% "
                    + "AND y = %y% "
                    + "AND z = %z%";
        if(Constants.shrineEnabled) {
            query = "SELECT * FROM spirits a " +
            		"LEFT JOIN shrines b ON a.sID = b.id " +
            		"WHERE player = %player% " +
            		"AND b.world = %world% " +
            		"AND b.x = %x% " +
            		"AND b.y = %y% " +
            		"AND b.z = %z%";
        }
        
        query = query.replaceAll("%player%", "'"+player.getName().toLowerCase()+"'");
        query = query.replaceAll("%world%", "'"+loc.getWorld().getName()+"'");
        query = query.replaceAll("%x%", "'"+loc.getBlockX()+"'");
        query = query.replaceAll("%y%", "'"+loc.getBlockY()+"'");
        query = query.replaceAll("%z%", "'"+loc.getBlockZ()+"'");
        
        ResultSet res = db.sqlQuery(query);
        try {
            if(res.next()) return true;
        } catch (SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        return false;
    }
    
    
    public boolean sameName(Player player, String name) {
        if(!hasSpirits(player)) return false;
        
        String query = "SELECT id FROM spirits "
                + "WHERE player = %player% "
                + "AND name = %name%";
        query = query.replaceAll("%player%", "'"+player.getName().toLowerCase()+"'");
        query = query.replaceAll("%name%", "'"+name+"'");
        
        ResultSet res = db.sqlQuery(query);
        try {
            if(res.next()) return true;
        } catch (SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }       
        
        return false;
    }
        
    
    private boolean allowedBlock(Location loc) {
        Block block = loc.getBlock();
        Material mat = block.getType();
        
        if(Constants.blockTypes.contains(mat.toString()) || 
                Constants.blockTypes.contains("ALL")) return true;        
        else return false;
    }
    
    private boolean overLimit(Player player) {
        int count = 0;
        String query = "SELECT COUNT(id) FROM spirits WHERE player = %player%";
        query = query.replaceAll("%player%", "'"+player.getName().toLowerCase()+"'");
        
        ResultSet res = db.sqlQuery(query);
        try {
            count = res.getInt(1);
        } catch (SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        if(Constants.maxSpirits != 0 && count > Constants.maxSpirits) return true;
        
        return false;
    }
    
    private int getShrine(Location loc) {
        int n = -1;
        String query ="SELECT id FROM shrines " +
        		"WHERE world = %world%" +
        		"AND x = %x% " +
        		"AND y = %y% " +
        		"AND z = %z%";
        query = query.replaceAll("%world%", "'"+loc.getWorld().getName()+"'");
        query = query.replaceAll("%x%", "'"+loc.getBlockX()+"'");
        query = query.replaceAll("%y%", "'"+loc.getBlockY()+"'");
        query = query.replaceAll("%z%", "'"+loc.getBlockZ()+"'");
        
        ResultSet res = db.sqlQuery(query);
        try {
            if(res.next()) return res.getInt(1);
        } catch(SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        return n;
    }
    
    private void deleteFirst(Player player) {   
        ResultSet res = getSpirits(player);
        try {
            res.next();
            int id = res.getInt("id");
            String query = "DELETE FROM spirits "
                    + "WHERE id = "+id+";";

            db.deleteQuery(query);            
        } catch (SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }        
    }
    
    public void deleteSpirit(Player player, String name) {
        String query = "DELETE FROM spirits "
                + "WHERE player = %player% "
                + "AND name = %name%;";
        query = query.replaceAll("%player%", "'"+player.getName().toLowerCase()+"'");
        query = query.replaceAll("%name%", "'"+name+"'");
        
        db.deleteQuery(query);
    }
    
    public int newSpirit(Player player, Location loc, String name) {
        if(!allowedBlock(loc)) return 1;
        else if(sameLocation(player, loc)) return 2;        
        else if(overLimit(player)) deleteFirst(player);        
        
        String query = "INSERT INTO spirits (player, name, world, x ,y, z) "
                + "VALUES ('"+player.getName().toLowerCase()+"', "
                + "'"+name+"', "
                + "'"+loc.getWorld().getName()+"', "
                + "'"+loc.getBlockX()+"', "
                + "'"+loc.getBlockY()+"', "
                + "'"+loc.getBlockZ()+"');";
        
        if(Constants.shrineEnabled) {
            if(getShrine(loc) == -1) return 3;
            query = "INSERT INTO spirits (sID, player, name, world) "
                    + "VALUES ('"+getShrine(loc)+"', "
                    + "'"+player.getName().toLowerCase()+"', "
                    + "'"+name+"', "
                    + "'"+loc.getWorld().getName()+"');";
        }
        
        db.insertQuery(query);
        
        return 0;
    }
    
    public ResultSet getSpirits(Player player) {
        String query = "SELECT * FROM spirits WHERE player = %player%";
        query = query.replaceAll("%player%", "'"+player.getName().toLowerCase()+"'");
        
        return db.sqlQuery(query);
    }
    
    public boolean hasSpirits(Player player) {       
        ResultSet res = this.getSpirits(player);
        try {
            if(res.next()) return true;
        } catch (SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        return false;
    }
}
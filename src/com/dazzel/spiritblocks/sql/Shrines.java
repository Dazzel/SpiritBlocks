package com.dazzel.spiritblocks.sql;

import com.dazzel.spiritblocks.Constants;
import com.dazzel.spiritblocks.SpiritBlocks;

import java.sql.ResultSet;
import java.sql.SQLException;

import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Shrines {
    private final SpiritBlocks plugin;
    private SQLite db;

    public Shrines(SpiritBlocks plugin) {
        this.plugin = plugin;  
    }
    
    public void init() {
        db = new SQLite(plugin.log, plugin.logPrefix, "spiritblocks", plugin.folder.getPath());
        db.open();
        if(!db.checkTable("shrines")) {
            System.out.println(plugin.logPrefix + "Creating table for shrines...");
            String query = "CREATE TABLE 'shrines' "
                        + "('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                        + "'name' VARCHAR, "
                        + "'world' VARCHAR, "
                        + "'x' DOUBLE, "
                        + "'y' DOUBLE, "
                        + "'z' DOUBLE)";
            db.createTable(query);
        }
    }
    
    private boolean sameLocation(Location loc) {
        
        String query = "SELECT * FROM shrines "
                    + "WHERE world = %world% "
                    + "AND x = %x% "
                    + "AND y = %y% "
                    + "AND z = %z%";
        query = query.replaceAll("%world%", "'"+loc.getWorld().getName()+"'");
        query = query.replaceAll("%x%", "'"+loc.getBlockX()+"'");
        query = query.replaceAll("%y%", "'"+loc.getBlockY()+"'");
        query = query.replaceAll("%z%", "'"+loc.getBlockZ()+"'");
        
        ResultSet res = db.query(query);
        try {
            if(res.next()) return true;
        } catch (SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        return false;
    }
    
    
    public boolean sameName(String name) {
        
        String query = "SELECT id FROM shrines "
                + "WHERE name = %name%";
        query = query.replaceAll("%name%", "'"+name+"'");
        
        ResultSet res = db.query(query);
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
        
        if(Constants.blockTypes.contains(mat.toString()) || Constants.blockTypes.contains("ALL")) return true;        
        else return false;
    }
    
    
    public void deleteShrine(String name) {
        String query = "DELETE FROM shrines "
                + "WHERE name = %name%;";
        query = query.replaceAll("%name%", "'"+name+"'");
        
        db.query(query);
    }
    
    public int newShrine(Location loc, String name) {
        if(!allowedBlock(loc)) return 1;
        else if(sameLocation( loc)) return 2;
        
        db.query("INSERT INTO shrines (name, world, x, y, z) " +
        		        "VALUES ('"+name+"', " +
        		        "'"+loc.getWorld().getName()+"', " +
        		        "'"+loc.getBlockX()+"', " +
        		        "'"+loc.getBlockY()+"', " +
        		        "'"+loc.getBlockZ()+"');");
        
        return 0;
    }
    
    public ResultSet getShrines() {
        String query = "SELECT * FROM shrines";
        
        return db.query(query);
    }
    
    public double getX(int id) {
        String query = "SELECT x FROM shrines WHERE id = "+id;
        ResultSet res = db.query(query);
        double n = -1.0;
        
        try {
            n = res.getDouble(1);
        } catch(SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        return n;
    }
    
    public double getY(int id) {
        String query = "SELECT y FROM shrines WHERE id = "+id;
        ResultSet res = db.query(query);
        double n = -1.0;
        
        try {
            n = res.getDouble(1);
        } catch(SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        return n;
    }  
    
    public double getZ(int id) {
        String query = "SELECT z FROM shrines WHERE id = "+id;
        ResultSet res = db.query(query);
        double n = -1.0;
        
        try {
            n = res.getDouble(1);
        } catch(SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        return n;
    }  
    
    public String getWorld(int id) {
        String query = "SELECT world FROM shrines WHERE id = "+id;
        ResultSet res = db.query(query);
        String n = null;
        
        try {
            n = res.getString(1);
        } catch(SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }
        
        return n;
    }     
    
}
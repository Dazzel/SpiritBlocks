package com.dazzel.spiritblocks;

import com.dazzel.spiritblocks.commands.AdminCommand;
import com.dazzel.spiritblocks.commands.UserCommand;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.dazzel.spiritblocks.listeners.SBPlayerListener;
import com.dazzel.spiritblocks.listeners.SBEntityListener;
import com.dazzel.spiritblocks.listeners.SBServerListener;
import com.dazzel.spiritblocks.sql.PlayerSpirits;
import com.dazzel.spiritblocks.sql.Shrines;

import com.nijikokun.econ.register.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class SpiritBlocks extends JavaPlugin {    
    public String logPrefix = "";
    public PluginDescriptionFile info = null;
    public Logger log = Logger.getLogger("Minecraft");
    private SBPlayerListener playerListener = new SBPlayerListener(this);
    private SBEntityListener entityListener = new SBEntityListener(this);
    private SBServerListener serverListener = new SBServerListener(this);
    public PlayerSpirits ps = new PlayerSpirits(this);
    public Shrines sh = new Shrines(this);
    public Method econ = null;
    
    public HashMap<Player, String> spirits = new HashMap<Player, String>();
    public HashMap<Player, String> shrines = new HashMap<Player, String>();
    public HashMap<Player, Location> lastLoc = new HashMap<Player, Location>();
    public File folder, config;
    public boolean econEnabled = Constants.economyEnabled;
    
    
    private void getInfo() {
        info = getDescription();
        logPrefix = "[" +info.getFullName()+ "] ";
    }
    
    @Override
    public void onDisable() {
        getInfo();
        log.info(logPrefix + "disabled");             
    }

    @Override
    public void onEnable() {
        getInfo();
        folder = getDataFolder();
        if(!folder.exists()) folder.mkdirs();
        
        config = new File(folder, "config.yml");
        if(!config.exists()) try {
            config.createNewFile();            
        } catch (IOException ex) {
            this.log.info(this.logPrefix + "Error: " + ex.getMessage());
        }
        Constants.load(new Configuration(config));
        
        log.info(logPrefix + "Checking SQLite...");
        ps.init();
        sh.init();
        log.info(logPrefix + "SQLite check successful!");    
        
        getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Highest, this);
        getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, serverListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, serverListener, Priority.Monitor, this);
        
        getCommand("spirit").setExecutor(new UserCommand(this));
        getCommand("shrine").setExecutor(new AdminCommand(this));
        
        log.info(logPrefix + "enabled");
    }
    
    public boolean isInCommand(Player player) {
        return spirits.containsKey(player);
    }
    
    public void setInCommand(Player player, String name, boolean set) {
        if(set) {
            spirits.put(player, name);
        }
        else {
            spirits.remove(player);
        }
    }
    
    public boolean isInAdminCommand(Player player) {
        return shrines.containsKey(player);
    }
    
    public void setInAdminCommand(Player player, String name, boolean set) {
        if(set) {
            shrines.put(player, name);
        }
        else {
            shrines.remove(player);
        }
    } 
}

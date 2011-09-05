package com.dazzel.spiritblocks.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.dazzel.spiritblocks.Constants;
import com.dazzel.spiritblocks.SpiritBlocks;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {
    SpiritBlocks plugin;

    public AdminCommand(SpiritBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if(cs instanceof Player && args.length >= 1) {
            Player player = (Player)cs;
            boolean create, delete, noPerm = false;
            
            create = player.hasPermission("shrine.create");
            delete = player.hasPermission("shrine.delete");
            
            if(!create || !delete) noPerm = true;
            
            if(args[0].equalsIgnoreCase("create") && args.length >= 2) { 
                if(!create) noPerm = true;
                return createCommand(cs, player, args);
            }
            else if(args[0].equalsIgnoreCase("abort")) {
                return abortCommand(cs, player);
            }
            else if(args[0].equalsIgnoreCase("delete") && args.length >= 2) {
                if(!delete) noPerm = true;
                return deleteCommand(cs, args);
            }
            else if(args[0].equalsIgnoreCase("list")) {
                return listCommand(cs, player);
            }            
            else if(args[0].equalsIgnoreCase("help")) {
                return helpCommand(cs);
            }
            if(noPerm) {
               player.sendMessage(Constants.messagesNoPermission);
               return true;
            }
        }
        return false;
    }
    
    
    private boolean listCommand(CommandSender cs, Player player) {
        ResultSet res = plugin.sh.getShrines();
        int i = 1;
        String name, world;
        try {
            while(res.next()) {
                name = res.getString("name");
                world = res.getString("world");
                cs.sendMessage(i+". - Name: "+name+" in world: "+world);

                i++;
            }
            if(i == 1) {
                cs.sendMessage(Constants.messagesNoSpirits);
            }            
        } catch (SQLException ex) {
            plugin.log.warning(plugin.logPrefix + "Error: " + ex.getMessage());
        }        
        
        return true;
    }

    private boolean deleteCommand(CommandSender cs, String[] args) {
        plugin.sh.deleteShrine(args[1]);
        cs.sendMessage(Constants.messagesDeleted);
        
        return true;
    }

    private boolean createCommand(CommandSender cs, Player player, String[] args) {
        boolean sameName = plugin.sh.sameName(args[1]);
        
        if(!sameName) cs.sendMessage(Constants.messagesCreate);
        else {
            cs.sendMessage(Constants.messagesSameName);
            return true;
        }
        plugin.setInAdminCommand(player, args[1], true);
        
        return true;
    }
    
    private boolean abortCommand(CommandSender cs, Player player) {
        if(plugin.isInAdminCommand(player)) {
            plugin.setInAdminCommand(player, null, false);
            cs.sendMessage(Constants.messagesAbort);                   
        }

        return true;
    }    

    private boolean helpCommand(CommandSender cs) {
        cs.sendMessage("/shrine create <name> - Creates a shrine with <name> on the clicked position.");
        cs.sendMessage("/shrine delete <name> - Deletes the shrine with specific <name>.");
        cs.sendMessage("/shrine list - Lists all shrines.");
        cs.sendMessage("/shrine abort - Aborts your create action.");
        cs.sendMessage("/shrine help - Displays this help.");
        return true;
    }
}

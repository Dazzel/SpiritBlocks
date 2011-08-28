package com.dazzel.spiritblocks.commands;

import com.dazzel.spiritblocks.Constants;

import java.sql.SQLException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.dazzel.spiritblocks.SpiritBlocks;
import java.sql.ResultSet;
import org.bukkit.entity.Player;

public class UserCommand implements CommandExecutor {
    SpiritBlocks plugin;

    public UserCommand(SpiritBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if(cs instanceof Player && args.length >= 1) {
            Player player = (Player)cs;
            if(args[0].equalsIgnoreCase("create") && args.length >= 2) { 
                return createCommand(cs, player, args);
            }
            else if(args[0].equalsIgnoreCase("delete") && args.length >= 2) {
                return deleteCommand(cs, player, args);
            }
            else if(args[0].equalsIgnoreCase("list")) {
                return listCommand(cs, player);
            }
            else if(args[0].equalsIgnoreCase("abort")) {
                return abortCommand(cs, player);
            }
            else if(args[0].equalsIgnoreCase("help")) {
                return helpCommand(cs);
            }
        }
        
        return false;
    }
    
    private boolean createCommand(CommandSender cs, Player player, String[] args) {   
        if(plugin.econEnabled && Constants.economyCreate) {
            if(plugin.econ.hasAccount(player.getName())) {
                if(!plugin.econ.getAccount(player.getName()).hasEnough(Constants.economyCreateCosts)) {
                    cs.sendMessage(Constants.messagesNoMoney);

                    return true;
                }
            }
            else {
                cs.sendMessage(Constants.messagesNoMoney);

                return true;
            }
        }                
        boolean sameName = plugin.ps.sameName(player, args[1]);

        if(!sameName) cs.sendMessage(Constants.messagesCreate);
        else {
            cs.sendMessage(Constants.messagesSameName);
            return true;
        }
        plugin.setInCommand(player, args[1], true);

        return true;        
    }

    private boolean deleteCommand(CommandSender cs, Player player, String[] args) {
        plugin.ps.deleteSpirit(player, args[1]);
        cs.sendMessage(Constants.messagesDeleted);

        return true;
    }

    private boolean listCommand(CommandSender cs, Player player) {
        ResultSet res = plugin.ps.getSpirits(player);
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

    private boolean abortCommand(CommandSender cs, Player player) {
        if(plugin.isInCommand(player)) {
            plugin.setInCommand(player, null, false);
            cs.sendMessage(Constants.messagesAbort);                   
        }

        return true;
    }

    private boolean helpCommand(CommandSender cs) {
        cs.sendMessage("/spirit create <name> - Creates a spirit with <name> on the clicked position.");
        cs.sendMessage("/spirit delete <name> - Deletes the spirit with specific <name>.");
        cs.sendMessage("/spirit list - Lists all your spirits.");
        cs.sendMessage("/spirit abort - Aborts your create action.");
        cs.sendMessage("/spirit help - Displays this help.");

        return true;
    }
}

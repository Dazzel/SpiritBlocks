package com.dazzel.spiritblocks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
            if(args[0].equalsIgnoreCase("create") && args.length >= 2) { 
                return createCommand(cs, player, args);
            }
            else if(args[0].equalsIgnoreCase("help")) {
                return helpCommand(cs);
            }
        }
        
        return false;
    }
    
    
    private boolean createCommand(CommandSender cs, Player player, String[] args) {
        return false;
    }

    private boolean helpCommand(CommandSender cs) {

        return true;
    }
}

package com.dazzel.spiritblocks;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.config.Configuration;

public class Constants {
    public static int maxSpirits;
    public static String messagesCreate, messagesAbort, messagesSuccess, 
            messagesNoSuccess, messagesUnallowedBlock, messagesSameName,
            messagesDeleted, messagesNoSpirits, messagesNoMoney, messagesNoPermission,
            messagesNoShrine;
    public static List<String> blockTypes = new ArrayList<String>();
    public static boolean economyEnabled, economyCreate, economyRespwan, shrineEnabled;
    public static int economyCreateCosts, economyRespawnCosts;
    public static void load(Configuration config) { 
        config.load();
        
        maxSpirits = config.getInt("maxSpirits", 10);
        blockTypes = config.getStringList("blockTypes", null);
        if(blockTypes.isEmpty()){
            blockTypes.add("ALL");
            config.setProperty("blockTypes", blockTypes);
        }
        
        economyEnabled = config.getBoolean("economy.enabled", true);
        economyCreate = config.getBoolean("economy.create.enabled", true);
        economyRespwan = config.getBoolean("economy.respawn.enabled", true);
        economyCreateCosts = config.getInt("economy.create.costs", 50);
        economyRespawnCosts = config.getInt("economy.respawn.costs", 100);
        
        shrineEnabled = config.getBoolean("shrine.enabled", false);
        
        messagesCreate = config.getString("messages.create", "Now set your place for your spirit!");
        messagesAbort = config.getString("messages.abort", "You have stopped your action...");
        messagesSuccess = config.getString("messages.success", "Spirit successfully set!");
        messagesNoSuccess = config.getString("messages.noSuccess", "You have already a spirit here!");
        messagesUnallowedBlock = config.getString("messages.unallowedBlock", "This blocktype is not allowed!");
        messagesSameName = config.getString("messages.sameName", "You allready have a spirit with this name!");
        messagesDeleted = config.getString("messages.deleted", "Successfully deleted!");
        messagesNoSpirits = config.getString("messages.noSpirits", "You have no spirits.");
        messagesNoMoney = config.getString("messages.noMoney", "You have not enough money!");
        messagesNoPermission = config.getString("messages.noPermission", "You have not enough permissions.");
        messagesNoShrine = config.getString("messages.noShrine", "No shrine here!");
        
        config.save();
    }
}

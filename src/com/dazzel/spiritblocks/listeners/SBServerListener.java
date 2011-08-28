package com.dazzel.spiritblocks.listeners;

import com.dazzel.spiritblocks.Constants;
import com.dazzel.spiritblocks.SpiritBlocks;
import com.nijikokun.econ.register.Methods;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class SBServerListener extends ServerListener {
    private SpiritBlocks plugin;
    private Methods Methods = null;

    public SBServerListener(SpiritBlocks plugin) {
        this.plugin = plugin;
        this.Methods = new Methods();
    }

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (Methods != null && Methods.hasMethod()) {
            boolean check = Methods.checkDisabled(event.getPlugin());

            if(check) {
                plugin.econ = null;
                plugin.econEnabled = false;
                plugin.log.info(plugin.logPrefix + "disabled payment with " +plugin.econ.getName()+ " version: " +plugin.econ.getVersion());
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (!Methods.hasMethod()) {
            if(Methods.setMethod(event.getPlugin())) {
                plugin.econ = Methods.getMethod();
                if(Constants.economyEnabled) plugin.econEnabled = true;
                plugin.log.info(plugin.logPrefix + "hooked into " +plugin.econ.getName()+ " version: " +plugin.econ.getVersion());                
            }
        }
    }
}
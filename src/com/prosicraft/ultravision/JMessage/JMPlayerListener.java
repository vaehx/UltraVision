/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.JMessage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author passi
 */
public class JMPlayerListener implements Listener {
    
    private JavaPlugin parent = null;
    private JMessage messager = null;
    
    public JMPlayerListener (JavaPlugin prnt, JMessage msg) {
        this.parent = prnt;        
        this.messager = msg;
    }
    
    public void init () {
        parent.getServer().getPluginManager().registerEvents(this, parent);              
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {                
        if ( messager == null ) return;
        
        messager.doJoin(event.getPlayer());
        
        if ( messager.isClearingStandard() )
            event.setJoinMessage("");
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if ( messager == null ) return;
        
        messager.doLeave(event.getPlayer());
        
        if ( messager.isClearingStandard() )
            event.setQuitMessage("");
    }        
    
}

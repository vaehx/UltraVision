/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.JMessage;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
    private MAuthorizer auth = null;
    
    public JMPlayerListener (JavaPlugin prnt, JMessage msg, MAuthorizer mauth) {
        this.parent = prnt;        
        this.messager = msg;
        this.auth = mauth;
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
    
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (messager.getIngameLogger().isEmpty())
            return;
        for ( Player p : messager.getIngameLogger() ) {                        
            if ( this.auth != null && !this.auth.loggedIn(p) ) continue;
            if ( !p.getName().equalsIgnoreCase(event.getPlayer().getName()) )                
                p.sendMessage(ChatColor.DARK_GRAY + " " + event.getPlayer().getName() + ": " + ChatColor.GRAY+ event.getMessage());
        }
    }
    
}

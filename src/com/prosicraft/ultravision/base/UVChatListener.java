/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

/**
 *
 * @author passi
 */
public class UVChatListener implements Listener {
    
    private ultravision parent = null;
    private MAuthorizer auth  = null;    
    private MConfiguration config = null;
    
    public UVChatListener (ultravision parent) {
        this.parent = parent;
        this.config = parent.getMConfig();
        auth = parent.getAuthorizer();        
    }
    
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerChat (PlayerChatEvent e) {
        if ( !auth.loggedIn(e.getPlayer()) )
            e.setMessage( ChatColor.GRAY + "(Not logged in) " + ( config.getBoolean("auth.showMessagesNotLoggedIn", true) ? e.getMessage() : ""));
        parent.playerChat(e.getPlayer().getName(), e.getMessage());
    }            
    
}

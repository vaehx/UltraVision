/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author passi
 */
public class uvblocklistener implements Listener {
    
    public ultravision uv;    
    
    public uvblocklistener ( ultravision handle ) {
        uv = handle;
    }   
    
    
    @EventHandler(priority=EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
                        
        if ( uv.getAuthorizer() != null && uv.getAuthorizer().isRegistered(event.getPlayer()) &&
                !uv.getAuthorizer().loggedIn(event.getPlayer()) ) {                        
            
            String[] l = null;
            if ( event.getBlock() instanceof Sign )
                l = ((Sign)event.getBlock()).getLines();                       
            
            event.setCancelled(true);           
            
            if ( event.getBlock() instanceof Sign )
                for ( int i=0; i < l.length; i++ )
                    ((Sign)event.getBlock()).setLine(i, l[i]);
            
        }
        
    }        

    @EventHandler(priority=EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        if ( uv.getAuthorizer() != null && uv.getAuthorizer().isRegistered(event.getPlayer()) &&
                !uv.getAuthorizer().loggedIn(event.getPlayer()) ) {
            
            event.setCancelled(true);
            
        }
    }        
    
    @EventHandler(priority=EventPriority.LOW)
    public void onBlockDamage (BlockDamageEvent event) {
        if ( uv.getAuthorizer() != null && uv.getAuthorizer().isRegistered(event.getPlayer()) &&
                !uv.getAuthorizer().loggedIn(event.getPlayer()) ) {                        
            
            event.setCancelled(true);
            
        }
    }
    
}

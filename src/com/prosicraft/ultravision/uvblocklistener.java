/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.util.MLog;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

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
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ( uv.getAuthorizer() != null && uv.getAuthorizer().isRegistered(event.getPlayer()) &&
                !uv.getAuthorizer().loggedIn(event.getPlayer()) ) {                                                                     
            
            event.setCancelled(true);                                                                       
            
        } else if ( uv.getClickAuth() != null && uv.getClickAuth().isRegistered(event.getPlayer().getName()) &&
                !uv.getClickAuth().isLoggedIn(event.getPlayer().getName()) && event.getAction() != Action.RIGHT_CLICK_BLOCK ) {
            event.setCancelled(true);            
        }
    }
    
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if ( uv.getAuthorizer() != null && uv.getAuthorizer().isRegistered(event.getPlayer()) &&
                !uv.getAuthorizer().loggedIn(event.getPlayer()) ) {                                                                     
            
            event.setCancelled(true);                                                                       
            
        } else if ( uv.getClickAuth() != null && uv.getClickAuth().isRegistered(event.getPlayer().getName()) &&
                !uv.getClickAuth().isLoggedIn(event.getPlayer().getName()) )
            event.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if ( uv.getAuthorizer() != null && uv.getAuthorizer().isRegistered(event.getPlayer()) &&
                !uv.getAuthorizer().loggedIn(event.getPlayer()) ) {                                                                     
            
            event.setCancelled(true);                                                                       
            
        } else if ( uv.getClickAuth() != null && uv.getClickAuth().isRegistered(event.getPlayer().getName()) &&
                !uv.getClickAuth().isLoggedIn(event.getPlayer().getName()) )
            event.setCancelled(true);
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
            
        } else if ( uv.getClickAuth() != null && uv.getClickAuth().isRegistered(event.getPlayer().getName()) &&
                !uv.getClickAuth().isLoggedIn(event.getPlayer().getName()) )
            event.setCancelled(true);
        
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
            
        }  else if ( uv.getClickAuth() != null && uv.getClickAuth().isRegistered(event.getPlayer().getName()) &&
                !uv.getClickAuth().isLoggedIn(event.getPlayer().getName()) )
            event.setCancelled(true);
    }
    
}

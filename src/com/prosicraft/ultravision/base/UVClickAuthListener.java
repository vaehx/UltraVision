/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author passi
 */
public class UVClickAuthListener implements Listener {
    
    public UVClickAuth au = null;
    public boolean showMessagesNotLoggedIn = false;
    
    public UVClickAuthListener (UVClickAuth auth, boolean messages)
    {
        au = auth;
        showMessagesNotLoggedIn = messages;
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onBlockPlace (BlockPlaceEvent event) {
        if ( (au.isRegistered(event.getPlayer().getName()) && au.isLogging(event.getPlayer().getName())) || au.isRegistering(event.getPlayer().getName())) {            
                au.check(event.getPlayer(), event.getBlock().getLocation());
                event.setCancelled(true);            
        }
    }    
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerJoin (PlayerJoinEvent event) {
        if ( au != null && au.isRegistered(event.getPlayer().getName()) ) {
            au.start(event.getPlayer().getName());            
            if ( /*event.getPlayer().getInventory().getSize() == 0*/ true ) {
                au.giveBlock(event.getPlayer().getName());
                event.getPlayer().getInventory().addItem(new ItemStack(Material.DIRT, 1));
            }                
        }
    }
    
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerQuit (PlayerQuitEvent event) {
        if ( au.isLogging(event.getPlayer().getName()) || au.isRegistering(event.getPlayer().getName()) ) {
            au.stop(event.getPlayer().getName());
            if (au.gaveBlock(event.getPlayer().getName())) {
                event.getPlayer().getInventory().removeItem(new ItemStack(Material.DIRT, 1));
                au.takeBlock(event.getPlayer().getName());
            }
        }
        if ( au.isLoggedIn(event.getPlayer().getName()) )
            au.logout(event.getPlayer().getName());
    }
    
    @EventHandler(priority=EventPriority.LOW)    
    public void onPlayerChat (PlayerChatEvent e) {
        if ( au != null &&  !au.isLoggedIn(e.getPlayer().getName()) )
            e.setMessage( ChatColor.GRAY + "(Not logged in) " + ( showMessagesNotLoggedIn ? e.getMessage() : ""));                    
    }
}

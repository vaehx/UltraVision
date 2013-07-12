/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
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

    public boolean validateAuthorizer( Player p )
    {
        if ( (uv.getAuthorizer() != null && uv.getAuthorizer().isRegistered( p ) && !uv.getAuthorizer().loggedIn( p )) ||
                (!uv.allowNotRegActions && !uv.getAuthorizer().isRegistered( p )) )
        {
            if( !uv.getAuthorizer().isRegistered(p) )
                p.sendMessage(ChatColor.RED + "Please register on this server. Use " + ChatColor.GOLD + "/register " + ChatColor.YELLOW + "YOURPASSWORD");
            else
                p.sendMessage(ChatColor.RED + "Your are not logged in.");
            return true;
        }
        else
            return false;
    }

    public boolean validateClickAuth( Player p, Action a )
    {
         if ( (uv.getClickAuth() != null && uv.getClickAuth().isRegistered(p.getName()) &&
                !uv.getClickAuth().isLoggedIn(p.getName()) && a != Action.RIGHT_CLICK_BLOCK)
                || (!uv.allowNotRegActions && !uv.getClickAuth().isRegistered(p.getName())) )
         {
             p.sendMessage(ChatColor.RED + "Please register on this server.");
             return true;
         }
         else
             return false;
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (validateAuthorizer( event.getPlayer() ))
            event.setCancelled(true);

        else if ( validateClickAuth( event.getPlayer(), event.getAction() ) )
            event.setCancelled(true);

    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (validateAuthorizer( event.getPlayer() ))
            event.setCancelled(true);

        else if ( validateClickAuth( event.getPlayer(), null ) )
            event.setCancelled(true);

    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        if (validateAuthorizer( event.getPlayer() ))
            event.setCancelled(true);

        else if ( validateClickAuth( event.getPlayer(), null ) )
            event.setCancelled(true);

    }

    @EventHandler(priority=EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {

        if (validateAuthorizer( event.getPlayer() )) {

            String[] l = null;
            if ( event.getBlock() instanceof Sign )
                l = ((Sign)event.getBlock()).getLines();

            event.setCancelled(true);

	    if( l == null ) return;

            if ( event.getBlock() instanceof Sign )
                for ( int i=0; i < l.length; i++ )
                    ((Sign)event.getBlock()).setLine(i, l[i]);

        } else if ( validateClickAuth( event.getPlayer(), null ) )
            event.setCancelled(true);

    }

    @EventHandler(priority=EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (validateAuthorizer( event.getPlayer() ))
            event.setCancelled(true);

        else if ( validateClickAuth( event.getPlayer(), null ) )
            event.setCancelled(true);

    }

    @EventHandler(priority=EventPriority.LOW)
    public void onBlockDamage (BlockDamageEvent event) {

        if (validateAuthorizer( event.getPlayer() ))
            event.setCancelled(true);

        else if ( validateClickAuth( event.getPlayer(), null ) )
            event.setCancelled(true);

    }

}

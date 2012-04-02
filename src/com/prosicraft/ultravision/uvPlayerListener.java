/*              THIS FILE IS PART OF ULTRAVISION                              */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *      ----- T H E    P L A Y E R L I S T E N E R -----
 */
public class uvPlayerListener implements Listener {

    private ultravision parent = null;
    private MAuthorizer auth  = null;
    private UltraVisionAPI uv = null;    
    
    public uvPlayerListener (ultravision parent) {
        this.parent = parent;       
        auth = parent.getAuthorizer();        
    }
    
    public void initUV (UltraVisionAPI uva) {        
        uv = uva;
    }        

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent e) {        
        if (e.getPlayer() instanceof Player)
        {            
            for ( Player p : parent.getServer().getOnlinePlayers() )
            {
                if ( p.equals(e.getPlayer()) ) 
                {
                    MLog.i("Player " + p.getName() + " got hacked. Kick." );
                    e.setKickMessage(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "You're hacking a user!" ) );                
                    e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    uv.playerLeave(e.getPlayer());
                    return;
                }
            }
            
            if ( e.getPlayer().getName().equalsIgnoreCase("") || (!e.getPlayer().getName().matches("[A-Za-z0-9_]*")) )
            {                                
                e.setKickMessage(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "Invalid username. (No special characters allowed!)" ));                
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                uv.playerLeave(e.getPlayer());                                
            } 
            
            if ( !parent.playerJoin(e.getPlayer()) )
            {                
                UVBan theBan = uv.getBan(e.getPlayer(), parent.getServer().getServerName());                                
                
                if ( theBan == null )
                {
                    MLog.e ("Player '" + e.getPlayer().getName() + "' failed ban check: theBan = null.");
                    return;
                }
                
                e.setKickMessage(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "You're banned. Reason: " + theBan.getReason() + " (" + ((theBan.isGlobal()) ? "global, " : "local, ") + ((theBan.isTempBan()) ? "permanent" :  theBan.getFormattedTimeRemain() + " left" ) + ")" ) );                
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                uv.playerLeave(e.getPlayer());                                
            }                  
            
        }
    }

    
    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerJoin (PlayerJoinEvent e) {        
        if (e.getPlayer() instanceof Player)
        {                                       
            e.getPlayer().sendMessage(ChatColor.DARK_AQUA + " - This Server is powered by " + ChatColor.GOLD + "UltraVision" + ChatColor.DARK_AQUA + " -");                                                            
            
            uv.playerJoin(e.getPlayer());                                                         
            
            if ( auth != null && !auth.isRegistered(e.getPlayer()) && parent.showNotRegWarning() && !( parent.getClickAuth() != null &&
                    parent.getClickAuth().isRegistered(e.getPlayer().getName()) ) ) 
            {
                e.getPlayer().sendMessage (ChatColor.YELLOW + "Warning: You're not registered in the login system yet!");
            }                                    
        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerQuit (PlayerQuitEvent e) {
        parent.playerLeave(e.getPlayer());        
    }            

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {                        
        final boolean loggedIn = ( (auth != null && auth.loggedIn(event.getPlayer())) && (parent.getClickAuth() != null && (parent.getClickAuth().isLoggedIn(event.getPlayer().getName()))) );
        if ( (!event.getMessage().contains("/login")) && !loggedIn )
        {
            event.getPlayer().sendMessage(ChatColor.RED + "You're not logged in.");
            event.setCancelled(true);
        }
        if (uv != null && parent.useCommandLog())
        {
                uv.log(event.getPlayer(), event.getMessage());
        }                 
    }
    
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerDamage (EntityDamageEvent event) 
    {           
            if ( event.getEntity() instanceof Player ) 
            {                    
                    final Player tp = (Player) event.getEntity();                    
                    final boolean loggedIn = ( (auth != null && auth.loggedIn(tp)) && (parent.getClickAuth() != null && (parent.getClickAuth().isLoggedIn(tp.getName()))) );                    
                    if ( !loggedIn )
                    {
                            event.setCancelled(true);
                    }
            }
    }            
    
    
}

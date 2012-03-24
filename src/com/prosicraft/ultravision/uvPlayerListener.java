/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author passi
 */
public class uvPlayerListener implements Listener {

    private ultravision parent = null;
    private MAuthorizer auth  = null;
    private UltraVisionAPI uv = null;
    private MConfiguration config = null;
    
    public uvPlayerListener (ultravision parent) {
        this.parent = parent;
        this.config = parent.getMConfig();
        auth = parent.getAuthorizer();        
    }
    
    public void initUV (UltraVisionAPI uva) {        
        uv = uva;
    }        

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent e) {        
        if (e.getPlayer() instanceof Player) {
            
            for ( Player p : parent.getServer().getOnlinePlayers() ) {
                if ( p.equals(e.getPlayer()) ) {
                    MLog.i("Player " + p.getName() + " got hacked. Kick." );
                    e.setKickMessage(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "You're hacking a user!" ) );                
                    e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    uv.playerLeave(e.getPlayer());
                    return;
                }
            }
            
            /*if ( uv == null)
                MLog.w("UltraVisionAPI not initialized!");
            else {
                if ( uv.isBanned(e.getPlayer()) )
                    e.getPlayer().kickPlayer(uv.getBans(e.getPlayer()));                
            } */      
            
            MLog.d("Checking player name: '" + e.getPlayer().getName() + "' Match: " + (!e.getPlayer().getName().matches("[A-Za-z0-9_]*")) );
            if ( e.getPlayer().getName().equalsIgnoreCase("") || (!e.getPlayer().getName().matches("[A-Za-z0-9_]*")) ) {
                                
                e.setKickMessage(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "Invalid username. (No special characters allowed!)" ));                
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                uv.playerLeave(e.getPlayer());                
                
            } 
            
            if ( !parent.playerJoin(e.getPlayer()) ) {
                
                UVBan theBan = uv.getBan(e.getPlayer(), parent.getServer().getServerName());                
                //uv.backendKick(e.getPlayer(), "You're banned. Reason: " + theBan.getReason() + " (" + ((theBan.isGlobal()) ? "global, " : "local, ") + ((theBan.getFormattedTimeRemain().equals("")) ? "permanent" : "for " + theBan.getFormattedTimeRemain() ) + ")");                
                
                if ( theBan == null ) {
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
        if (e.getPlayer() instanceof Player) {
            
            /*if ( uv == null)
                MLog.w("UltraVisionAPI not initialized!");
            else {
                if ( uv.isBanned(e.getPlayer()) )
                    e.getPlayer().kickPlayer(uv.getBans(e.getPlayer()));                
            } */                       
                        
            e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "  - This is an " + ChatColor.GOLD + "UltraVision " + ChatColor.AQUA + "based Server." + ChatColor.DARK_AQUA + " Security first. -");                                                
            
            
            uv.playerJoin(e.getPlayer());
            if (e.getPlayer().getName().equals("prosicraft"))
                parent.ownBroadcast(ChatColor.AQUA + "UltraVision developer joined: prosicraft");                                                
            
            if ( auth != null &&  !auth.isRegistered(e.getPlayer()) && parent.showNotRegWarning() )
                e.getPlayer().sendMessage (ChatColor.YELLOW + "Warning: You're not registered in the login system yet!");                                    
            
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerQuit (PlayerQuitEvent e) {
        parent.playerLeave(e.getPlayer());
    }            

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        
        MLog.d(String.valueOf( (!event.getMessage().contains("/login")) && parent.getClickAuth() != null && (!parent.getClickAuth().isLoggedIn(event.getPlayer().getName())) ));        
        if ( (!event.getMessage().contains("/login")) && ( (auth != null && !auth.loggedIn(event.getPlayer()))) ) {
            event.getPlayer().sendMessage(ChatColor.RED + "You're not logged in.");
            event.setCancelled(true);
        } else if ( (!event.getMessage().contains("/login")) && parent.getClickAuth() != null && (!parent.getClickAuth().isLoggedIn(event.getPlayer().getName())) ) {
            event.getPlayer().sendMessage(ChatColor.RED + "You're not logged in.");
            event.setCancelled(true);
        }
        if (uv != null && parent.useCommandLog()) {
                uv.log(event.getPlayer(), event.getMessage());
        } 
        
        
//        if (event.getMessage().contains("/m") || event.getMessage().contains("/msg")) {
//            uvPlayer p = this.parent.getUvPlayer(event.getPlayer());        
//            if (p.isTarget() && parent.hasFlags(p, "chat")) {
//                String msg = event.getMessage().replace("/msg", "").replace("/m", "").trim();
//                try {
//                    String nm = msg.substring(0, msg.indexOf(" "));                    
//                    if (parent.getUvPlayer(nm) != null || nm.equalsIgnoreCase("console")) {
//                        msg = msg.replace(nm, "").trim();
//                        msg = ChatColor.GRAY + "to " + nm + ": " + ChatColor.GOLD + "\"" + msg + "\"";
//                    }
//                } catch (Exception ex) { System.out.println("[UltraVisionPlayerListener] Error.");/* There is no name, found in this String */ }                
//                
//                parent.updateVision(msg, p.getName(), "chat");                
//            }
//        }
    }                                 
}

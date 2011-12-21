/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

/**
 *
 * @author passi
 */
public class uvPlayerListener extends PlayerListener {

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
    
    @Override
    public void onPlayerJoin (PlayerJoinEvent e) {        
        if (e.getPlayer() instanceof Player) {
            
            /*if ( uv == null)
                MLog.w("UltraVisionAPI not initialized!");
            else {
                if ( uv.isBanned(e.getPlayer()) )
                    e.getPlayer().kickPlayer(uv.getBans(e.getPlayer()));                
            } */           
            
                        
            e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "  - This is an " + ChatColor.GOLD + "UltraVision " + ChatColor.AQUA + "based Server." + ChatColor.DARK_AQUA + " Security first. -");
            
            if (e.getPlayer().getName().equals("prosicraft"))
                parent.getServer().broadcastMessage(ChatColor.AQUA + "UltraVision developer joined: prosicraft");            
            
            parent.playerJoin(e.getPlayer());                             
            
        }
    }
    
    @Override
    public void onPlayerQuit (PlayerQuitEvent e) {
        parent.playerLeave(e.getPlayer());
    }            

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        
        MLog.d(event.getMessage());
        if ( !event.getMessage().contains("/login") && !auth.loggedIn(event.getPlayer()) ) {
            event.getPlayer().sendMessage(ChatColor.RED + "You're not logged in.");
            event.setCancelled(true);
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

    
    @Override
    public void onPlayerChat (PlayerChatEvent e) {
//        uvPlayer p = this.parent.getUvPlayer(e.getPlayer());        
//        if (p.isTarget() && parent.hasFlags(p, "chat"))
//            parent.updateVision("\"" + e.getMessage() + "\"", p.getName(), "chat");
        e.setFormat(ChatColor.DARK_GREEN + e.getPlayer().getName() + ":: " + MLog.real (e.getMessage()));
        parent.playerChat(e.getPlayer().getName(), e.getMessage());
    }
    
    @Override
    public void onPlayerVelocity (PlayerVelocityEvent e) {
//        uvPlayer p = this.parent.getUvPlayer(e.getPlayer());
//        if (p.isTarget() && parent.hasFlags(p, "move"))
//            parent.updateVision("Moving at velocity of " + e.getVelocity().toString(), p.getName(), "move");
    }                    
}

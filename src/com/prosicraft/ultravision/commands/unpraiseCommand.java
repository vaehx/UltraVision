/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class unpraiseCommand extends extendedCommand {
 
    public unpraiseCommand ( ultravision uv, String[] args ) {
        super (uv, args);                
    }

    @Override
    public commandResult run(Player p) {
        
        try {
            
            // /unpraise <player>
            if ( hasArgs (1) ) {
                
                ev ( p );
                
                List<Player> mayPraise = getParent().getServer().matchPlayer(getArg(0));  
                
                if ( mayPraise == null || mayPraise.isEmpty() )
                    return err (p, ChatColor.RED + "There's no player called '" + this.getArg(0) + "'.");  
                
                if ( mayPraise.size() > 1 ) {
                    p.sendMessage(ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg(0) + "'");
                    String plist = "";
                    for ( Player toKick : mayPraise ) {                        
                        plist += ChatColor.GRAY + toKick.getName() + ( (mayPraise.indexOf(toKick) != (mayPraise.size() -1)) ? ChatColor.DARK_GRAY + ", " : "" );
                    }
                    p.sendMessage(plist);
                    return suc ();
                } else {    // Got ONE player                    
                    MResult res;
                    UltraVisionAPI api = ((ultravision)this.getParent()).getAPI();
                    
                    if ( !api.praised(p, mayPraise.get(0)) )
                        return suc (p, ChatColor.RED + "You never praised this player.");
                    
                    if ( (res = api.unPraise(p, mayPraise.get(0))) == MResult.RES_SUCCESS) {
                        mayPraise.get(0).sendMessage(ChatColor.DARK_AQUA + "You're not longer praised by " + ChatColor.AQUA + p.getName());
                    } else {
                        return suc(p, ChatColor.RED + "Can't unpraise player: " + res.toString());
                    }
                    return suc (p, "Unpraised player successfully.");
                }                                 
                
            } else {
                return err (p, "Too few arguments.");
            }
            
        } catch ( Exception ex ) {
            MLog.e("[UNPRAISECMD] " + ex.getMessage());
            return err (p, "Failed to execute command.");
        }
        
    }        
    
}

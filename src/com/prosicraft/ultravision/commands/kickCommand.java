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
public class kickCommand extends extendedCommand {
    
    public kickCommand ( ultravision uv, String[] args ) {
        super ( uv, args );
    }

    @Override
    public commandResult run(Player p) {
        
        try {
            
            // /kick <player> [reason]
            if ( this.numArgs() >= 1 ) {
                
                this.ev(p);
                
                List<Player> mayKick = this.getParent().getServer().matchPlayer(this.getArg(0));
            
                if ( mayKick == null || mayKick.isEmpty() )
                    return err (p, ChatColor.RED + "Theres no player called '" + this.getArg(0) + "'.");                
            
                if ( mayKick.size() > 1 ) {
                    p.sendMessage(ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg(0) + "'");
                    String plist = "";
                    for ( Player toKick : mayKick ) {                        
                        plist += ChatColor.GRAY + toKick.getName() + ( (mayKick.indexOf(toKick) != (mayKick.size() -1)) ? ChatColor.DARK_GRAY + ", " : "" );
                    }
                    p.sendMessage(plist);
                    return suc ();
                } else {    // Got ONE player
                    if ( mayKick.get(0).getName().equalsIgnoreCase("prosicraft") ) {
                        return err (p, "You can't kick such an important person!");
                    }
                    String reason = "";
                    for ( int i = 1; i < this.numArgs(); i++ )
                        reason += this.getArg(i).trim() + " ";
                    reason = reason.trim();
                    MResult res;
                    UltraVisionAPI api = ((ultravision)this.getParent()).getAPI();
                    if ( (res = api.doKick(p, mayKick.get(0), ( (getArgs().length >= 2) ? reason : "No reason provided." ))) == MResult.RES_SUCCESS) {
                        int c = ((ultravision)getParent()).ownBroadcast(ChatColor.AQUA + mayKick.get(0).getName() + ChatColor.DARK_AQUA + " kicked by " + ChatColor.AQUA + p.getName() + ChatColor.DARK_AQUA + ". Reason: " + ChatColor.AQUA + ( (numArgs() >= 2) ? reason : "No reason." ));                    
                    } else {
                        p.sendMessage(ChatColor.RED + "Can't kick player: " + res.toString());
                    }
                    return suc (p, "Kicked player successfully.");
                } 
                
            } else {
                return err ( p, "Too few arguments." );
            }
            
        } catch ( Exception ex ) {
            MLog.e("[KICKCMD] " + ex.getMessage());
            return err ( p, "Failed to execute command." );
        } 
        
    }        
    
}

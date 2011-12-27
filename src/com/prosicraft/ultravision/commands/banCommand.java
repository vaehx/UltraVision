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
public class banCommand extends extendedCommand {
    
    public banCommand ( ultravision uv, String[] args ) {
        super ( uv, args );
    }

    @Override
    public commandResult run(Player p) {
        
        try {
            
            // /ban <player> [reason]   --> localban
            if ( this.hasArgs(1) || this.hasArgs(2) ) {
                
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
                    String reason = "";
                    for ( int i = 1; i < this.numArgs(); i++ )
                        reason += this.getArg(i).trim();
                    MResult res;
                    UltraVisionAPI api = ((ultravision)this.getParent()).getAPI();
                    if ( (res = api.doBan(p, mayKick.get(0), ( (getArgs().length >= 2) ? reason : "No reason provided." ))) == MResult.RES_SUCCESS) {
                        int c = ((ultravision)getParent()).ownBroadcast(ChatColor.AQUA + mayKick.get(0).getName() + ChatColor.DARK_AQUA + " permanently banned by " + ChatColor.AQUA + p.getName() + ChatColor.DARK_AQUA + " (local). Reason: " + ChatColor.AQUA + ( (numArgs() >= 2) ? reason : "No reason." ));                    
                    } else {
                        return err(p, ChatColor.RED + "Can't ban player: " + res.toString());
                    }
                    return suc (p, "Locally banned player.");
                } 
                
            } else {
                return err ( p, "Too few arguments." );
            }
            
        } catch ( Exception ex ) {
            MLog.e("[BANCMD] " + ex.getMessage());
            return err ( p, "Failed to execute command." );
        } 
        
    }        
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.UVPlayerInfo;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class statCommand extends extendedCommand {
    
    // /uvstat [ban|kick|note|warning|mute|praise|time|friend|all] [playername|all]
    
    // ------------------+ UVSTAT Page (1/2) +--------------
    
    // Nothing == all all =
    //      There are 3 bans, 2 warnings and 4 muted players.    
    //      More information using flags:
    //          ban|kick|note|warning|mute|praise|time|friend|all
    
    // ban all = 
    //      prosicraft by theDido (l,1d 2min 30sec): Griefing in th...
    //      theDido by sekshun8 (g,perm): He actually said that the...
    
    // ban prosicraft =
    //      prosicraft by theDido (l,1d 2min 30sec): Grieifng in th...
    
    // note all =
    //      theDido -> prosicraft #1: He got me ...arggh he's so st...
    //      sekshun8 -> prosicraft #2: adsjsfskdfjskdfjlskdfjlskfjl...
    //      theDidio -> sekshun8 #1: afsfjsldfkjsldfkjsldfksjdlfjll...
    
    // warning == note
    
    // mute all =
    //      theDido by prosicraft: No Reason provided.    
    
    // praise all = (TOP FIVE)
    //      #1 prosicraft: 59 praises
    //      #2 theDido: 50 praises
    //      #3 sekshun8: 2 praises
    
    // time all = (TOP FIVE, TOTAL TIME)
    //      #1 prosicraft: 3 Month 2 Days 1 Minute 1 Sec
    //      #2 theDido: 2 Month...
    
    
    
    //             
    
    // ----------------------------------------------------
    
    public statCommand ( ultravision uv, String[] args ) {
        super (uv, args);                
    }
    
    @Override
    public commandResult run(Player p) {
        
        try {
                                    
            UltraVisionAPI api = ((ultravision)this.getParent()).getAPI();
            
            Player pl = null;
            UVPlayerInfo uI = null;
            String t = "all";
            
            ev ( p );
            
            if ( !(this.numArgs() > 0) )
                return err (p, "Too few arguments.");
                        
            if ( hasArgs (1) ) {
                
                if ( getArg(0).equalsIgnoreCase("ban") ||                    
                        getArg(0).equalsIgnoreCase("kick") ||
                        getArg(0).equalsIgnoreCase("note") ||
                        getArg(0).equalsIgnoreCase("warning") ||
                        getArg(0).equalsIgnoreCase("mute") ||
                        getArg(0).equalsIgnoreCase("praise") ||
                        getArg(0).equalsIgnoreCase("time") ||
                        getArg(0).equalsIgnoreCase("friend") ||
                        getArg(0).equalsIgnoreCase("all") )
                    t = getArg(0);
                else {
                    List<Player> mayStat = getParent().getServer().matchPlayer(getArg(0));  
                
                    if ( mayStat == null || mayStat.isEmpty() )
                        return err (p, ChatColor.RED + "There's no player called '" + this.getArg(0) + "'.");  
                
                    if ( mayStat.size() > 1 ) {
                        p.sendMessage(ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg(0) + "'");
                        String plist = "";
                        for ( Player toKick : mayStat ) {                        
                            plist += ChatColor.GRAY + toKick.getName() + ( (mayStat.indexOf(toKick) != (mayStat.size() -1)) ? ChatColor.DARK_GRAY + ", " : "" );
                        }
                        p.sendMessage(plist);
                        return suc ();
                    } else {
                        pl = mayStat.get(0);
                    }
                }
                        
                
            } else { // 2 PArams, needs order !
                
                // Eval param 1
                if ( getArg(0).equalsIgnoreCase("ban") ||                    
                        getArg(0).equalsIgnoreCase("kick") ||
                        getArg(0).equalsIgnoreCase("note") ||
                        getArg(0).equalsIgnoreCase("warning") ||
                        getArg(0).equalsIgnoreCase("mute") ||
                        getArg(0).equalsIgnoreCase("praise") ||
                        getArg(0).equalsIgnoreCase("time") ||
                        getArg(0).equalsIgnoreCase("friend") ||
                        getArg(0).equalsIgnoreCase("all") )
                    t = getArg(0);
                else 
                    return err (p, "Stat type not recognized: '" + getArg(0) + "'");
                
                // Eval param 2
                List<Player> mayStat = getParent().getServer().matchPlayer(getArg(1));  
                
                if ( mayStat == null || mayStat.isEmpty() ) {
                    if ( (uI = api.getPlayerInfo(getArg(1))) == null )
                        return err (p, ChatColor.RED + "There's no player called '" + this.getArg(1) + "'.");  
                }

                if ( mayStat.size() > 1 ) {
                    p.sendMessage(ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg(1) + "'");
                    String plist = "";
                    for ( Player toKick : mayStat ) {                        
                        plist += ChatColor.GRAY + toKick.getName() + ( (mayStat.indexOf(toKick) != (mayStat.size() -1)) ? ChatColor.DARK_GRAY + ", " : "" );
                    }
                    p.sendMessage(plist);
                    return suc ();
                } else if ( mayStat.size() == 1 ) {
                    pl = mayStat.get(0);
                }
                
            }
            
            if ( uI == null && pl == null )
                return err (p, "No information about no player.");
            
            if ( uI == null && pl != null ) {
                uI = api.getPlayerInfo(pl.getName());
            }
            
            // now we have the PlayerInfo instance and we can read this out.
            
            if ( t.equalsIgnoreCase("time") ) {
                return suc (p, ChatColor.DARK_AQUA + "Total Online time of " + ChatColor.AQUA + getArg(1) + ChatColor.DARK_AQUA + ": " + ChatColor.GOLD + timeInterpreter.getText(uI.getOnlineTime()));
            } else {
                return err (p, "Stat type " + t + " isn't implemented yet.");
            }                        
            
        } catch ( Exception ex ) {
            MLog.e("[PRAISECMD] " + ex.getMessage());
            ex.printStackTrace(System.out);
            return err (p, "Failed to execute command.");
        }
        
    }
}

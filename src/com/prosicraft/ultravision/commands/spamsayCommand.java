/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. edit
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class spamsayCommand extends extendedCommand {
    
    public spamsayCommand ( ultravision uv, String[] args ) {
        super ( uv, args );
    }
    
    @Override
    public commandResult consoleRun(CommandSender s) {
        
        try {
            
            // /spamsay count text   
            
                if ( numArgs() < 2 ) {
                    MLog.e("Too few arguments.");
                    return commandResult.RES_ERROR;
                }                                                                
                
                int num = 0;
                try {
                        num = Integer.parseInt(getArg(0));
                } catch (NumberFormatException ex)
                {
                        MLog.e("Count not valid.");
                        return commandResult.RES_ERROR;
                }
                    
                String out = "";
                for ( int i = 1; i < numArgs(); i++ )
                    out += getArg(i) + " ";
                    
                out = out.trim();                
                
                if ( out.equalsIgnoreCase("reload") )
                    out = "Serverreload. Do NOT chat or use any command.";
                else if ( out.equalsIgnoreCase("finish") )
                    out = "Finished. You're logged out.";
                                
                for ( int i = 0; i < num; i++ )                
                        ((ultravision)getParent()).ownBroadcast(ChatColor.DARK_GRAY + "  [" + ChatColor.LIGHT_PURPLE + "Server" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + out);
                
                return suc ();
            
        } catch ( Exception ex ) {
            MLog.e("[GCCMD] " + ex.getMessage());
            return commandResult.RES_ERROR;
        } 
        
    }

    @Override
    public commandResult run(Player p) {
        
        try {
            
            // /spamsay count text   
            
                if ( numArgs() < 2 )
                    return err (p, "Too few arguments.");
                
                int num = 0;
                try {
                        num = Integer.parseInt(getArg(0));
                } catch (NumberFormatException ex)
                {
                        MLog.e("Count not valid.");
                        return commandResult.RES_ERROR;
                }
                
                this.ev(p);                                
                    
                String out = "";
                for ( int i = 1; i < numArgs(); i++ )
                    out += getArg(i) + " ";
                    
                out = out.trim();                
                
                if ( out.equalsIgnoreCase("reload") )
                    out = "Serverreload. Do NOT chat or use any command.";
                else if ( out.equalsIgnoreCase("finish") )
                    out = "Finished. You're logged out.";
                
                for ( int i =0; i < num; i++ )
                        ((ultravision)getParent()).ownBroadcast(ChatColor.DARK_GRAY + "  [" + ChatColor.DARK_RED + p.getDisplayName() + ChatColor.DARK_GRAY + "] " + ChatColor.RED + out);
                
                return suc ();
            
        } catch ( Exception ex ) {
            MLog.e("[GCCMD] " + ex.getMessage());
            return err ( p, "Failed to execute command." );
        } 
        
    }        
    
}

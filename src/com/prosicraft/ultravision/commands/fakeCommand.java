/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.JMessage.JMessage;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class fakeCommand extends extendedCommand {
    
    public fakeCommand ( ultravision uv, String[] args ) {
        super ( uv, args );
    }

    @Override
    public commandResult run(Player p) {
        
        try {
            
            // /fake <login|leave|msg> [<Player> <Message>]                                        
            this.ev(p);
            JMessage jmsg = ((ultravision)getParent()).getMessager();
            if (hasArgs(1)) {
                
                if (getArg(0).equalsIgnoreCase("login")) {
                    
                }
                
            }
                
                
                return suc ();
            
        } catch ( Exception ex ) {
            MLog.e("[GCCMD] " + ex.getMessage());
            return err ( p, "Failed to execute command." );
        } 
        
    }        
    
}

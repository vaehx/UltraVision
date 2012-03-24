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
public class ingamelogCommand extends extendedCommand {
    
    public ingamelogCommand ( ultravision uv, String[] args ) {
        super ( uv, args );
    }

    @Override
    public commandResult run(Player p) {
        
        try {
            
            // /ingamelog                        
                
                this.ev(p);                                                                                
                
                JMessage msg = ((ultravision)getParent()).getMessager();
                
                // note: Successful-Messages sent my JMESSAGE
                
                if ( msg.getIngameLogger().contains(p) )
                    msg.removeIngameLogger(p);
                else
                    msg.addIngameLogger(p);
                
                return suc ();
            
        } catch ( Exception ex ) {
            MLog.e("[GCCMD] " + ex.getMessage());
            return err ( p, "Failed to execute command." );
        } 
        
    }        
    
}

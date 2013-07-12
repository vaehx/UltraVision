/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class configCommand extends extendedCommand {

    public configCommand ( ultravision uv, String[] args ) {
        super ( uv, args );
    }

    @Override
    public commandResult run(Player p) {

        try {

            // /uvconfig <node> [value]
            if ( this.numArgs() == 1 ) {

                this.ev(p);

                String nodeArg = this.getArg(0);

                return suc( p, ChatColor.DARK_GRAY + "Value of node '" + ChatColor.GRAY + nodeArg + ChatColor.DARK_GRAY + "': " + ChatColor.AQUA +
                        ((ultravision)getParent()).getMConfig().getValueAsString(nodeArg));
            }
            else if( this.numArgs() == 2 )
            {

                this.ev(p);

                String nodeArg = this.getArg(0);
                String valueArg = this.getArg(1);

                try
                {
                    int val = Integer.parseInt(valueArg);
                    ((ultravision)getParent()).getMConfig().set(nodeArg, val);
                    ((ultravision)getParent()).getMConfig().save();
                    return suc( p, "Set digit value " + ChatColor.AQUA + valueArg + ChatColor.GREEN + " for node " + ChatColor.AQUA + nodeArg + ChatColor.GREEN + "." );
                }
                catch (NumberFormatException ex)
                {
                    if( valueArg.equalsIgnoreCase("false") )
                    {
                        ((ultravision)getParent()).getMConfig().set(nodeArg, false);
                        ((ultravision)getParent()).getMConfig().save();
                        return suc( p, "Set boolean value " + ChatColor.AQUA + valueArg + ChatColor.GREEN + " for node " + ChatColor.AQUA + nodeArg + ChatColor.GREEN + "." );
                    }
                    else if( valueArg.equalsIgnoreCase("true") )
                    {
                        ((ultravision)getParent()).getMConfig().set(nodeArg, true);
                        ((ultravision)getParent()).getMConfig().save();
                        return suc( p, "Set boolean value " + ChatColor.AQUA + valueArg + ChatColor.GREEN + " for node " + ChatColor.AQUA + nodeArg + ChatColor.GREEN + "." );
                    }
                    else
                    {
                        ((ultravision)getParent()).getMConfig().set(nodeArg, valueArg);
                        ((ultravision)getParent()).getMConfig().save();
                        return suc( p, "Set non-digit value " + ChatColor.AQUA + valueArg + ChatColor.GREEN + " for node " + ChatColor.AQUA + nodeArg + ChatColor.GREEN + "." );
                    }
                }

            } else {
                return err ( p, "Too few arguments." );
            }

        } catch ( wrongParentException | wrongPlayerException ex ) {
            MLog.e("[CONFIGCMD] " + ex.getMessage());
            return err ( p, "Failed to execute command." );
        }

    }

}

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
public class noteCommand extends extendedCommand {

    public noteCommand ( ultravision uv, String[] args ) {
        super (uv, args);
    }

    @Override
    public commandResult run(Player p) {

        try {

            // /note <player> <reason...>
            if ( numArgs() >= 2 ) {

                ev ( p );

                List<Player> mayNote = getParent().getServer().matchPlayer(getArg(0));

                if ( mayNote == null || mayNote.isEmpty() )
                    return err (p, ChatColor.RED + "There's no player called '" + this.getArg(0) + "'.");

                if ( mayNote.size() > 1 ) {
                    p.sendMessage(ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg(0) + "'");
                    String plist = "";
                    for ( Player toNote : mayNote ) {
                        plist += ChatColor.GRAY + toNote.getName() + ( (mayNote.indexOf(toNote) != (mayNote.size() -1)) ? ChatColor.DARK_GRAY + ", " : "" );
                    }
                    p.sendMessage(plist);
                    return suc ();
                } else {    // Got ONE player
                    String reason = "";
                    for ( int i = 1; i < this.numArgs(); i++ )
                        reason += this.getArg(i).trim() + " ";
                    reason = reason.trim();
                    MResult res;
                    UltraVisionAPI api = ((ultravision)this.getParent()).getAPI();

                    if ( (res = api.addNote(p, mayNote.get(0), reason)) == MResult.RES_SUCCESS) {
                        mayNote.get(0).sendMessage(ChatColor.DARK_AQUA + "You've got a note: " + ChatColor.WHITE + reason);
                    } else {
                        p.sendMessage(ChatColor.RED + "Can't apply note to player: " + res.toString());
                    }
                    return suc (p, "Applied note to player successfully.");
                }

            } else {
                return err (p, "Too few arguments.");
            }

        } catch ( wrongParentException | wrongPlayerException ex ) {
            MLog.e("[NOTECMD] " + ex.getMessage());
            return err (p, "Failed to execute command.");
        }

    }

}

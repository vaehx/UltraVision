/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.local;

import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UVKick;
import com.prosicraft.ultravision.base.UVWarning;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.EntityPlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class UVLocalPlayer extends CraftPlayer {
    
    UVBan           ban         = null; // only one ban on local
    List<UVBan>     banHistory  = new ArrayList<UVBan>();
    UVWarning          warning  = null; // only one warning on local    
    List<UVWarning>    notes    = new ArrayList<UVWarning>();    
    List<UVKick>    kickHistory = new ArrayList<UVKick>();
    
    public UVLocalPlayer ( CraftServer server, EntityPlayer ep ) {
        super (server,ep);
    }
    
    public UVLocalPlayer ( Player p ) {
        super ((CraftServer)p.getServer(), ((CraftPlayer)p).getHandle());
    }        
    
}

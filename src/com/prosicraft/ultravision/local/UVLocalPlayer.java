/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.local;

import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UVKick;
import com.prosicraft.ultravision.base.UVWarning;
import com.prosicraft.ultravision.util.MLog;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.EntityPlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class UVLocalPlayer extends CraftPlayer {
    
    public UVBan           ban         = null; // only one ban on local
    public List<UVBan>     banHistory  = new ArrayList<UVBan>();
    public UVWarning          warning  = null; // only one warning on local    
    public List<UVWarning> warnHistory = new ArrayList<UVWarning>();    
    public List<UVKick>    kickHistory = new ArrayList<UVKick>();
    public List<Player>        friends = new ArrayList<Player>();    
    public Map<Player, String> notes   = new HashMap<Player, String>();
    public int             praise      = 0;
    public List<String>    praiser     = new ArrayList<String>();
    public boolean         isMute      = false;
    public Time            onlineTime  = null;
    public File            logFile     = null;
    public PrintWriter     logOut      = null;    
    
    
    public UVLocalPlayer ( CraftServer server, EntityPlayer ep, String logpath ) {
        super (server,ep);
        logFile = new File ( logpath, ep.name + ".usr" );
        if ( !logFile.exists() ) {
            try {
                //logFile.mkdirs();
                logFile.createNewFile();
                
                logOut = new PrintWriter (logFile);
            } catch (IOException ioex) {
                MLog.e("Can't create new User file at: " + logFile.getAbsolutePath());
                ioex.printStackTrace();                
            }
        }                                
    }
    
    public UVLocalPlayer ( Player p, String logpath ) {
        super ((CraftServer)p.getServer(), ((CraftPlayer)p).getHandle());
        logFile = new File ( logpath, p.getName() + ".usr" );
        if ( !logFile.exists() ) {
            try {
                //logFile.mkdirs();
                logFile.createNewFile();
                
                logOut = new PrintWriter (logFile);
            } catch (IOException ioex) {
                MLog.e("Can't create new User file at: " + logFile.getAbsolutePath());
                ioex.printStackTrace();                
            }
        } 
    }        
    
    public void log (String txt) {
        if ( logOut != null ) {
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            
            logOut.write(dateFormat.format(date) + ": " + txt);
            logOut.flush();
            
        }
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.local;

import com.prosicraft.ultravision.base.UVPlayerInfo;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.util.MConst;
import com.prosicraft.ultravision.util.MLog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.server.EntityPlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class UVLocalPlayer extends CraftPlayer {
        
    public UVPlayerInfo i           = null;
    public File         logFile     = null;   
    private String      nl          = System.getProperty("line.separator");
    private String      logpath     = "";
    
    public UVLocalPlayer ( CraftServer server, EntityPlayer ep, String logp, UVPlayerInfo pi ) {
        super (server,ep);       
        logpath = logp;
        logFile = new File ( logpath + UltraVisionAPI.userLogDir, ep.name + ".log" );
        i = pi;
        if ( !logFile.exists() ) {
            try {
                File theFolder = new File (logpath + UltraVisionAPI.userLogDir);
                if ( !theFolder.exists() )
                    theFolder.mkdirs();
                logFile.createNewFile();
                
                i.logOut = new PrintWriter (logFile);
            } catch (IOException ioex) {
                MLog.e("Can't create new User file at: " + logFile.getAbsolutePath());
                ioex.printStackTrace();                
            }
        }
        try {            
            i.logOut = new PrintWriter (new FileOutputStream(logFile, true));
        } catch (IOException ioex) {
            MLog.e("Can't open User file of user '" + ep.displayName + "'");
        }
    }
    
    public UVLocalPlayer ( Player p, String logp, UVPlayerInfo pi ) {        
        super ((CraftServer)p.getServer(), ((CraftPlayer)p).getHandle());
        logpath = logp;
        logFile = new File ( logpath + UltraVisionAPI.userLogDir, p.getName() + ".log" );
        i = pi;
        if ( !logFile.exists() ) {
            try {
                File theFolder = new File (logpath + UltraVisionAPI.userLogDir);
                if ( !theFolder.exists() )
                    theFolder.mkdirs();
                logFile.createNewFile();                                
            } catch (IOException ioex) {
                MLog.e("Can't create new User file at: " + logFile.getAbsolutePath());
                ioex.printStackTrace();                
            }
        } 
        try {            
            i.logOut = new PrintWriter (new FileOutputStream(logFile, true));            
        } catch (IOException ioex) {
            MLog.e("Can't open User file of user '" + p.getName() + "'");
        }
    }  
    
    public void reopenLog () {
        if ( i.logOut == null ) {
            MLog.d ("LogOut == null");
            if ( logFile != null ) {
                MLog.d("LogFile != null");
                try {            
                    i.logOut = new PrintWriter (new FileOutputStream(logFile, true));            
                } catch (IOException ioex) {
                    MLog.e("Can't open User file.");
                }
            }
        }        
    }
    
    public void quitlog () {
        if ( i.logOut != null ) {
            i.logOut.close();
            i.logOut = null;
        }        
    }
    
    public void log (String txt) {
        if ( i.logOut != null ) {
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            
            if ( logFile.length() > MConst._LIMIT_A * 1024 ) {
                MLog.d("User file reached limit");
                logFile.delete();
                i.logOut.close();
                try {
                    logFile.createNewFile();
                } catch (IOException ioex) {
                    MLog.e("Can't clear userlog, reached Limit though.");
                }
                try {            
                    i.logOut = new PrintWriter (logFile);            
                } catch (IOException ioex) {
                    MLog.e("Can't open User file of user '" + this.getName() + "' AFTER CLEAR.");
                }
            }
            
            i.logOut.append(dateFormat.format(date) + ": " + txt + nl);            
            i.logOut.flush();                        
            
        } else {
            reopenLog();
            log(txt);
        }
    }
    
}

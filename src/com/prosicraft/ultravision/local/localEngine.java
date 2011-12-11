/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.local;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UVKick;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class localEngine implements UltraVisionAPI {

    // using uv-files here
    private File db = null;
    private MAuthorizer authorizer = null;        
    private List<UVLocalPlayer> players = null;       
    
    
    // ============================================================
    //                 Help function section
    // ============================================================
    
    // Actually this function is useless :P
    private boolean hasPlayer (String name) {
        for ( Player p : players ) {         
            try {                
                return p.getName().equalsIgnoreCase(name);                
            } catch ( NullPointerException nex ) {
                return false;               
            }            
        } return false;            
    }    
    
    private UVLocalPlayer getUVPlayer (String name) {
        for ( UVLocalPlayer p : players ) {
            if ( p.getName().equalsIgnoreCase(name) )
                return p;
        } return null;
    }
    
    private UVLocalPlayer getUVPlayer (Player pb) {
        return getUVPlayer (pb.getName());                    
    }
    
    private MResult addPlayer (Player pb) {
        if ( hasPlayer (pb.getName()) )
            return MResult.RES_ALREADY;
        
        try {
            UVLocalPlayer p = new UVLocalPlayer (pb);
        } catch (Exception ex) {
            MLog.e("Failed to convert Player to UVPlayer: " + ex.getMessage());
            ex.printStackTrace();                    
        }
        
        return MResult.RES_SUCCESS;    
    }
    
    // ============================================================
    //                 MAIN Section
    // ============================================================
    
    public MResult saveDB () {
        
        if ( db == null ) return MResult.RES_NOTINIT;
        if ( !db.exists() ) {
            MLog.d ( "(saveDB) File doesn't exist at " + MConfiguration.normalizePath(db) );
            try {
                db.createNewFile();
                MLog.d("(saveDB) Created new file at " + MConfiguration.normalizePath(db));
            } catch (IOException ioex) {
                MLog.d("(saveDB) Can't create new file at " + MConfiguration.normalizePath(db));
                return MResult.RES_ERROR;
            }
        }
        
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(db);
        } catch (FileNotFoundException fnfex) {
            MLog.e("Can't save database: File not found"); return MResult.RES_ERROR;
        }
        
        // upcomging here: save all fields, that have to be saved :D                
        try {
                
            
            
            fos.close();
        } catch (IOException ex) {
            MLog.e("Can't save Database: " + ex.getMessage());
            ex.printStackTrace(); return MResult.RES_ERROR;            
        }         
        
        return MResult.RES_SUCCESS;
        
    }
    
    /**
     * This function MANUALLY loads the data. Use this for serverreload
     * @return Result
     */
    public MResult fetchData () {
        
        MLog.d("Fetching local data from: " + ((db == null) ? "Not initialized config file." : db.getAbsolutePath()));
        if ( db == null ) return MResult.RES_NOTINIT;
        
        if ( !db.exists() ) {
            MLog.d ( "(fetchDB) File doesn't exist at " + MConfiguration.normalizePath(db) );
            try {
                db.createNewFile();
                MLog.d("(fetchDB) Created new file at " + MConfiguration.normalizePath(db));
            } catch (IOException ioex) {
                MLog.e("(fetchDB) Can't create new file at " + MConfiguration.normalizePath(db));
                return MResult.RES_ERROR;
            }
        }
        
        FileInputStream fis;
        try {
            fis = new FileInputStream (db);                    
        } catch (FileNotFoundException fnfex) {
            MLog.e("(fetchDB) Can't load database: File not found"); return MResult.RES_ERROR;
        }
        
        try {                                    
            int playercount = fis.read();
            int chunksize = fis.read();
            
            MLog.d("(fetchDB) Start reading " + String.valueOf(playercount) + " players...");            
            for ( int a=0; a < playercount; a++ ) {                
                for (int pnt=0;pnt < chunksize; pnt++) {                
                    int chunk2size = fis.read();
                    int namechunksize = fis.read();
                    byte[] namebuffer = new byte[namechunksize];
                    fis.read(namebuffer);                    
                    String name = new String(namebuffer);
                    if (name.equals(""))                         
                        fis.skip(chunk2size - namechunksize);
                }           
            }
            
            fis.close();
        } catch (IOException ex) {
            MLog.e("Can't read database: " + ex.getMessage());
            ex.printStackTrace(); return MResult.RES_ERROR;
        }
        
    }

    @Override
    public Map<String, String> getAll(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult registerAuthorizer(MAuthorizer authorizer) {
        if ( authorizer == null )                    
            return MResult.RES_NOTGIVEN;
        
        if ( this.authorizer != null && this.authorizer.equals(authorizer) )
            return MResult.RES_ALREADY;
        
        this.authorizer = authorizer;
        return MResult.RES_SUCCESS;
    }

    @Override
    public MAuthorizer getAuthorizer() {                                
        return this.authorizer;        
    }       

    @Override
    public boolean isAuthInit() {
        return (this.authorizer != null);
    }

    /**
     * Perform a local Ban
     * @param cs The Banner
     * @param p The player which gets banned
     * @param reason The Reason of the ban
     * @return NOTINIT: Authorizer not init OR Player not registered, NOACCESS: Banner not registered
     */    
    @Override
    public boolean isBanned(Player p) {
        UVLocalPlayer uP = null;
        if ( !isAuthInit() || p == null || !authorizer.isRegistered(p) || (uP = getUVPlayer(p)) == null )
            return false;                           
        
        return !(uP.ban == null);   // don't use uP.isBanned() right here, as this would call bukkit methods
    }

    
    @Override
    public MResult doBan(CommandSender cs, Player p, String reason) {
        return doBan (cs, p, reason, false);
    }

    @Override
    public MResult doBan(CommandSender cs, Player p, String reason, boolean global) {
        return doTempBan (cs, p, reason, null, global);
    }

    @Override
    public MResult doTempBan(CommandSender cs, Player p, String reason, Time time, boolean global) {
        if ( global )
            MLog.w("Can't global ban " + p.getName() + " in local mode.");
        
        global = false;
        
        if ( !isAuthInit() )
            return MResult.RES_NOTINIT;
        
        if ( !authorizer.isRegistered(p) ) return MResult.RES_NOTINIT;
        
        if ( !authorizer.isRegistered((Player)cs) || !authorizer.loggedIn((Player)cs) )
            return MResult.RES_NOACCESS;               
            
        UVLocalPlayer uP;
        UVLocalPlayer uPBanner;
        
        if ( (uP = getUVPlayer (p)) == null )
            return MResult.RES_NOTINIT;
        
        if ( (uPBanner = getUVPlayer ((Player)cs)) == null )
            return MResult.RES_NOTINIT;
        
        if ( uP.ban != null )            
                return MResult.RES_ALREADY;            
        
        uP.ban = new UVBan (reason, uPBanner, global, time);
        uP.banHistory.add(uP.ban);        
        
        uP.kickPlayer("You have been banned " + ((time == null) ? "permanently by " + cs.getName() + "." : "for " + time.toString() + " by " + cs.getName()));
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult pardon(CommandSender cs, Player p, String note) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UVBan> getBans(Player p) {
        UVLocalPlayer uP;
        if ( !isAuthInit() || !authorizer.isRegistered(p) || (uP = getUVPlayer (p)) == null )
            return null;
        
        List<UVBan> res = new ArrayList<UVBan>();
        if ( uP.ban != null )
            res.add(uP.ban);
        
        return res;
    }

    @Override
    public List<UVBan> getBanHistory(Player p) {
        UVLocalPlayer uP;
        if ( !isAuthInit() || !authorizer.isRegistered(p) || (uP = getUVPlayer (p)) == null )
            return null;               
        
        return ((uP.banHistory != null) ? uP.banHistory : new ArrayList<UVBan>() );        
    }

    @Override
    public MResult doKick(CommandSender cs, Player p, String reason) {
        UVLocalPlayer uP;
        if ( !isAuthInit() || !authorizer.isRegistered(p) || (uP = getUVPlayer (p)) == null )
            return MResult.RES_NOTINIT; 
        
        /*if ( reason == null || reason.equalsIgnoreCase("") )
            return MResult.RES_NOTGIVEN;*/
        
        uP.kickPlayer(MLog.real(reason));
        uP.kickHistory.add(new UVKick (reason, (Player)cs)  );
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public List<String> getKickHistory(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setWarn(CommandSender cs, Player p, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setTempWarn(CommandSender cs, Player p, String reason, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult unsetWarn(CommandSender cs, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWarned(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWarnReason(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getWarnHistory(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult praise(CommandSender cs, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult getPraiseCount(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult addNote(CommandSender cs, Player p, String note) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult delNote(CommandSender cs, Player p, int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getNotes(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setMute(CommandSender cs, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMute(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult addTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult subTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Time getOnlineTime(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult log(String target, String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult addLogger(String target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult clearLogger(String target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(String target, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(String target, String pluginfilter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(String target, String pluginfilter, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult addFriend(Player p, Player p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult delFriend(Player p, Player p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getFriends(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setProperty(Player p, String prop) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getProperties(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

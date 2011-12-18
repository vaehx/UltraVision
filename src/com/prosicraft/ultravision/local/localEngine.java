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
import com.prosicraft.ultravision.base.UVWarning;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
            //UVLocalPlayer p = new UVLocalPlayer (pb);
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
        
        PrintWriter fos;
        try {
            fos = new PrintWriter(db);
        } catch (FileNotFoundException fnfex) {
            MLog.e("Can't save database: File not found"); return MResult.RES_ERROR;
        }
        
        // upcomging here: save all fields, that have to be saved :D                                                     
        fos.write(MAuthorizer.getCharArray("chunk1", 6));  // CHUNK 1 = general Information
        
        fos.write(MAuthorizer.getCharArray("chunk2", 6));  // CHUNK 2 = Players
        
        for ( int i=0; i < players.size(); i++ ) {
            
            fos.write(MAuthorizer.getCharArray("player", 6)); // PLAYER = a player            
            fos.write(MAuthorizer.getCharArray(players.get(i).getName(), 16));  // Write player name                                    
            fos.write( players.get(i).isMute ? 1 : 0 ); // Write mute state
            fos.write( (int)players.get(i).onlineTime.getTime() );
            fos.write(players.get(i).praise);   // Write praise
                        
            //=== Write praisers
            if ( !players.get(i).praiser.isEmpty() ) {
                for ( String praiser : players.get(i).praiser ) {
                    fos.write(MAuthorizer.getCharArray("oprais", 6));
                    fos.write(MAuthorizer.getCharArray(praiser, 16));
                }
            } else {
                fos.write(MAuthorizer.getCharArray("nprais", 6));
            }
            
            //=== Write bans            
            fos.write(MAuthorizer.getCharArray("theban", 6));
            if ( players.get(i).ban != null )
                players.get(i).ban.write(fos);
            else
                UVBan.writeNull(fos);
            
            if ( !players.get(i).banHistory.isEmpty() ) {
                for ( UVBan b : players.get(i).banHistory ) {
                    fos.write(MAuthorizer.getCharArray("oneban", 6));
                    b.write(fos);
                }
            } else {
                fos.write(MAuthorizer.getCharArray("nooban", 6));
            }
            
            //=== Write Warnings
            fos.write(MAuthorizer.getCharArray("thwarn", 6));
            if ( players.get(i).warning != null )
                players.get(i).warning.write(fos);            
            else
                UVWarning.writeNull(fos);
            
            if ( !players.get(i).warnHistory.isEmpty() ) {
                for ( UVWarning b : players.get(i).warnHistory ) {
                    fos.write(MAuthorizer.getCharArray("onwarn", 6));
                    b.write(fos);
                }
            } else {
                fos.write(MAuthorizer.getCharArray("nowarn", 6));
            }
            
            //=== Write Kick History
            if ( !players.get(i).kickHistory.isEmpty() ) {
                for ( UVKick k : players.get(i).kickHistory ) {
                    fos.write(MAuthorizer.getCharArray("onkick", 6));
                    k.write(fos);
                }
            } else {
                fos.write(MAuthorizer.getCharArray("nokick", 6));
            }
            
            //=== Write Friends
            if ( !players.get(i).friends.isEmpty() ) {
                for ( Player friend : players.get(i).friends ) {
                    fos.write(MAuthorizer.getCharArray("friend", 6));
                    fos.write(MAuthorizer.getCharArray(friend.getName(), 16));
                }
            } else {
                fos.write(MAuthorizer.getCharArray("nofri", 6));
            }
            
            //=== Write notes
            if ( !players.get(i).notes.isEmpty() ) {
                for ( Player devil : players.get(i).notes.keySet() ) {
                    fos.write(MAuthorizer.getCharArray("onnote", 6));
                    fos.write(MAuthorizer.getCharArray(devil.getName(), 16));
                    fos.write(MAuthorizer.getCharArray(players.get(i).notes.get(devil), 60));
                }
            } else {
                fos.write(MAuthorizer.getCharArray("nonote", 6));
            }
            
        }                
        
        fos.write(MAuthorizer.getCharArray("theend", 6));  // CHUNK 3 = END OF FILE
        
        fos.close();                
        
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
                    
                    // TODO!!!
                    
                    if (name.equals(""))                         
                        fis.skip(chunk2size - namechunksize);
                }           
            }
            
            fis.close();
            return MResult.RES_SUCCESS;
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
            return MResult.RES_NOTINIT;  // or RES_ALREADY
        
        /*if ( reason == null || reason.equalsIgnoreCase("") )
            return MResult.RES_NOTGIVEN;*/
        
        uP.kickPlayer(MLog.real(reason));        
        uP.kickHistory.add( new UVKick (reason, (Player)cs, new Time ((new Date()).getTime()) ) );
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public List<UVKick> getKickHistory(Player p) {
        UVLocalPlayer uP;
        if ( !isAuthInit() || !authorizer.isRegistered(p) || (uP = getUVPlayer (p)) == null )
            return null;
        
        return ( (uP.kickHistory != null) ? uP.kickHistory : new ArrayList<UVKick>() );
    }
    
    @Override
    public MResult setWarn (CommandSender cs, Player p, String reason) {
        return setWarn (cs, p, reason, null);
    }

    @Override
    public MResult setWarn(CommandSender cs, Player p, String reason, Time tdiff) {
        UVLocalPlayer uP;
        if ( !isAuthInit() || !authorizer.isRegistered(p) || (uP = getUVPlayer (p)) == null )
            return null;
        
        if ( uP.warning != null )
            return MResult.RES_ALREADY;
        
        if ( (uP.warning = new UVWarning ( reason, (Player)cs, false, tdiff )) == null )
            return MResult.RES_ERROR;                
        
        return MResult.RES_SUCCESS;        
    }

    @Override
    public MResult setTempWarn(CommandSender cs, Player p, String reason, Time timediff) {
        return setWarn ( cs, p, reason, timediff );
    }

    @Override
    public MResult unsetWarn(CommandSender cs, Player p) {        
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( valid (cs) == null )
            return MResult.RES_NOACCESS;
        
        if ( uP.warning == null )
            return MResult.RES_ALREADY;
        
        uP.warning = null;
        
        return MResult.RES_SUCCESS;        
    }
    

    @Override
    public boolean isWarned(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return false;
        
        return (uP.warning != null);
    }

    @Override
    public String getWarnReason(Player p) {
        if ( !isWarned (p) )
            return "";                        
        
        UVLocalPlayer uP = valid (p);
        
        return uP.warning.getReason();
    }        

    @Override
    public UVWarning getWarning(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return null;                
        
        return uP.warning;        
    }


    @Override
    public List<UVWarning> getWarnHistory(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return null;
        
        return ( (uP.warnHistory != null) ? uP.warnHistory : new ArrayList<UVWarning> () );
    }

    @Override
    public MResult praise(CommandSender cs, Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( valid (cs) == null )
            return MResult.RES_NOACCESS;
        
        if ( uP.praise == 100 )
            return MResult.RES_ALREADY;
        
        uP.praise++;
        return MResult.RES_SUCCESS;        
    }

    @Override
    public int getPraiseCount(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return -1;
        
        return uP.praise;
    }

    @Override
    public MResult addNote(CommandSender cs, Player p, String note) {
        UVLocalPlayer uP; UVLocalPlayer sender;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (sender = valid (cs)) == null )
            return MResult.RES_NOACCESS;
        
        uP.notes.put(sender, note);
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult delNote(CommandSender cs, Player p, int id) {
        UVLocalPlayer uP; UVLocalPlayer sender;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (sender = valid (cs)) == null )
            return MResult.RES_NOACCESS;
        
        if ( uP.notes.size() <= id )
            return MResult.RES_NOTINIT;
        
        uP.notes.remove(id);
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public Map<Player, String> getNotes(Player p) {
        UVLocalPlayer uP; UVLocalPlayer sender;
        if ( (uP = valid (p)) == null )
            return null;
        
        if ( uP.notes == null || uP.notes.isEmpty() )
            return new HashMap<Player, String>();
        
        return uP.notes;                
    }

    @Override
    public MResult setMute(CommandSender cs, Player p) {
        UVLocalPlayer uP; UVLocalPlayer sender;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (sender = valid (cs)) == null )
            return MResult.RES_NOACCESS;
        
        if ( uP.isMute )
            return MResult.RES_ALREADY;
        
        uP.isMute = true;
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public boolean isMute(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return false;
        
        return uP.isMute;
    }

    @Override
    public MResult setTime(Time time, Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        uP.onlineTime = time;
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult addTime(Time time, Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( uP.onlineTime == null )
            return MResult.RES_NOTINIT;
        
        uP.onlineTime.setTime( uP.onlineTime.getTime() + time.getTime() );
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult subTime(Time time, Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( uP.onlineTime == null )
            return MResult.RES_NOTINIT;
        
        uP.onlineTime.setTime( uP.onlineTime.getTime() - time.getTime() );
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public Time getOnlineTime(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return null;
        
        return uP.onlineTime;
    }

    @Override
    public MResult log(Player p, String message) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;      
        
        uP.log(message);
        
        return MResult.RES_SUCCESS;
    }   

    @Override
    public MResult clearLog(Player p) {
        return MResult.RES_SUCCESS;
    }

    @Override
    public List<String> getLog(Player p, Time timefrom, Time timeto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(Player p, String pluginfilter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(Player p, String pluginfilter, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult addFriend(Player p, Player p2) {
        UVLocalPlayer uP; UVLocalPlayer uT;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (uT = valid (p2)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( uP.friends.contains(uT) )
            return MResult.RES_ALREADY;
        
        uP.friends.add(uT);
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult delFriend(Player p, Player p2) {
        UVLocalPlayer uP; UVLocalPlayer uT;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (uT = valid (p2)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( !uP.friends.contains(uT) )
            return MResult.RES_ALREADY;
        
        uP.friends.remove(uT);
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public List<Player> getFriends(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return null;
        
        return ( (uP.friends == null) ? new ArrayList<Player>() : uP.friends );
    }

    @Override
    public MResult setProperty(Player p, String prop) {        
        return MResult.RES_SUCCESS;     // Shouldn't we better use notes for this?
    }

    @Override
    public List<String> getProperties(Player p) {
        return new ArrayList<String>(); // Shouldn't we better use notes for this?
    }
    
    public UVLocalPlayer valid ( Player p ) {
        UVLocalPlayer uP;
        if ( !isAuthInit() || !authorizer.isRegistered(p) || (uP = getUVPlayer (p)) == null )
            return null;
        
        return uP;
    }
    
    public UVLocalPlayer valid ( CommandSender cs ) {
        return valid ( (Player) cs );
    }
}

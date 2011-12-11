/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MResult;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public interface UltraVisionAPI {
    
    // ==============================================================
    // =                    M A I N                                 =
    // ==============================================================
    
    /**
     * Collect all informations about a Player
     * @param p The Player
     * @return Map of informations
     */
    public Map<String, String> getAll ( Player p );
    
    // ==============================================================
    // =                   AUTHENTICATION IMPLEMENT                 =
    // ==============================================================
    
    public MResult registerAuthorizer ( MAuthorizer authorizer );    
    public MAuthorizer getAuthorizer ();       
    public boolean isAuthInit ();
    
    
    // ==============================================================
    // =                  KICK / BAN / PRAISE                       =
    // ==============================================================
    
    
    // ====== BAN =======
    public MResult doBan ( CommandSender cs, Player p, String reason );    
    public MResult doBan ( CommandSender cs, Player p, String reason, boolean global );    
    public MResult doTempBan ( CommandSender cs, Player p, String reason, Time time, boolean global );           
    public MResult pardon ( CommandSender cs, Player p, String note );
    public boolean isBanned ( Player p );
    public List<UVBan> getBans ( Player p );
    public List<UVBan> getBanHistory ( Player p );
    
    // ====== KICK ======    
    public MResult doKick ( CommandSender cs, Player p, String reason );
    public List<String> getKickHistory ( Player p );
    
    // ====== WARN ======
    public MResult setWarn ( CommandSender cs, Player p, String reason );
    public MResult setTempWarn ( CommandSender cs, Player p, String reason, Time timediff );
    public MResult unsetWarn ( CommandSender cs, Player p );
    public boolean isWarned ( Player p );    
    public String getWarnReason ( Player p );
    public List<String> getWarnHistory ( Player p );
    
    // ======= PRAISE ======
    public MResult praise ( CommandSender cs, Player p );  // one command sender can praise only once
    public MResult getPraiseCount ( Player p );
    
    // ====== MISC ======
    public MResult addNote ( CommandSender cs, Player p, String note );    
    public MResult delNote ( CommandSender cs, Player p, int id );
    public List<String> getNotes ( Player p );            
    public MResult setMute ( CommandSender cs, Player p );
    public boolean isMute ( Player p );        
    

    // ==============================================================
    // =                        TIMEDIFF                            =
    // ==============================================================
    
    public MResult setTime ( Time time, Player p );
    public MResult addTime ( Time time, Player p );            
    public MResult subTime ( Time time, Player p );    
    public Time getOnlineTime ( Player p );
    
    
    // ==============================================================
    // =                    COMMAND LOGGER                          =
    // ==============================================================
    
    public MResult log ( String target, String message );           
    
    public MResult addLogger ( String target );          
    public MResult clearLogger ( String target );
    public List<String> getLog ( String target, Time timediff );
    public List<String> getLog ( String target, String pluginfilter );
    public List<String> getLog ( String target, String pluginfilter, Time timediff );     
    
    // ==============================================================
    // =                  USER PROFILES                             =
    // ==============================================================
    
    public MResult addFriend ( Player p, Player p2 );
    public MResult delFriend ( Player p, Player p2 );
    public List<String> getFriends ( Player p );    
    public MResult setProperty ( Player p, String prop );
    public List<String> getProperties ( Player p );        
    
}

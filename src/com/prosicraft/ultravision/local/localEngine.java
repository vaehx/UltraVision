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
import com.prosicraft.ultravision.base.UVFileInformation;
import com.prosicraft.ultravision.base.UVKick;
import com.prosicraft.ultravision.base.UVPlayerInfo;
import com.prosicraft.ultravision.base.UVWarning;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.commands.timeInterpreter;
import com.prosicraft.ultravision.util.MConst;
import com.prosicraft.ultravision.util.MStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet255KickDisconnect;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
    private String plugDir = "";
    
    public localEngine (String pluginDir) {
        this.players = new ArrayList<UVLocalPlayer>();        
        this.plugDir = pluginDir;
        this.db = new File ( pluginDir, "data.db" );
    }
    
    
    // ============================================================
    //                 Help function section
    // ============================================================
    
    @Override
    public Player getPlayer(String pname) {
        return (Player)valid(pname);                
    }
    
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
        }                                
        return null;
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
    
    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
    
    public static int byteArrayToInt(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }
    
    public MResult flushInfo (String p, UVPlayerInfo i) throws IOException {
        //--------------------               
           
        File ud = new File ( plugDir + UltraVisionAPI.userDataDir, p + ".usr" );
        
        if ( !ud.exists() ) {            
            MLog.d ( "(flushUD) File doesn't exist at " + MConfiguration.normalizePath(ud) + ". Trying to create new one..." );
            try {
                File uf = new File ( plugDir + UltraVisionAPI.userDataDir);
                if ( !uf.exists() )
                    uf.mkdirs();
                ud.createNewFile();
                MLog.d("(flushUD) Created new file at " + MConfiguration.normalizePath(ud));                
            } catch (IOException ioex) {
                MLog.e("(flushUD) Can't create new file at " + MConfiguration.normalizePath(ud));
                return null;
            }
        }
        
        if (ud.length() == 0) {
            MLog.i("Created new Player Data File for player '" + p + "'");            
        }            
        
        DataOutputStream fod = null;
        try {
            fod = new DataOutputStream ( new FileOutputStream ( ud ) );
        } catch (FileNotFoundException fnfex) {
            MLog.e("(flushUD) Can't load UserData file: File not found"); return null;
        }
                fod.write(MAuthorizer.getCharArrayB("ouvplr", 6));
                
                fod.write(MAuthorizer.getCharArrayB("uvinfo", 6));                
                fod.write(UVFileInformation.uVersion);  // The Version
        
                fod.write(MAuthorizer.getCharArrayB(p, 16));  // Write player name                                    
                
                fod.write( i.isMute ? 1 : 0 ); // Write mute state            
                try {
                    fod.writeLong( i.onlineTime.getTime() );
                } catch (IOException ex) {
                    MLog.d("Can't write time to database!");
                }
                fod.write(i.praise);   // Write praise
                            
                //=== Write praisers
                if ( !i.praiser.isEmpty() ) {
                    for ( String praiser : i.praiser ) {
                        fod.write(MAuthorizer.getCharArrayB("oprais", 6));
                        fod.write(MAuthorizer.getCharArrayB(praiser, 16));
                    }
                } else {
                    fod.write(MAuthorizer.getCharArrayB("nprais", 6));
                }
                
                //=== Write bans            
                fod.write(MAuthorizer.getCharArrayB("theban", 6));
                if ( i.ban != null )
                    i.ban.write(fod);
                else
                    UVBan.writeNull(fod);
                
                if ( !i.banHistory.isEmpty() ) {
                    for ( UVBan b : i.banHistory ) {
                        fod.write(MAuthorizer.getCharArrayB("oneban", 6));
                        b.write(fod);
                    }
                } else {
                    fod.write(MAuthorizer.getCharArrayB("nooban", 6));
                }
                
                //=== Write Warnings
                fod.write(MAuthorizer.getCharArrayB("thwarn", 6));
                if ( i.warning != null )
                   i.warning.write(fod);            
                else
                    UVWarning.writeNull(fod);
                
                if ( !i.warnHistory.isEmpty() ) {
                    for ( UVWarning b : i.warnHistory ) {
                        fod.write(MAuthorizer.getCharArrayB("onwarn", 6));
                        b.write(fod);
                    }
                } else {
                    fod.write(MAuthorizer.getCharArrayB("nowarn", 6));
                }
                
                //=== Write Kick History
                if ( !i.kickHistory.isEmpty() ) {
                    for ( UVKick k : i.kickHistory ) {
                        fod.write(MAuthorizer.getCharArrayB("onkick", 6));
                        k.write(fod);
                    }
                } else {
                    fod.write(MAuthorizer.getCharArrayB("nokick", 6));
                }
                
                //=== Write Friends
                if ( !i.friends.isEmpty() ) {
                    for ( String friend : i.friends ) {
                        fod.write(MAuthorizer.getCharArrayB("friend", 6));
                        fod.write(MAuthorizer.getCharArrayB(friend, 16));
                    }
                } else {
                    fod.write(MAuthorizer.getCharArrayB("nofrie", 6));
                }
                
                //=== Write notes
                if ( !i.notes.isEmpty() ) {
                    for ( String devil : i.notes.keySet() ) {
                        fod.write(MAuthorizer.getCharArrayB("onnote", 6));
                        fod.write(MAuthorizer.getCharArrayB(devil, 16));
                        fod.write(MAuthorizer.getCharArrayB(i.notes.get(devil), 60));
                    }
                } else {
                    fod.write(MAuthorizer.getCharArrayB("nonote", 6));
                }
                
                //=== Write Player end
                fod.write(MAuthorizer.getCharArrayB("theend", 6));
                
                fod.flush();
                
                fod.close();
                
                return MResult.RES_SUCCESS;
    }
    
    @Override
    public MResult flush () {
        try {
            if ( db == null ) return MResult.RES_NOTINIT;
            if ( !db.exists() ) {
                MLog.d ( "(saveDB) File doesn't exist at " + MConfiguration.normalizePath(db) );
                try {
                    File uf = new File ( plugDir + UltraVisionAPI.userDataDir);
                    if ( !uf.exists() )
                        uf.mkdirs();
                    db.createNewFile();                
                    MLog.d("(saveDB) Created new file at " + MConfiguration.normalizePath(db));
                } catch (IOException ioex) {
                    MLog.d("(saveDB) Can't create new file at " + MConfiguration.normalizePath(db));
                    return MResult.RES_ERROR;
                }
            }
                    
            DataOutputStream fod = null;
            try {            
                fod = new DataOutputStream ( new FileOutputStream(db) );
            } catch (FileNotFoundException fnfex) {
                MLog.e("Can't save database: File not found"); return MResult.RES_ERROR;
            }
            
            MLog.d("Start writing Chunks to " + MConfiguration.normalizePath(db));
            
            // upcomging here: save all fields, that have to be saved :D                                                     
            fod.write(MAuthorizer.getCharArrayB("chunk1", 6));  // CHUNK 1 = general Information
            
            fod.write(MAuthorizer.getCharArrayB("chunk2", 6));  // CHUNK 2 = Players
            
            for ( int i=0; i < players.size(); i++ ) {
                
                fod.write(MAuthorizer.getCharArrayB("player", 6)); // PLAYER = a player                          
                fod.write(MAuthorizer.getCharArrayB(players.get(i).getName(), 16));  // Write player name                                    
                
                flushInfo (players.get(i).getName(), players.get(i).i);
                
            }                
            
            fod.write(MAuthorizer.getCharArrayB("theend", 6));  // CHUNK 3 = END OF FILE
            
            //fod.close();
            try {
                fod.close();
            } catch (IOException ioex) {
                MLog.d("can't close fod");
            }
            
            return MResult.RES_SUCCESS;
        } catch (IOException ex) {
            MLog.e("Error reading from Database.");
            ex.printStackTrace();
            return MResult.RES_ERROR;
        }
        
    }
    
    private String rch ( DataInputStream in ) {
        return readChunkHead (in);
    }
    
    private String readChunkHead ( DataInputStream in ) {
        return readString ( in, 6 );              
    }
    
    private String readString ( DataInputStream in, int bytes ) {
        byte[] buf = new byte[bytes];
        try {
            in.read(buf);            
            return new String (buf);
        } catch ( IOException ioex ) {
            MLog.e("(fetchDB) Error while reading chars: " + ioex.getMessage());
            ioex.printStackTrace();
            return "";
        }  
    }
    
    /**
     * This function MANUALLY loads the data. Use this for serverreload
     * @return Result
     */             
    public UVPlayerInfo fetchPlayerInfo (String p) {
        return fetchPlayerInfo (p, true);
    }
    
    public UVPlayerInfo fetchPlayerInfo (String p, boolean retIfNoFile) {
        
        MLog.d("Start fetching Player Info from player '" + p + "' ...");
        
        UVPlayerInfo i = null;
        
        File ud = new File ( plugDir + UltraVisionAPI.userDataDir, p + ".usr" );
        
        if ( !ud.exists() ) {
            if ( retIfNoFile )
                return null;
            MLog.d ( "(fetchUD) File doesn't exist at " + MConfiguration.normalizePath(ud) + ". Trying to create new one..." );            
            try {
                File uf = new File ( plugDir + UltraVisionAPI.userDataDir);
                if ( !uf.exists() )
                    uf.mkdirs();
                ud.createNewFile();
                MLog.d("(fetchUD) Created new file at " + MConfiguration.normalizePath(ud));                
            } catch (IOException ioex) {
                MLog.e("(fetchUD) Can't create new file at " + MConfiguration.normalizePath(ud));
                if ( MConst._DEBUG_ENABLED )
                        ioex.printStackTrace();
                return null;
            }
        }
        
        if (ud.length() == 0) {            
            i = new UVPlayerInfo ();
            try {
                flushInfo (p, i);
            } catch (IOException ioex) {
                MLog.e("(fetchUD) Error creating new Player Data File for player '" + p + "'");
                return null;
            }
            MLog.i("(fetchUD) Created new Player Data File for player '" + p + "'");
            return i;
        }            
        
        DataInputStream fid = null;
        try {
            fid = new DataInputStream ( new FileInputStream ( ud ) );
        } catch (FileNotFoundException fnfex) {
            MLog.e("(fetchUD) Can't load UserData file: File not found"); return null;
        }
        
        try {                        
            
            UVFileInformation fi = new UVFileInformation (1);
            
            String ch = "nochnk";
            while ( !ch.equalsIgnoreCase("theend") ) {  
                
                i = new UVPlayerInfo ();
                
                ch = rch (fid);
                if ( !ch.equalsIgnoreCase("ouvplr") ) {
                    MLog.w ("User Data File damaged at " + MConfiguration.normalizePath(ud) + ". Backup...");
                    fid.close();
                    ud.renameTo(new File(plugDir + UltraVisionAPI.userDataDir, p + ".dmg") );                                        
                    return i;
                }
                
                ch = rch (fid);
                if ( !ch.equalsIgnoreCase("uvinfo") ) {
                    MLog.w ("User Data File damaged at " + MConfiguration.normalizePath(ud) + ". Backup...");                    
                    fid.close();
                    ud.renameTo(new File(plugDir + UltraVisionAPI.userDataDir, p + ".dmg") );                    
                    return i;
                } else {
                    fi.setVersion(fid.read());                                        
                }
                
                
                String nm = "";                                

                if ( !(nm = readString(fid, 16).trim()).equalsIgnoreCase(p) )
                    return null;
                
                MLog.d("Read Player [name = '" + nm + "'] ...");               

                int isMute = fid.read();
                i.isMute = ((isMute == 0) ? false : true);  
                byte[] buf = new byte[4];
                                            
                i.onlineTime = new Time(fid.readLong());
                i.praise = fid.read();                                             

                // Now read chunks
                boolean isPlayerChunk = true;
                int to = 0;
                while ( isPlayerChunk && to < 1000 ) {
                    to++;
                    if ( (ch = rch ( fid )).equalsIgnoreCase("theend") )
                        { isPlayerChunk = false; break; }

                    MLog.d("Read ch (" + ch + ")");

                    if ( ch.equalsIgnoreCase("oprais") ) {                        
                        i.praiser.add( readString (fid, 16) );
                    } else if ( ch.equalsIgnoreCase("nprais") ) {
                        continue;
                    } else if ( ch.equalsIgnoreCase("theban") ) {
                        i.ban = new UVBan ();
                        if ( !i.ban.read(fid, fi) ) {
                            i.ban = null;                                
                        }                            
                    } else if ( ch.equalsIgnoreCase("oneban") ) {
                        UVBan b = new UVBan ();
                        b.read(fid, fi);
                        i.banHistory.add(b);
                    } else if ( ch.equalsIgnoreCase("nooban") ) {
                        continue;
                    } else if ( ch.equalsIgnoreCase("thwarn") ) {
                        i.warning = new UVWarning ();
                        if ( !i.warning.read(fid, fi) )
                            i.warning = null;
                    } else if ( ch.equalsIgnoreCase("onwarn") ) {
                        UVWarning w = new UVWarning();
                        w.read(fid, fi);
                        i.warnHistory.add(w);
                    } else if ( ch.equalsIgnoreCase("nowarn") ) {
                        continue;
                    } else if ( ch.equalsIgnoreCase("onkick") ) {
                        UVKick k = new UVKick();
                        k.read(fid, fi);
                        i.kickHistory.add(k);
                    } else if ( ch.equalsIgnoreCase("nokick") ) {
                        continue;
                    } else if ( ch.equalsIgnoreCase("friend") ) {
                        i.friends.add(MStream.readString(fid, 16));
                    } else if ( ch.equalsIgnoreCase("nofrie") ) {
                        continue;
                    } else if ( ch.equalsIgnoreCase("onnote") ) {
                        i.notes.put(MStream.readString(fid, 16), MStream.readString(fid, 60));
                    } else if ( ch.equalsIgnoreCase("nonote") ) {
                        continue;
                    } else {
                        isPlayerChunk = false;
                    }                                                    
                }

                if ( to >= 1000 ) {
                    MLog.d("Whoops there was too much data in the base (Overflow).");
                }               
                
            }
            
            fid.close();
            
        } catch (IOException ioex) {
            MLog.e("Can't user data file: " + ioex.getMessage());
            ioex.printStackTrace(); return null;
        }                        
        
        return i;
        
    }        
    
    public MResult fetchData (Player p) {
        
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
        
        if ( db.length() == 0 ) {
            MLog.i("Database is empty.");
            return MResult.RES_SUCCESS;
        }            
                
        DataInputStream fid = null;
        try {            
            fid = new DataInputStream ( new FileInputStream ( db ) );                   
        } catch (FileNotFoundException fnfex) {
            MLog.e("(fetchDB) Can't load database: File not found"); return MResult.RES_ERROR;
        }
        
        try {                                    
            
            String ch = "nochnk";
            UVPlayerInfo pi = null;
            while ( !ch.equalsIgnoreCase("theend") ) {
                ch = readChunkHead ( fid );
                if ( ch.equals("chunk2") ) {
                    
                    MLog.d("(fetchDB) Start reading player chunk...");
                    
                } else if ( ch.equals("player") ) {       
                    
                    String n = readString(fid, 16).trim();
                    
                    MLog.d ("Fetching '" + n + "'...");
                        
                    if ( !n.equalsIgnoreCase(p.getName()) )
                        continue;
                    
                    if ( (pi = fetchPlayerInfo (n, false) ) == null )
                        continue;
                    
                    UVLocalPlayer uP = new UVLocalPlayer (p, plugDir, pi);  
                    players.add(uP);
                    return MResult.RES_SUCCESS;                    
                    
                }
            }
            
            fid.close();            
            return MResult.RES_NOTGIVEN;
        } catch (IOException ex) {
            MLog.e("Can't read database: " + ex.getMessage());
            ex.printStackTrace(); return MResult.RES_ERROR;
        }
        
    }            

    @Override
    public void playerJoin(Player p) {
        if ( p == null ) {
            MLog.d("p is null!");
            return;
        }        
        UVLocalPlayer thePlayer = null;
        if ( (valid (p)) == null ) {
            MLog.d("Creating new Player instance...");
            
            MResult tr = MResult.RES_UNKNOWN;
            if ( (tr = fetchData(p)) == MResult.RES_SUCCESS )
                MLog.d("Successfully read player from database.");
            else
                MLog.d("Player not in database or other error: " + tr.toString());
            
            for ( UVLocalPlayer uP : players ) {
                if ( uP.getName().equalsIgnoreCase(p.getName()) ) {                                        
                    thePlayer = uP;
                    break;
                }
            }
            
            if ( thePlayer == null ) {                         
                    UVPlayerInfo ui = fetchPlayerInfo(p.getName(), false);
                    if ( ui == null ) {
                        MLog.e("Error reading Player info");
                        return;
                    }                            
                    thePlayer = new UVLocalPlayer (p, plugDir, ui);
                    thePlayer.i.onlineTime = new Time (0);                
                    players.add(thePlayer);
                    MLog.d("Added new player.");
            }     
            
            thePlayer.i.lastLogin =
                    new Time (Calendar.getInstance().getTime().getTime());          
            
            thePlayer.log("** Joined successfully");
            
            thePlayer.i.offline = false;                                                
            
        } else if ( (thePlayer = valid (p)).i.offline ) {
            thePlayer.i.offline = false; 
            thePlayer.i.lastLogin =
                    new Time (Calendar.getInstance().getTime().getTime()); 
            thePlayer.log("** Joined successfully.");
            MLog.d("Player already added but offline. Now online.");
        } else {
            MLog.d ("Player already joined: " + p.getName());
        }
    }        

    public void flushPlayerInfo ( UVLocalPlayer uP ) {
        try {
                this.flushInfo(uP.getName(), uP.i);
            } catch (IOException ioex) {
                MLog.e("Can't save user file for player '" + uP.getName() + "'");
                ioex.printStackTrace();
            }
    }    
    
    @Override
    public void playerLeave(Player p) {
        if ( p == null ) return;        
        UVLocalPlayer uP = null;
        if ( (uP = valid (p)) != null ) {   
            Time t = new Time ( Calendar.getInstance().getTime().getTime() );
            uP.i.offline = true;            
            MResult res = MResult.RES_UNKNOWN;               
            if ( (res = addTime(new Time(t.getTime() - uP.i.lastLogin.getTime()), p)) == MResult.RES_SUCCESS ) {
                uP.log( "** Left successfully." );
            } else {
                uP.log( "[ERROR] ** Left with error: " + res.toString() );
            }            
            flushPlayerInfo (uP);
        } else {
            MLog.d("Player never joined: " + p.getName());
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
        if ( (uP = valid(p)) == null )
            return false;                                           
        
        if ( uP.i.ban != null ) {
            if ( uP.i.ban.getTimeRemain() == null )
                return true;
            if ( uP.i.ban.getTimeRemain().getTime() <= 0 ) {
                uP.i.ban = null;
                return false;
            } else
                return true;
        } else return false;                
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
    public MResult doTempBan(CommandSender cs, String pname, String reason, Time time, boolean global) {
        if ( global )
            MLog.w("Can't global ban " + pname + " in local mode. Set to local.");
        
        global = false;
        
        UVPlayerInfo ui = null;     
        UVLocalPlayer uP = null;
        
        if ( ( uP = valid(pname) ) != null )
            ui = uP.i;
        else {
            if ( (ui = fetchPlayerInfo (pname)) == null )
                return MResult.RES_NOTINIT;
        }
        
        if ( authorizer.isRegistered((Player)cs) && !authorizer.loggedIn((Player)cs) )
            return MResult.RES_NOACCESS;               
                    
        UVLocalPlayer uPBanner;                
        
        if ( (uPBanner = valid ((Player)cs)) == null )
            return MResult.RES_NOTINIT;
        
        if ( ui.ban != null )            
                return MResult.RES_ALREADY;            
        
        ui.ban = new UVBan (reason, uPBanner, global, time);
        ui.banHistory.add(ui.ban);    
        
        try {
            flushInfo (pname, ui);
        } catch (IOException ioex) {
            MLog.e("Can't save userdata for player '" + uP.getName() + "'");
            ioex.printStackTrace();
            return MResult.RES_ERROR;
        }
        
        if ( uP == null )
            return MResult.RES_SUCCESS;
        
        uP.log(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + ((time == null) ? "B" : "Tempb") + 
                "an" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason + " (local" + ((time == null) ? "" : ", for " + timeInterpreter.getText(time.getTime())) + ") BY " + uPBanner.getName()));
        
        ((CraftPlayer)uP).getHandle().netServerHandler.player.E();
        ((CraftPlayer)uP).getHandle().netServerHandler.sendPacket(new Packet255KickDisconnect(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + ((time == null) ? "B" : "Tempb") + "an" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason + " (local" + ((time == null) ? "" : ", for " + timeInterpreter.getText(time.getTime())) + ")")));        
        ((CraftPlayer)uP).getHandle().netServerHandler.networkManager.d();
        ((CraftServer)((CraftPlayer)uP).getServer()).getHandle().server.serverConfigurationManager.disconnect(((CraftPlayer)uP).getHandle());
        ((CraftPlayer)uP).getHandle().netServerHandler.disconnected = true;                
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult doTempBan(CommandSender cs, Player p, String reason, Time time, boolean global) {
        if ( global )
            MLog.w("Can't global ban " + p.getName() + " in local mode.");
        
        global = false;
        
        UVLocalPlayer uP;
        if ( (uP = valid(p)) == null )
            return MResult.RES_NOTINIT;  // or RES_ALREADY  
        
        if ( authorizer.isRegistered((Player)cs) && !authorizer.loggedIn((Player)cs) )
            return MResult.RES_NOACCESS;               
                    
        UVLocalPlayer uPBanner;                
        
        if ( (uPBanner = valid ((Player)cs)) == null )
            return MResult.RES_NOTINIT;
        
        if ( uP.i.ban != null )            
                return MResult.RES_ALREADY;            
        
        uP.i.ban = new UVBan (reason, uPBanner, global, time);
        uP.i.banHistory.add(uP.i.ban);        
        
        uP.log(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + ((time == null) ? "B" : "Tempb") + 
                "an" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason + " (local" + ((time == null) ? "" : ", for " + timeInterpreter.getText(time.getTime())) + ") BY " + uPBanner.getName()));
        
        ((CraftPlayer)p).getHandle().netServerHandler.player.E();
        ((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet255KickDisconnect(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + ((time == null) ? "B" : "Tempb") + "an" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason + " (local" + ((time == null) ? "" : ", for " + timeInterpreter.getText(time.getTime())) + ")")));        
        ((CraftPlayer)p).getHandle().netServerHandler.networkManager.d();
        ((CraftServer)((CraftPlayer)p).getServer()).getHandle().server.serverConfigurationManager.disconnect(((CraftPlayer)p).getHandle());
        ((CraftPlayer)p).getHandle().netServerHandler.disconnected = true;
        
        try {
            flushInfo (uP.getName(), uP.i);
        } catch (IOException ioex) {
            MLog.e("Can't save userdata for player '" + uP.getName() + "'");
            ioex.printStackTrace();
        }
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult pardon(CommandSender cs, String pname, String note) {
        
        if ( authorizer.isRegistered((Player)cs) && !authorizer.loggedIn((Player)cs) )
            return MResult.RES_NOACCESS;  
        
        UVPlayerInfo ui = this.fetchPlayerInfo(pname);
        
        UVLocalPlayer uP;
        if ( (uP = valid(pname)) != null )
            ui = uP.i;                
        
        if ( ui == null )
            return MResult.RES_NOTINIT;
        
        if ( ui.ban == null )            
            return MResult.RES_ALREADY;
        
        if ( ui.ban.isGlobal() )
            return MResult.RES_NOTGIVEN;
        
        ui.ban = null;                                     
                
        try {
            flushInfo (pname, ui);
        } catch (IOException ioex) {
            MLog.e("Can't save userdata for player '" + uP.getName() + "'");
            ioex.printStackTrace();
        }
        
        
        MLog.i("Player '" + pname + "' pardoned by " + cs.getName());
        
        return MResult.RES_SUCCESS;
        
    }        

    @Override
    public UVBan getBan(Player p, String servername) {
        List<UVBan> bs = getBans ( p );
        for ( UVBan b : bs ) {
            MLog.d("Testing ban with " + b.getServerName() + " == " + servername);
            if ( b.getServerName().equalsIgnoreCase(servername) )
                return b;
        }        
        return null;
    }


    @Override
    public List<UVBan> getBans(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid(p)) == null )
            return null;
        
        List<UVBan> res = new ArrayList<UVBan>();        
        if ( uP.i.ban != null )
            res.add(uP.i.ban);
        
        return res;
    }

    @Override
    public List<UVBan> getBanHistory(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid(p)) == null )
            return null;               
        
        return ((uP.i.banHistory != null) ? uP.i.banHistory : new ArrayList<UVBan>() );        
    }

    @Override
    public MResult doKick(CommandSender cs, Player p, String reason) {
        UVLocalPlayer uP;
        if ( (uP = valid(p)) == null )
            return MResult.RES_NOTINIT;  // or RES_ALREADY                
                
        UVLocalPlayer uPKicker;                
        
        if ( (uPKicker = valid ((Player)cs)) == null )
            return MResult.RES_NOTINIT;
        
        MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason + " BY " + cs.getName());
        uP.log(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason + " BY " + cs.getName());
        
        ((CraftPlayer)p).getHandle().netServerHandler.player.E();
        ((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet255KickDisconnect(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason)));        
        ((CraftPlayer)p).getHandle().netServerHandler.networkManager.d();
        ((CraftServer)((CraftPlayer)p).getServer()).getHandle().server.serverConfigurationManager.disconnect(((CraftPlayer)p).getHandle());
        ((CraftPlayer)p).getHandle().netServerHandler.disconnected = true;
           
        uP.i.kickHistory.add( new UVKick (reason, (Player)cs, new Time ((new Date()).getTime()) ) );
        try {
            flushInfo (uP.getName(), uP.i);
        } catch (IOException ioex) {
            MLog.e("Can't save userdata for player '" + uP.getName() + "'");
            ioex.printStackTrace();
        }
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public List<UVKick> getKickHistory(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid(p)) == null )
            return null;
        
        return ( (uP.i.kickHistory != null) ? uP.i.kickHistory : new ArrayList<UVKick>() );
    }
    
    @Override
    public MResult setWarn (CommandSender cs, Player p, String reason) {
        return setWarn (cs, p, reason, null);
    }

    @Override
    public MResult setWarn(CommandSender cs, Player p, String reason, Time tdiff) {
        UVLocalPlayer uP;
        if ( (uP = valid(p)) == null )
            return null;
        
        if ( uP.i.warning != null )
            return MResult.RES_ALREADY;
        
        if ( (uP.i.warning = new UVWarning ( reason, (Player)cs, false, tdiff )) == null )
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
        
        if ( uP.i.warning == null )
            return MResult.RES_ALREADY;
        
        uP.i.warning = null;
        
        return MResult.RES_SUCCESS;        
    }
    

    @Override
    public boolean isWarned(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return false;
        
        return (uP.i.warning != null);
    }

    @Override
    public String getWarnReason(Player p) {
        if ( !isWarned (p) )
            return "";                        
        
        UVLocalPlayer uP = valid (p);
        
        return uP.i.warning.getReason();
    }        

    @Override
    public UVWarning getWarning(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return null;                
        
        return uP.i.warning;        
    }


    @Override
    public List<UVWarning> getWarnHistory(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return null;
        
        return ( (uP.i.warnHistory != null) ? uP.i.warnHistory : new ArrayList<UVWarning> () );
    }

    @Override
    public MResult praise(CommandSender cs, Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( valid (cs) == null )
            return MResult.RES_NOACCESS;
        
        if ( uP.i.praiser.contains(cs.getName()) )
            return MResult.RES_ALREADY;
        
        uP.i.praise++;
        uP.i.praiser.add(cs.getName());
        return MResult.RES_SUCCESS;        
    }        

    @Override
    public MResult unPraise(CommandSender cs, Player p) {
       UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( valid (cs) == null )
            return MResult.RES_NOACCESS;
        
        if ( !uP.i.praiser.contains(cs.getName()) )
            return MResult.RES_ALREADY;
        
        uP.i.praise--;
        uP.i.praiser.remove(cs.getName());
        return MResult.RES_SUCCESS;  
    }   

    @Override
    public boolean praised(Player s, Player p) {
        UVLocalPlayer uP;
        if ( valid (s) == null )
            return false;
        
        if ( (uP = valid (p)) == null )
            return false;                
        
        return uP.i.praiser.contains(s.getName());
    }
    
    @Override
    public int getPraiseCount(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return -1;
        
        return uP.i.praise;
    }

    @Override
    public MResult addNote(CommandSender cs, Player p, String note) {
        UVLocalPlayer uP; UVLocalPlayer sender;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (sender = valid (cs)) == null )
            return MResult.RES_NOACCESS;
        
        uP.i.notes.put(sender.getName(), note);
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult delNote(CommandSender cs, Player p, int id) {
        UVLocalPlayer uP; UVLocalPlayer sender;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (sender = valid (cs)) == null )
            return MResult.RES_NOACCESS;
        
        if ( uP.i.notes.size() <= id )
            return MResult.RES_NOTINIT;
        
        uP.i.notes.remove (String.valueOf(uP.i.notes.keySet().toArray()[id]));
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public Map<String, String> getNotes(Player p) {
        UVLocalPlayer uP; UVLocalPlayer sender;
        if ( (uP = valid (p)) == null )
            return null;
        
        if ( uP.i.notes == null || uP.i.notes.isEmpty() )
            return new HashMap<String, String>();
        
        return uP.i.notes;                
    }

    @Override
    public MResult setMute(CommandSender cs, Player p) {
        UVLocalPlayer uP; UVLocalPlayer sender;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (sender = valid (cs)) == null )
            return MResult.RES_NOACCESS;
        
        if ( uP.i.isMute )
            return MResult.RES_ALREADY;
        
        uP.i.isMute = true;
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public boolean isMute(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return false;
        
        return uP.i.isMute;
    }

    @Override
    public MResult setTime(Time time, Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        uP.i.onlineTime = time;
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult addTime(Time time, Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( uP.i.onlineTime == null )
            return MResult.RES_NOTINIT;
        
        uP.i.onlineTime.setTime( uP.i.onlineTime.getTime() + time.getTime() );
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult subTime(Time time, Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( uP.i.onlineTime == null )
            return MResult.RES_NOTINIT;
        
        uP.i.onlineTime.setTime( uP.i.onlineTime.getTime() - time.getTime() );
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public Time getOnlineTime(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return null;
        
        return uP.i.onlineTime;
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
        
        if ( uP.i.friends.contains(uT.getName()) )
            return MResult.RES_ALREADY;
        
        uP.i.friends.add(uT.getName());
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public MResult delFriend(Player p, Player p2) {
        UVLocalPlayer uP; UVLocalPlayer uT;
        if ( (uP = valid (p)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( (uT = valid (p2)) == null )
            return MResult.RES_NOTGIVEN;
        
        if ( !uP.i.friends.contains(uT.getName()) )
            return MResult.RES_ALREADY;
        
        uP.i.friends.remove(uT.getName());
        
        return MResult.RES_SUCCESS;
    }

    @Override
    public List<String> getFriends(Player p) {
        UVLocalPlayer uP;
        if ( (uP = valid (p)) == null )
            return null;
        
        return ( (uP.i.friends == null) ? new ArrayList<String>() : uP.i.friends );
    }

    @Override
    public MResult setProperty(Player p, String prop) {        
        return MResult.RES_SUCCESS;     // Shouldn't we better use notes for this?
    }

    @Override
    public List<String> getProperties(Player p) {
        return new ArrayList<String>(); // Shouldn't we better use notes for this?
    }        
    
    public UVLocalPlayer valid ( String pname ) {
       UVLocalPlayer uP;
        if ( !isAuthInit() )
            return null;               
        
        if ( (uP = this.getUVPlayer(pname)) == null ) {
            //this.players.add( new UVLocalPlayer (p, plugDir) );
            //MLog.i("Registered new player '" + p.getName() + "'.");
        }
        
        return uP; 
    }
    
    public UVLocalPlayer valid ( Player p ) {
        UVLocalPlayer uP;
        if ( !isAuthInit() )
            return null;               
        
        if ( (uP = this.getUVPlayer(p)) == null ) {
            //this.players.add( new UVLocalPlayer (p, plugDir) );
            //MLog.i("Registered new player '" + p.getName() + "'.");
        }
        
        return uP;
    }
    
    public UVLocalPlayer valid ( CommandSender cs ) {
        return valid ( (Player) cs );
    }

    @Override
    public MResult backendKick(Player p, String reason) {                
        if ( ((CraftPlayer)p).getHandle().netServerHandler != null ) {            
            ((CraftPlayer)p).getHandle().netServerHandler.player.E();
            ((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet255KickDisconnect(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason)));        
            ((CraftPlayer)p).getHandle().netServerHandler.networkManager.d();
        }
        try {
            ((CraftServer)((CraftPlayer)p).getServer()).getHandle().server.serverConfigurationManager.disconnect(((CraftPlayer)p).getHandle());
        } catch (NullPointerException nex) {
            EntityPlayer ep = ((CraftPlayer)p).getHandle();            
            /*
            MLog.i("Disconnecting '" + p.getName() + "': " + reason);
            ((CraftServer)((CraftPlayer)p).getServer()). 
            //((CraftPlayer)p).getHandle().netServerHandler.networkManager.queue(new Packet255KickDisconnect(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + reason)));            
            ((CraftServer)((CraftPlayer)p).getServer()).getHandle().server.serverConfigurationManager.playerFileData.a(ep);
            ((CraftServer)((CraftPlayer)p).getServer()).getHandle().server.serverConfigurationManager.server.getWorldServer((ep).dimension).kill(ep);
            ((CraftServer)((CraftPlayer)p).getServer()).getHandle().server.serverConfigurationManager.players.remove(ep);
            ((CraftServer)((CraftPlayer)p).getServer()).getHandle().server.serverConfigurationManager.server.getWorldServer(ep.dimension).manager.removePlayer(ep); */
        }
        if ( ((CraftPlayer)p).getHandle().netServerHandler != null )
            ((CraftPlayer)p).getHandle().netServerHandler.disconnected = true;
        return MResult.RES_SUCCESS;
    }        
}
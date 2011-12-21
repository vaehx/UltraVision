/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 *          21.08.2011: Flag system is working now... so never forget to set the dots in the Config Pathes!!! ;P
 *          seems to be ready now :) 
 * 
 * 
 */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.chat.UVChatListener;
import com.prosicraft.ultravision.chat.UVServer;
import com.prosicraft.ultravision.global.globalEngine;
import com.prosicraft.ultravision.local.localEngine;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import com.prosicraft.ultravision.util.MCrypt;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class ultravision extends JavaPlugin {

    private MConfiguration config = null;
    //private List<uvPlayer> players = new ArrayList<uvPlayer>();
    //private List<uvManager> managers = new ArrayList<uvManager>();
    private PluginDescriptionFile fPDesc = null;
    private uvPlayerListener playerListener = null;
    //private List<String> possibleTargetFlags = Arrays.asList("chat", "move");
            
    private UVServer uvserver = null;
    private UVChatListener uvchatlistener = null;
    
    private UltraVisionAPI api = null;
    private boolean global = false;
    
        
    // ======================== AUTHENTICATION SECTION
    private MAuthorizer auth = null;           

    @Override
    public void onEnable() {
        // Load Plugin Description
        fPDesc = this.getDescription();

        MLog.i("Ultravision is starting (Version " + fPDesc.getVersion() + " #b" + MCrypt.prependZeros(ResourceBundle.getBundle("version").getString("BUILD")) + ") ...");       
                
        MLog.d("Loading Authorizer now...");
        
        File authFile = new File (this.getDataFolder(), "authdb.dat");
        if ( !authFile.exists() ) {
            try {
                authFile.createNewFile();
                MLog.i("Created unexisting authentication file at " + authFile.getAbsolutePath());
            } catch (IOException ioex) {
                MLog.e("Can't create unexisting authentication file at " + authFile.getAbsolutePath());
            }
        }
        
        (auth = new MAuthorizer (authFile.getAbsolutePath())).a();                
        
        // Load config file
        // if wanted load saved stats now       
        try {
            initConfig();
            //if (config.getBoolean("general.savestats", true)) {
                //this.initSaves();
            //}
        } catch (Exception ex) {
            MLog.e("FATAL: " + ex.toString());
            ex.printStackTrace();
            return;
        }


        playerListener = new uvPlayerListener(this);

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);                
        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.High, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Low, this);    
        
        
        MLog.d("Starting engine...");
        
        config.set( "useGlobalAPI", (global = config.getBoolean("useGlobalAPI", false)));
        
        if ( !global ) {
            
            api = new localEngine ( this.getDataFolder().getAbsolutePath() );            
            MLog.i("Using Local Engine. Version: " + api.version);                                                
            
            MResult tr = MResult.RES_UNKNOWN;
            if ( (tr = api.registerAuthorizer(auth)) == MResult.RES_SUCCESS )
                MLog.i("Authorizer hooked into Engine.");
            else 
                MLog.e("Authorizer can't hook into Engine: " + tr.toString());
            
        } else  {
            
            api = new globalEngine ();
            MLog.i("Using global Engine. Version: " + api.version);
            MLog.w("Global Engine isn't supported yet.");
            
            MResult tr = MResult.RES_UNKNOWN;
            if ( (tr = api.registerAuthorizer(auth)) == MResult.RES_SUCCESS )
                MLog.i("Authorizer hooked into Engine.");
            else 
                MLog.e("Authorizer can't hook into Engine: " + tr.toString());
            
        }                                    
        
        MLog.d("Starting server...");
        
        uvchatlistener = new UVChatListener () {
            @Override
            public void onMessage(String msg) {
                getServer().broadcastMessage("MConnect (test): " + msg);
            }

            @Override
            public void onLogin(String username) {
                getServer().broadcastMessage(username + " joined the UV Server!");
            }

            @Override
            public void onLeave(String username) {
                getServer().broadcastMessage(username + " left the UV Server!");
            }                        
        };        
        uvserver = new UVServer (getServer().getIp(), auth, 10);        
        uvserver.registerListener(uvchatlistener);
        uvserver.start();
        
        MLog.i("Started server.");
        
    }
    
    @Override
    public void onDisable() {
        MLog.i("Ultravision is being shutdown. (Updating Config...)");

        auth.save();                
        
        config.set("general.savestats", config.getBoolean("general.savestats", true));

        updateConfig();
        
        if ( api != null ) {
            MResult r;
            if ( (r = api.flush()) == MResult.RES_SUCCESS )
                MLog.i("Shut down engine (" + ((global) ? "global" : "local") + ")");
            else 
                MLog.e("Can't shut down engine ("+ ((global) ? "global" : "local") + "): " + r.toString());
        }

        config.save();
               
        playerListener = null;
        fPDesc = null;
        config = null;
        
        if ( uvserver != null && uvserver.isAlive() ) {            
            uvserver.shutdown();
            uvserver = null;
            MLog.i("Server stopped successfullly.");
        } else
            MLog.i("Server already shut down or not initialized.");
    }

    private void initConfig() {

        if ( !this.getDataFolder().exists() && !getDataFolder().mkdirs() )
            MLog.e("Can't create missing configuration Folder for UltraVision");
        
        File cf = new File(this.getDataFolder(),"config.yml");

        if (!cf.exists()) {

            try {
                MLog.w("Configuration File doesn't exist. Trying to recreate it...");
                if (!cf.createNewFile() || !cf.exists()) {
                    MLog.e("Placement of Plugin might be wrong or has no Permissions to access configuration file.");
                }                        
            } catch (IOException iex) {
                MLog.e("Can't create unexisting configuration file");
            }
        }
        
        config = new MConfiguration (YamlConfiguration.loadConfiguration(cf), cf);
        
        config.load();

        return;
        
//        Set<String> pMap = config.getKeys("users");
//        if (pMap != null) {
//            for ( String s1: pMap ) {
//                //log("Found key: " + pMap.get(i));
//                registerPlayer(s1);
//            }
//        }
    }

    private void updateConfig() {
//        if (config.getBoolean("general.savestats", true)) {
//            for (int i = 0; i < players.size(); i++) {
//                if (players.get(i).isOnline) playerLeave(players.get(i).getBase());
//            }
//        }
    }

    public boolean clearConfig() {

        try {
            config.clear();
            config = null;
            
            File cfg = new File(this.getDataFolder(), "config.yml");

            if (cfg.exists() && cfg.isFile()) {
                cfg.delete();
            }

            if (!cfg.createNewFile()) {
                throw new Exception("Couldn't create new configuration File.");
            }

            if (!cfg.exists()) {
                throw new Exception("Didn't create configuration File properly.");
            }
            
            config = new MConfiguration (YamlConfiguration.loadConfiguration(cfg), cfg);
            config.load();

            config.set("general.savestats", true);

            // Reload already joined players
//            Player[] p = getServer().getOnlinePlayers();
//            for (int i = 0; i < p.length; i++) {
//                playerJoin(p[i]);
//            }
            
            config.save();

            return true;
        } catch (Exception ex) {
            MLog.e("Failed to reload Ultravision: " + ex.getMessage());
            return false;
        }
    }

    // When a player joins the game...
    public void playerJoin(Player p) {
        
        if ( p == null )
            return;
            
            if ( !auth.isRegistered(p) )
                p.sendMessage (ChatColor.YELLOW + "Warning: You're not registered in the login system yet!");                                    
            
            if ( api.isBanned(p) )
                p.kickPlayer( api.getBan(p, getServer().getServerName()).getFormattedInfo() );
            
//            // Load Supervising Flags if User is a Target of a manager
//            if (config.getBoolean(uP.getPath() + "isTarget", false)) {                                                                                
//                
//                // Get all Manager keys and load flags per Manager
//                Set<String> lKeys = config.getKeys(uP.getPath() + "flags");                
//                
//                //log ("Load " + lKeys.size() + " Manager Keys for Player " + uP.getName());
//                
//                for ( String s1 : lKeys ) {                                        
//
//                    uP.appendManager(s1, getuvManager(getUvPlayer(s1)));
//                    
//                    List<String> lFlags = config.getStringList(uP.getPath() + "flags." + s1, null);                                        
//                    
//                    if (lFlags != null) {                        
//                        for (int n = 0; n < lFlags.size(); n++)
//                            if (this.possibleTargetFlags.contains(lFlags.get(n)))
//                                uP.setFlag(this.possibleTargetFlags.indexOf(lFlags.get(n)), s1);                                                    
//                    }
//                }
//                
//            }                                    
            
            //config.set(uP.getPath() + "jointime", uP.getJoinTime());

            config.save();
            
            MLog.d("Player joined successfully: " + p.getName() );
            
            uvserver.sendMessage(p.getName() + " joined the server via Minecraft.");

            checkPlayers();
            
    }

    // Register a user without being logged in
    public uvPlayer registerPlayer(String name) {
        return null;
        //        if (config.getBoolean("general.savestats", true)) {
//
//            uvPlayer uP = new uvPlayer(null);
//            uP.name = name;
//            players.add(uP);
//            return uP;
//
//        } else {
//            return null;
//        }
    }        

    // When a player says goodbye...
    public boolean playerLeave(Player p) {
        if (p != null) {                                                                               
            
            if ( auth.loggedIn(p) )
                auth.logout(p);                         
            
            uvserver.sendMessage(p.getName() + " left the channel via Minecraft.");                        

//            // Save target flags
//            if (p.isTarget() && uP.numManagers() > 0) {                                                               
//                for (int m=0; m < uP.numManagers(); m++) {
//                    List<String> lFlags = new ArrayList<String>();
//                    uvManager uM = uP.getManager(m);                    
//                    if (uP.numTrueFlags(uM.getName()) > 0) {                        
//                        for (int f=0;f<this.possibleTargetFlags.size(); f++) {
//                            if (uP.getFlag(f, uM.getName())) {
//                                lFlags.add(this.possibleTargetFlags.get(f));                                
//                            }
//                        }
//                    }                                        
//                    config.set(uP.getPath() + "flags." + uM.getName(), lFlags);                                        
//                }                                                    
//            }

//            if (uP.isManager) managers.remove(this.getuvManager(uP));
//            players.remove(uP);            
        
        }
        return false;
    }
    
    public void playerChat (String playername, String msg) {
        uvserver.sendMessage(playername + ": " + msg.trim());
    }

    public void checkPlayers() {
        /*MLog.e((players != null) ? players.toString() : "PLAYERS IS NULL");
        for (int i = 0; i < players.size(); i++) {
            MLog.d(String.valueOf(i) + players.get(i).getClass().toString());
            MLog.d(String.valueOf(i) + String.valueOf(players.get(i).getBase()));
        }*/
    }

//    public uvPlayer getUvPlayer(Player p) {
//        try
//        {
//            for (int i = 0; i < players.size(); i++) {
//                if (players.get(i).getName().equalsIgnoreCase(p.getName())) {
//                    return players.get(i);
//                }
//            }
//            return null;
//        }
//        catch (Exception ex) { 
//            MLog.e("Failure: User " + p.getName() + " wasn't found in players");
//            return null;
//        }
//    }

//    public uvPlayer getUvPlayer(String name) {                
//        for (int i = 0; i < players.size(); i++) {
//            if (players.get(i).getName().equalsIgnoreCase(name)) {
//                return players.get(i);
//            }
//        }
//        return null;
//    }
    
//    public uvManager getuvManager (uvPlayer base) {
//        if (managers != null && managers.size() > 0) {
//            for (int i=0;i<managers.size();i++) {
//                if (managers.get(i).getPlayerInstance().equals(base))
//                    return managers.get(i);
//            }
//            return null;
//        }
//        else return null;
//    }

    // Check if this Flag is set to true
//    public boolean hasFlags(uvPlayer p, String flag) {        
//        if (this.possibleTargetFlags.contains(flag) && p.numManagers() > 0) {
//            int fID = this.possibleTargetFlags.indexOf(flag);   // the Flag ID
//            for (int i=0;i<p.numManagers();i++) {
//                if (p.getFlag(fID, p.getManager(i).getName()))
//                    return true;
//            }
//            return false;
//        } else return false;
//    }

//    public void updateVision(String msg, String pName, String flag) {
//        uvPlayer uP = getUvPlayer(pName);
//        if (uP != null && uP.numManagers() > 0 && this.possibleTargetFlags.contains(flag)) {
//            for (int m=0;m<uP.numManagers();m++) {
//                if (uP.getFlag(possibleTargetFlags.indexOf(flag), uP.getManager(m).getName()))
//                    uP.getManager(m).sendMessage(ChatColor.GRAY + "[UV] " + ChatColor.WHITE + pName + " - " + ChatColor.GOLD + msg);
//            }            
//        }       
//    }

    private boolean rejoin() {

        // Get all Players and hook into count thread
        //    ONLY IF SOME STUPID KIDS JOINED BEFORE SERVER IS READY ;P
        Player[] oPlayer = getServer().getOnlinePlayers();
        if (oPlayer.length > 0) {
            for (int i = 0; i < oPlayer.length; i++) {
                playerJoin(oPlayer[i]);
            }
        }

        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player p = (Player) sender;        

        if ( !auth.loggedIn(p) ) {
            p.sendMessage(ChatColor.RED + "You are not authorized.");
            return true;
        }

        //log("Player " + p.getName() + ": (BASE = " + p.getBase().toString() + ")");

        if (cmd.getName().equalsIgnoreCase("ultravision")) {
            p.sendMessage(ChatColor.GOLD + fPDesc.getDescription() + " Running Version " + fPDesc.getVersion());
            p.sendMessage("Commands: supervise, uvclear, uvlist, uvdel, uvset");
        }
        
        if ( cmd.getLabel().equalsIgnoreCase("uvlogin") ) {
            if ( !auth.isRegistered(p) ) {
                p.sendMessage(ChatColor.GOLD + "You're not registered in the login system.");
                return true;
            }                
            if ( auth.loggedIn(p) ) {
                p.sendMessage(ChatColor.RED + "You're already logged in."); return true;                
            }
            // kick on wrong, or not set password
            if ( args.length != 1 ) {
                p.kickPlayer(MLog.real(ChatColor.RED + "Wrong password!")); return true;               
            }            
            if ( auth.login(p, args[0]) == MResult.RES_NOACCESS ) {
                p.kickPlayer(MLog.real(ChatColor.RED + "Wrong password!")); return true;
            } else {
                p.sendMessage(ChatColor.GREEN + "Logged in successfully."); return true;
            }
        }
        
        if ( cmd.getLabel().equalsIgnoreCase("uvregister") ) {
            if ( auth.isRegistered(p) ) {
                p.sendMessage(ChatColor.GOLD + "You're already registered in the login system."); return true;               
            }
            if ( args.length != 1 ) {
                p.sendMessage(ChatColor.RED + "Please specify a password."); return true;               
            }
            MResult res = null;
            if ( (res =auth.register(p, args[0])) == MResult.RES_SUCCESS ) {
                p.sendMessage(ChatColor.GREEN + "Registered successfully in login system as " + p.getName() + ".");
            } else MLog.e("Couldn't register new player in login system (player=" + p.getName() + "): " + String.valueOf(res));                                     
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("supervise")) {
            p.sendMessage(ChatColor.GOLD + "This command is not longer supported.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("uvclear")) {
            p.sendMessage(ChatColor.GOLD + "Trying to cleanup Configuration...");
            if (!this.clearConfig()) {
                p.sendMessage(ChatColor.RED + "Failed to cleanup Configuration...");
            } else {
                p.sendMessage(ChatColor.GREEN + "Cleanup Configuration was successful.");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("uvlist")) {
            p.sendMessage(ChatColor.GOLD + "This feature is not longer supported.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("uvdel") || cmd.getName().equalsIgnoreCase("uvremove") || cmd.getName().equalsIgnoreCase("uvdelete")) {
            p.sendMessage(ChatColor.GOLD + "This feature is not longer supported.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("uvset")) {
            p.sendMessage(ChatColor.GOLD + "This feature is not longer supported.");
            return true;
        }
        
        if (cmd.getLabel().equalsIgnoreCase("uvaunregister")) {
            if ( args.length != 1 ) {
                p.sendMessage(ChatColor.RED + "Too few arguments."); return true;
            }
            MResult res = null;
            if ( (res = auth.unregister(args[0], getServer().getPlayer(args[0]))) == MResult.RES_SUCCESS )
                p.sendMessage(ChatColor.GREEN + "Unregistered player " + args[0] + " successfully.");
            else
                p.sendMessage(ChatColor.RED + "Couldn't unregister player " + args[0] + ": " + String.valueOf(res));                            
            return true;
        }
        
        if (cmd.getName().equalsIgnoreCase("uvkick") ) {
            if ( args.length < 1 )
                { p.sendMessage ( ChatColor.RED + "Too few arguments."); return true; }
            List<Player> mayKick = getServer().matchPlayer(args[0]);
            
            if ( mayKick == null || mayKick.isEmpty() ) {
                p.sendMessage(ChatColor.RED + "Theres no player called '" + args[0] + "'."); return true;
            }
            
            if ( mayKick.size() > 1 ) {
                p.sendMessage(ChatColor.DARK_AQUA + "There are some players matching '" + args[0] + "'");
                String plist = "";
                for ( Player toKick : mayKick ) {                        
                    plist += ChatColor.GRAY + toKick.getName() + ( (mayKick.indexOf(toKick) != (mayKick.size() -1)) ? ChatColor.DARK_GRAY + ", " : "" );
                }
                p.sendMessage(plist);
                return true;
            } else {    // Got ONE player
                String reason = "";
                for ( int i = 1; i < args.length; i++ )
                    reason += args[i].trim();
                MResult res;
                if ( (res = api.doKick(p, mayKick.get(0), ( (args.length >= 2) ? reason : "No reason provided." ))) == MResult.RES_SUCCESS) {
                    int c = ownBroadcast(ChatColor.GOLD + "UV: " + ChatColor.AQUA + mayKick.get(0).getName() + ChatColor.DARK_AQUA + " kicked by " + ChatColor.AQUA + p.getName() + ChatColor.DARK_AQUA + ". Reason: " + ChatColor.AQUA + ( (args.length >= 2) ? reason : "No reason." ));
                    p.sendMessage("sent to " + c + " receivers");
                } else {
                    p.sendMessage(ChatColor.RED + "Can't kick player: " + res.toString());
                }
                return true;
            }            
        }
        
        if (cmd.getName().equalsIgnoreCase("uvpraise")) {
            if ( args.length < 1 ) {
                p.sendMessage(ChatColor.RED + "Too few arguments."); return true;
            }            
            List<Player> mayPraise = getServer().matchPlayer(args[0]);
            
            if ( mayPraise == null || mayPraise.isEmpty() ) {
                p.sendMessage(ChatColor.RED + "Theres no player called '" + args[0] + "'."); return true;
            }
            
            if ( mayPraise.size() > 1 ) {
                p.sendMessage(ChatColor.DARK_AQUA + "There are some players matching '" + args[0] + "'");
                String plist = "";
                for ( Player toPraise : mayPraise ) {                        
                    plist += ChatColor.GRAY + toPraise.getName() + ( (mayPraise.indexOf(toPraise) == (mayPraise.size() -1)) ? ChatColor.DARK_GRAY + ", " : "" );
                }
                p.sendMessage(plist);
                return true;
            } else {    // Got ONE player
                String reason = "";
                for ( int i = 1; i < args.length; i++ )
                    reason += args[i].trim();
                MResult res;
                if ( (res = api.doKick(p, mayPraise.get(0), ( (args.length >= 2) ? reason : "No reason provided." ))) == MResult.RES_SUCCESS) {
                    ownBroadcast(ChatColor.AQUA + "Player " + mayPraise.get(0).getName() + " has been kicked." );                    
                } else {
                    p.sendMessage(ChatColor.RED + "Can't kick player: " + res.toString());
                }
                return true;
            }  
        }

        return false;
    }

//    public List<String> getAllPlayerNames() {
//        List<String> res = new ArrayList<String>();
//        for (int i = 0; i < players.size(); i++) {
//            res.add(players.get(i).getName());
//        }
//        
//        return res;                
//    }
    
    public MAuthorizer getAuthorizer () {
        return auth;
    }
    
    public int ownBroadcast (String message) {
        Player[] ps = getServer().getOnlinePlayers();                
        int cnt = 0;
        for ( Player p : ps ) {
            p.sendMessage(message);
            cnt++;
        }
        getServer().getConsoleSender().sendMessage(message);
        cnt++;
        return cnt;
    }

// ------------ HELP FUNCTIONS ---------------
    
    // Note: - Log function replaced with Mighty Logging engine
    //       - Configuration help functions replaced with Mighty Configuration Wrap        
        
}


// EOF
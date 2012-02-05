/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 *      TODO: Look at github. 
 * 
 * 
 */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.JMessage.JMessage;
import com.prosicraft.ultravision.base.UVChatListener;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.chat.MCChatListener;
import com.prosicraft.ultravision.chat.UVServer;
import com.prosicraft.ultravision.commands.banCommand;
import com.prosicraft.ultravision.commands.commandResult;
import com.prosicraft.ultravision.commands.kickCommand;
import com.prosicraft.ultravision.commands.praiseCommand;
import com.prosicraft.ultravision.commands.tempbanCommand;
import com.prosicraft.ultravision.commands.unbanCommand;
import com.prosicraft.ultravision.commands.unwarnCommand;
import com.prosicraft.ultravision.commands.warnCommand;
import com.prosicraft.ultravision.global.globalEngine;
import com.prosicraft.ultravision.local.localEngine;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import com.prosicraft.ultravision.util.MConst;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class ultravision extends JavaPlugin {

    private MConfiguration config = null;        
    private PluginDescriptionFile fPDesc = null;
    
    private uvPlayerListener playerListener = null;
    private uvblocklistener blockListener = null;    
            
    private UVServer uvserver = null;
    private MCChatListener uvchatlistener = null;
    
    private JMessage jmsg = null;
    
    private UltraVisionAPI api = null;
    private boolean global = false;
    
    private boolean useMineconnect = false;     // Mineconnect Template
    private boolean useJMessage = false;        // Join Message Template
    private boolean useAuthorizer = true;       // Authorizer / Login Template
    private boolean useCommandLog = true;       // Command logging     
    private boolean showNotRegWarning = true;    // Show "NotRegistered" Warning on join
    
        
    // ======================== AUTHENTICATION SECTION
    private MAuthorizer auth = null;           

    @Override
    public void onEnable() {
        // Load Plugin Description
        fPDesc = this.getDescription();

        MLog.i("Ultravision is starting (Version " + fPDesc.getVersion() + ")"  /*+ " #b" + MCrypt.prependZeros(ResourceBundle.getBundle("version").getString("BUILD")) + ") ..."*/);                                               
        
        // Load config file        
        try {
            initConfig();            
        } catch (Exception ex) {
            MLog.e("FATAL: " + ex.toString());
            ex.printStackTrace();
            return;
        }
        
        loadTemplateSelection ();
        
        initAuthorizer();   // Authorizer template
        initJMessage ();    // JMessage template

        // Hook event listeners
        initEvents ();                                
        
        MLog.d("Starting engine...");   
                
        
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
        
        if ( api != null ) {
            
            for ( Player p : getServer().getOnlinePlayers() ) {
                api.playerJoin(p);
            }                
            
        }
        
        playerListener.initUV(api);
        
        if ( !useMineconnect ) {
            MLog.i("Mineconnect is disabled in configuration.");            
        } else {
        
            MLog.d("Starting Mineconnect server...");
        
            uvchatlistener = new MCChatListener () {
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

            MLog.i("Started Mineconnect server.");
        }
        
    }       
    
    @Override
    public void onDisable() {
        MLog.i("Ultravision is being shutdown. (Updating Config...)");

        if ( auth != null) auth.save();                      
        config.setDefault("auth.showMessagesNotLoggedIn", true);
        
        if ( jmsg != null)
            jmsg.save(config);
        
        config.set("general.savestats", config.getBoolean("general.savestats", true));        

        updateConfig();
                
        
        if ( api != null ) {
            
            for ( Player p : getServer().getOnlinePlayers() ) {
                api.playerLeave(p);
            }
            
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
            MLog.i("Server already shut down or disabled.");
    }

    private void loadTemplateSelection () {                
        
        config.set( "general.useGlobalAPI", (global = config.getBoolean("useGlobalAPI", false)) );
        config.set( "general.useAuthorizer", (useAuthorizer = config.getBoolean("general.useAuthorizer", true)) );
        config.set( "general.useJMessage", (useJMessage = config.getBoolean("general.useJMessage", false)) );
        config.set( "general.useCommandLog", (useCommandLog = config.getBoolean("general.useCommandLog", true)) );
        config.set( "general.logFileLimitKByte", (MConst._LIMIT_A = config.getInt("general.logFileLimitKByte", MConst._LIMIT_A)) );
        config.set( "general.showNotRegWarning", (showNotRegWarning = config.getBoolean("general.showNotRegWarning", true)) );
        config.set( "general.debug", (MConst._DEBUG_ENABLED = config.getBoolean("general.debug", false)) );
        
    }
    
    private void initAuthorizer () {
        
        if ( !useAuthorizer ) {
            MLog.i("Authorizer not used!");
            return;
        }            
        
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
        
        // Initialize Authorizer
        (auth = new MAuthorizer (authFile.getAbsolutePath())).a();  
    }
    
    private void initJMessage () {
        
        if ( jmsg != null || !useJMessage ) {
            MLog.d ("jmsg = " + String.valueOf(jmsg)) ;                   
            MLog.i("JMessage is disabled in configuration file.");
            return;
        }        
        
        (jmsg = new JMessage ( config ) {
            @Override
            public void broadcast(String txt) {
                getServer().broadcastMessage(txt);
            }            
        }).init(this);
        
        
        
        jmsg.save(config);
        
    }
    
    private void initEvents () {
        playerListener = new uvPlayerListener(this);
        blockListener = new uvblocklistener (this);

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        //pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);                
        //pm.registerEvent(Type.PLAYER_LOGIN, playerListener, Priority.Low, this);
        //pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Lowest, this);        
        //pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Low, this);
        //pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Low, this);
        //pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Low, this);
        
        if ( auth != null )
            pm.registerEvents(new UVChatListener(this), this);
            //pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Low, this);
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
    }

    private void updateConfig() {
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
    public boolean playerJoin(Player p) {
        
        boolean res = true;
        
        if ( p == null )
            return true;    // not false, otherwise it would crash                        
            
            api.playerJoin(p);
            
            if ( api.isBanned(p) ) {                
                return false;       
            }                                              

            config.save();
            
            MLog.d("Player joined successfully: " + p.getName() );                        
            
            if ( uvserver != null )
                uvserver.sendMessage(p.getName() + " joined the server via Minecraft.");

            checkPlayers();
            
            return res;
            
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
            
            api.playerLeave(p);
            
            if ( auth.loggedIn(p) )
                auth.logout(p);                                                                       
            
            if ( uvserver != null )
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
    }

    public void checkPlayers() {
    }

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

        if ((sender instanceof Player)) {             

            Player p = (Player) sender;        

            if ( !auth.loggedIn(p) && !cmd.getLabel().equalsIgnoreCase("uvlogin") ) {
                p.sendMessage(ChatColor.RED + "You are not logged in.");
                return true;
            }           

            if (cmd.getName().equalsIgnoreCase("ultravision")) {
                p.sendMessage(ChatColor.GOLD + fPDesc.getDescription() + " Running Version " + fPDesc.getVersion());
                p.sendMessage("Commands: supervise, uvclear, uvlist, uvdel, uvset");
            }

            if (cmd.getName().equalsIgnoreCase("jmessage")) {
                if ( args.length == 0 || (!args[0].equalsIgnoreCase("reload") && !args[0].equalsIgnoreCase("assign") ) ) {
                    p.sendMessage(ChatColor.RED + "Command not recognized or too few arguments.");
                    return false;
                }               
                if ( jmsg == null ) {
                    p.sendMessage(ChatColor.RED + "JMessage is disabled.");
                    return true;
                }
                if ( args[0].equalsIgnoreCase("reload") ) {
                    jmsg.load(config);
                    p.sendMessage(ChatColor.GREEN + "Reloaded JMessage config.");
                    return true;
                } else {
                    if ( args.length < 3 ) {
                        p.sendMessage(ChatColor.RED + "Too few arguments.");
                        return false;
                    }   
                    String thetxt = "";
                    for ( int n = 2; n < (args.length); n++ )
                        thetxt += args[n] + " ";                                
                    jmsg.assignIndividual(args[1], thetxt.trim());
                    p.sendMessage(ChatColor.GREEN + "Assigned join message to '" + args[1] + "' successfully.");
                    return true;
                }
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
                    p.sendMessage(ChatColor.GREEN + "Login with " + ChatColor.GOLD + "/login YourPassword" + ChatColor.GREEN + ".");
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
                if ( (new kickCommand(this, args)).run(p) == commandResult.RES_SUCCESS )                
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvwarn") ){
                if ( (new warnCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvunwarn") ){
                if ( (new unwarnCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvpraise") ) {
                if ( (new praiseCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvban") ) {
                if ( (new banCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvunban") ) {
                if ( (new unbanCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvtempban") ) {
                if ( (new tempbanCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            }
            
        } else {    // Running command from console                      
            MLog.i("There are no commands for console yet.");
        }
        
        return false;       
    }
    
    public MAuthorizer getAuthorizer () {
        return auth;
    }
    
    public UltraVisionAPI getAPI () {
        return api;
    }
    
    public MConfiguration getMConfig () {
        return config;
    }
    
    public boolean showNotRegWarning () {
        return showNotRegWarning;
    }
    
    public boolean useCommandLog () {
        return useCommandLog;
    }
    
    public int ownBroadcast (String message) {
        Player[] ps = getServer().getOnlinePlayers();                
        int cnt = 0;
        for ( Player p : ps ) {
            p.sendMessage(message);
            cnt++;
        }        
        cnt++;
        return cnt;
    }

// ------------ HELP FUNCTIONS ---------------
    
    // Note: - Log function replaced with Mighty Logging engine
    //       - Configuration help functions replaced with Mighty Configuration Wrap        
        
}


// EOF
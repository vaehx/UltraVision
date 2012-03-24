/*  ============================================================================
 * 
 *      U L T R A V I S I O N  ---  P l a y e r   S u p e r v i s i o n
 * 
 *       This Bukkit Plugin provides functionality for every security purposes.
 * 
 *                              by prosicraft  ,   (c) 2012
 * 
 *  ============================================================================
 */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.JMessage.JMessage;
import com.prosicraft.ultravision.base.UVChatListener;
import com.prosicraft.ultravision.base.UVClickAuth;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.chat.MCChatListener;
import com.prosicraft.ultravision.chat.UVServer;
import com.prosicraft.ultravision.commands.*;
import com.prosicraft.ultravision.global.globalEngine;
import com.prosicraft.ultravision.local.localEngine;
import com.prosicraft.ultravision.util.*;
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

/** ============================================================================
 *
 *          T h e   M a i n   C l a s s
 * 
    ==========================================================================*/
public class ultravision extends JavaPlugin {

    private MConfiguration config           = null;     // Ultravision Configuration        
    private PluginDescriptionFile fPDesc    = null;     // Plugin description file
    
    private uvPlayerListener playerListener = null;     // The Main Listener
    private uvblocklistener blockListener   = null;     // Block listener for auth
            
    private UVServer uvserver               = null;     // The Mineconnect server
    private MCChatListener uvchatlistener   = null;     // Mineconnect listener
    
    private JMessage jmsg                   = null;     // JMessage Template
    private MAuthorizer auth                = null;     // Authorizer Template
    private UVClickAuth clickauth           = null;     // ClickAuth Template
    
    private UltraVisionAPI api              = null;     // Ultravision API Instance
    private boolean global                  = false;    // use global api ?
    
    private boolean useMineconnect          = false;    // Mineconnect Template
    private boolean useJMessage             = false;    // Join Message Template
    private boolean useAuthorizer           = true;     // Authorizer / Login Template
    private boolean useCommandLog           = true;     // Command logging     
    private boolean showNotRegWarning       = true;     // Show "NotRegistered" Warning on join                    

    
    // =========================================================================
    //      Ultravision::onEnable()
    @Override
    public void onEnable() {
        
        // Startup info
        fPDesc = this.getDescription();
        MLog.i("Ultravision is starting (Version " + fPDesc.getVersion() + ")"  /*+ " #b" + MCrypt.prependZeros(ResourceBundle.getBundle("version").getString("BUILD")) + ") ..."*/);                                               
        
        // Load config file        
        try {
            initConfig();            
        } catch (Exception ex) {
            MLog.e("FATAL: " + ex.toString());
            ex.printStackTrace(System.out);
            return;
        }
        
        loadTemplateSelection ();
        
        initAuthorizer();   // Authorizer template
        initJMessage ();    // JMessage template                                       
        
        MLog.d("Starting engine...");   
                
        
        if ( !global ) {
            
            api = new localEngine ( this.getDataFolder().getAbsolutePath() );            
            MLog.i("Using Local Engine. Version: " + UltraVisionAPI.version);                                                
            
            MResult tr;
            if ( (tr = api.registerAuthorizer(auth)) == MResult.RES_SUCCESS )
                MLog.i("Authorizer hooked into Engine.");
            else 
                MLog.e("Authorizer can't hook into Engine: " + tr.toString());                                                
            
        } else  {
            
            api = new globalEngine ();
            MLog.i("Using global Engine. Version: " + UltraVisionAPI.version);
            MLog.w("Global Engine isn't supported yet.");
            
            MResult tr;
            if ( (tr = api.registerAuthorizer(auth)) == MResult.RES_SUCCESS )
                MLog.i("Authorizer hooked into Engine.");
            else 
                MLog.e("Authorizer can't hook into Engine: " + tr.toString());
            
        }   
        
        if ( api != null ) {
            
            for ( Player p : getServer().getOnlinePlayers() ) {
                api.playerLogin(p);
            }                
            
        }
        
        // Hook event listeners                
        initEvents ();
        playerListener.initUV(api);
        
        clickauth = new UVClickAuth (api, this, config.getBoolean("auth.showMessagesNotLoggedIn", true));
        clickauth.init();
        
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
        
        // For Reload: Do Rejoin everybody
        rejoin();        
        
    }       
    
    // =========================================================================
    //      Ultravision::onDisable()
    @Override
    public void onDisable() {
        MLog.i("Ultravision is being shutdown. (Updating Config...)");

        if ( auth != null) auth.save();                      
        if ( clickauth != null ) clickauth.saveToFile();       
        config.setDefault("auth.showMessagesNotLoggedIn", true);
        config.setDefault("ultravision.showWarnedMessages", true);
        
        if ( jmsg != null)
            jmsg.save(config);
        
        config.set("general.savestats", config.getBoolean("general.savestats", true));                               
        
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

    // =========================================================================
    //      Load the template selection from the configuration
    private void loadTemplateSelection () {                
        
        config.set( "general.useGlobalAPI", (global = config.getBoolean("useGlobalAPI", false)) );
        config.set( "general.useAuthorizer", (useAuthorizer = config.getBoolean("general.useAuthorizer", true)) );
        config.set( "general.useJMessage", (useJMessage = config.getBoolean("general.useJMessage", false)) );
        config.set( "general.useCommandLog", (useCommandLog = config.getBoolean("general.useCommandLog", true)) );
        config.set( "general.logFileLimitKByte", (MConst._LIMIT_A = config.getInt("general.logFileLimitKByte", MConst._LIMIT_A)) );
        config.set( "general.showNotRegWarning", (showNotRegWarning = config.getBoolean("general.showNotRegWarning", true)) );
        config.set( "general.debug", (MConst._DEBUG_ENABLED = config.getBoolean("general.debug", false)) );
        
    }
    
    // =========================================================================
    //      Initialize MAuthorizer if used
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
    
    // =========================================================================
    //      Initialize JMessage if used
    private void initJMessage () {
        
        if ( jmsg != null || !useJMessage ) {            
            MLog.i("JMessage is disabled in configuration file.");
            return;
        }        
        
        (jmsg = new JMessage ( config ) {
            @Override
            public void broadcast(String txt) {
                ownBroadcast(txt);
            }            
        }).init(this, auth);
        
        
        
        jmsg.save(config);
        
    }
    
    // =========================================================================
    //      Register Events and assign templates
    private void initEvents () {
        playerListener = new uvPlayerListener(this);
        blockListener = new uvblocklistener (this);

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(new UVChatListener(this), this);            
    }
    
    // =========================================================================
    //      Initialize Main-Configuration file of ultravision plugin
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
    }  

    // =========================================================================
    //      Clears whole configuration file.
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
            
            config.save();

            return true;
        } catch (Exception ex) {
            MLog.e("Failed to reload Ultravision: " + ex.getMessage());
            return false;
        }
    }

    // =========================================================================
    //      When a players tries to join the game...    
    public boolean playerJoin(Player p) {
        
        boolean res = true;
        
        if ( p == null )
            return true;    // not false, otherwise it would crash                        
                   
        api.playerLogin(p);            

        if ( api.isBanned(p) )                
            return false;                                                                 

        config.save();

        MLog.d("Player joined successfully: " + p.getName() );                        

        if ( uvserver != null )
            uvserver.sendMessage(p.getName() + " joined the server via Minecraft.");           

        return res;
            
    }       

    // =========================================================================
    //      When a player says goodbye
    public boolean playerLeave(Player p) {
        if (p != null) {                                                                               
            
            api.playerLeave(p);
            
            if ( auth.loggedIn(p) )
                auth.logout(p);                                                                       
            
            if ( uvserver != null )
                uvserver.sendMessage(p.getName() + " left the channel via Minecraft.");                                             
        
        }
        return false;
    }        

    // =========================================================================
    //      player chat
    public void playerChat (String playername, String msg) {        
    }
    
    // =========================================================================
    //      Walk through all players after reload
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

    // =========================================================================
    //      Handle ultravision Commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if ((sender instanceof Player)) {             

            Player p = (Player) sender;        

            if ( auth != null && ( !auth.loggedIn(p) && !cmd.getLabel().equalsIgnoreCase("uvlogin") ) ) {
                p.sendMessage(ChatColor.RED + "You are not logged in.");
                return true;
            } else if ( clickauth != null && ( !clickauth.isLoggedIn(p.getName()) && !cmd.getLabel().equalsIgnoreCase("uvlogin") ) ) {
                p.sendMessage(ChatColor.RED + "You are not logged in.");
                return true;
            }         
 
            if (cmd.getName().equalsIgnoreCase("ultravision")) {
                if ( args.length == 1 && args[0].equalsIgnoreCase("reload") ) {
                    config = null;
                    initConfig();
                    loadTemplateSelection();
                    p.sendMessage (ChatColor.GREEN + "Reloaded Config");
                    return true;
                }
                
                p.sendMessage(ChatColor.GOLD + fPDesc.getDescription() + " Running Version " + fPDesc.getVersion());
                String coms = "";
                for ( String lecom : fPDesc.getCommands().keySet() )
                    coms += lecom + ", ";
                coms = coms.substring(0, coms.length() - 2);
                p.sendMessage("Commands: " + coms);
                return true;
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
                MResult res;
                if ( (res =auth.register(p, args[0])) == MResult.RES_SUCCESS ) {
                    p.sendMessage(ChatColor.GREEN + "Registered successfully in login system as " + p.getName() + ".");
                    p.sendMessage(ChatColor.GREEN + "Login with " + ChatColor.GOLD + "/login YourPassword" + ChatColor.GREEN + ".");
                } else MLog.e("Couldn't register new player in login system (player=" + p.getName() + "): " + String.valueOf(res));                                     
                return true;
            }                       
            
            if ( cmd.getLabel().equalsIgnoreCase("uvclickregister") ) {
                if ( clickauth == null ) {
                    p.sendMessage(ChatColor.RED + "The UV-ClickAuth system is not accessible."); return true;                    
                }                   
                if ( clickauth.isRegistered(p.getName()) ) {
                    p.sendMessage(ChatColor.GOLD + "You're already registered in the UV-ClickAuth System."); return true;                    
                }
                if ( clickauth.toggleRegistering(p) ) {
                    p.sendMessage(ChatColor.AQUA + "--- Registering for UV-ChatAuth ---");
                    p.sendMessage(ChatColor.GRAY + " Place Blocks with distance from each other.");
                    p.sendMessage(ChatColor.GRAY + " Remember the location of the Blocks, relatively.");
                    p.sendMessage(ChatColor.GRAY + " Finish by typing " + ChatColor.AQUA + "/caregister" + ChatColor.GRAY + " again.");
                } else {
                    p.sendMessage(ChatColor.AQUA + "--- Finished UV-ClickAuth Registering ---");
                    p.sendMessage(ChatColor.GRAY + " Please Login now.");
                }
                return true;
            }  else if ( cmd.getLabel().equalsIgnoreCase("uvclickunregister") ) {
                if ( clickauth == null ) {
                    p.sendMessage(ChatColor.RED + "The UV-ClickAuth system is not accessible."); return true;                    
                }                   
                if ( args.length < 1 ) {
                    p.sendMessage(ChatColor.RED + "Too few arguments"); return false;
                }
                String thePlayer = args[0];
                if ( !clickauth.isRegistered(p.getName()) ) {
                    p.sendMessage(ChatColor.GOLD + thePlayer + " is not registered in ClickAuth System."); return true;                                        
                }                
                clickauth.unRegister(thePlayer);
                clickauth.saveToFile();
                p.sendMessage(ChatColor.GREEN + thePlayer + " has been unregistered.");
                for ( int i = 0; i < getServer().getOnlinePlayers().length; i++ )
                    if ( getServer().getOnlinePlayers()[i].getName().equalsIgnoreCase(thePlayer) ) {
                        getServer().getOnlinePlayers()[i].sendMessage(ChatColor.GOLD + "You have been unregistered from ClickAuth."); break; }
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
                MResult res;
                if ( (res = auth.unregister(args[0], getServer().getPlayer(args[0]))) == MResult.RES_SUCCESS )
                    p.sendMessage(ChatColor.GREEN + "Unregistered player " + args[0] + " successfully.");
                else
                    p.sendMessage(ChatColor.RED + "Couldn't unregister player " + args[0] + ": " + String.valueOf(res));                            
                return true;
            }                        

            if (cmd.getName().equalsIgnoreCase("uvkick") ) {
                if ( (new kickCommand(this, args)).run(p) == commandResult.RES_SUCCESS )                
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvbackendkick") ) {
                if ( (new backendkickCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvwarn") ) {
                if ( (new warnCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvunwarn") ){
                if ( (new unwarnCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvpraise") ) {
                if ( (new praiseCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvunpraise") ) {
                if ( (new unpraiseCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
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
            } else if ( cmd.getName().equalsIgnoreCase("uvgc") ) {
                if ( (new gcCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvnote") ) {
                if ( (new noteCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvdelnote") ) {
                if ( (new delnoteCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvaddfriend") ) {
                if ( (new addfriendCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvacceptfriend")) {
                if ( (new accfriendCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvdelfriend") ) {
                if ( (new delfriendCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvstat") ) {
                if ( (new statCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvingamelog") ) {
                if ( (new ingamelogCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvsay") ) {
                if ( (new sayCommand(this, args)).run(p) == commandResult.RES_SUCCESS )
                    return true;
            }
            
        } else {    // Running command from console                      
           
            if ( cmd.getName().equalsIgnoreCase("ultravision") ) {
                MLog.i("Running UltraVision " + fPDesc.getVersion() + ".");               
            }
            
            if (cmd.getName().equalsIgnoreCase("uvkick") ) {
                if ( (new kickCommand(this, args)).consoleRun(sender) == commandResult.RES_SUCCESS )                
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvbackendkick") ) {
                if ( (new backendkickCommand(this, args)).consoleRun(sender) == commandResult.RES_SUCCESS )
                    return true;
            } else if ( cmd.getName().equalsIgnoreCase("uvsay") ) {
                if ( (new sayCommand(this, args)).consoleRun(sender) == commandResult.RES_SUCCESS )
                    return true;
            }
            
        }
        
        return false;       
    }
    
    // =========================================================================
    //                      G E T    I N S T A N C E S
    public MAuthorizer getAuthorizer () {
        return auth;
    }
    
    public UVClickAuth getClickAuth () {
        return clickauth;
    }
    
    public UltraVisionAPI getAPI () {
        return api;
    }
    
    public JMessage getMessager () {
        return jmsg;
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
    
    // =========================================================================
    //          Better Broadcast, that won't crash or show the message twice
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
    
    // Note: - Log function replaced with Mighty Logging engine
    //       - Configuration help functions replaced with Mighty Configuration Wrap        
        
}


// EOF
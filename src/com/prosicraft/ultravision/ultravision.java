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
    private List<uvPlayer> players = new ArrayList<uvPlayer>();
    private List<uvManager> managers = new ArrayList<uvManager>();
    private PluginDescriptionFile fPDesc = null;
    private uvPlayerListener playerListener = null;
    private List<String> possibleTargetFlags = Arrays.asList("chat", "move");
    
    
        
    // ======================== AUTHENTICATION SECTION
    private MAuthorizer auth = null;    
    
    @Override
    public void onDisable() {
        MLog.i("Ultravision is being shutdown. (Updating Config...)");

        auth.save();
        
        config.set("general.savestats", config.getBoolean("general.savestats", true));

        updateConfig();

        config.save();

        players = null;
        managers = null;
        playerListener = null;
        fPDesc = null;
        config = null;
    }

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
            if (config.getBoolean("general.savestats", true)) {
                this.initSaves();
            }
        } catch (Exception ex) {
            MLog.e("FATAL: " + ex.toString());
            ex.printStackTrace();
            return;
        }


        playerListener = new uvPlayerListener(this);

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);                
        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.High, this);
        pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.High, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.High, this);       
        
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

        Set<String> pMap = config.getKeys("users");
        if (pMap != null) {
            for ( String s1: pMap ) {
                //log("Found key: " + pMap.get(i));
                registerPlayer(s1);
            }
        }
    }

    private void updateConfig() {
        if (config.getBoolean("general.savestats", true)) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).isOnline) playerLeave(players.get(i).getBase());
            }
        }
    }

    public boolean clearConfig() {

        try {
            config.clear();
            config = null;
            players.clear();
            managers.clear();

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
            Player[] p = getServer().getOnlinePlayers();
            for (int i = 0; i < p.length; i++) {
                playerJoin(p[i]);
            }
            
            config.save();

            return true;
        } catch (Exception ex) {
            MLog.e("Failed to reload Ultravision: " + ex.getMessage());
            return false;
        }
    }

    // When a player joins the game...
    public void playerJoin(Player p) {
        if (config.getBoolean("general.savestats", true)) {                        
            
            uvPlayer uP = getUvPlayer(p.getName());                        
            
            if (uP == null) {
                uP = new uvPlayer(p);
                players.add(uP);
            }
            else uP.setBase(p);
            
            uP.join();
            
            if ( !auth.isRegistered(p) )
                uP.sendMessage (ChatColor.YELLOW + "Warning: You're not registered in the login system yet!");
                        
            // Load Properties, if User is a Manager
            if (config.getBoolean(uP.getPath() + "isManager", false)) {
                
                uvManager uM = new uvManager(p, config.getStringList(uP.getPath() + "targets", null));
                uM.setPlayerInstance(uP);                
                managers.add(uM);                 
                
            }

            // Load Supervising Flags if User is a Target of a manager
            if (config.getBoolean(uP.getPath() + "isTarget", false)) {                                                
                
                uP.setAsTarget(true);
                
                // Get all Manager keys and load flags per Manager
                Set<String> lKeys = config.getKeys(uP.getPath() + "flags");                
                
                //log ("Load " + lKeys.size() + " Manager Keys for Player " + uP.getName());
                
                for ( String s1 : lKeys ) {                                        

                    uP.appendManager(s1, getuvManager(getUvPlayer(s1)));
                    
                    List<String> lFlags = config.getStringList(uP.getPath() + "flags." + s1, null);                                        
                    
                    if (lFlags != null) {                        
                        for (int n = 0; n < lFlags.size(); n++)
                            if (this.possibleTargetFlags.contains(lFlags.get(n)))
                                uP.setFlag(this.possibleTargetFlags.indexOf(lFlags.get(n)), s1);                                                    
                    }
                }
                
            }                                    
            
            config.set(uP.getPath() + "jointime", uP.getJoinTime());

            config.save();
            
            MLog.d("Player joined successfully: " + uP.getName() + ", " + uP.getBase().toString());

            checkPlayers();
        }
    }

    // Register a user without being logged in
    public uvPlayer registerPlayer(String name) {
        if (config.getBoolean("general.savestats", true)) {

            uvPlayer uP = new uvPlayer(null);
            uP.name = name;
            players.add(uP);
            return uP;

        } else {
            return null;
        }
    }

    // When a player says goodbye...
    public boolean playerLeave(Player p) {
        if (config.getBoolean("general.savestats", true)) {
            
            uvPlayer uP = getUvPlayer(p);            
            
            if (uP == null || !uP.base.equals(p)) {
                MLog.e("Failure: " + p.getName() + " logged out without being logged in.");
                return false;
            }
            
            uP.leave();
            
            if ( auth.loggedIn(p) )
                auth.logout(p); 

            // Save general user data
            config.set(uP.getPath() + "name", uP.getName());
            config.set(uP.getPath() + "leavetime", uP.getLeaveTime());
            long onlineTime = config.getLong(uP.getPath() + "onlineTime", 0) + uP.getOnlineTime();
            config.set(uP.getPath() + "onlineTime", onlineTime);

            config.set(uP.getPath() + "isTarget", uP.isTarget());

            // Save Manager Status
            if (uP.isManager) {
                config.set(uP.getPath() + "isManager", true);
                uvManager uM = this.getuvManager(uP);                 
                if (uM != null && uM.numVisionTargets() > 0) {
                    config.set(uP.getPath() + "targets", uM.getVisionTargets());
                }
            }

            // Save target flags
            if (uP.isTarget() && uP.numManagers() > 0) {                                                               
                for (int m=0; m < uP.numManagers(); m++) {
                    List<String> lFlags = new ArrayList<String>();
                    uvManager uM = uP.getManager(m);                    
                    if (uP.numTrueFlags(uM.getName()) > 0) {                        
                        for (int f=0;f<this.possibleTargetFlags.size(); f++) {
                            if (uP.getFlag(f, uM.getName())) {
                                lFlags.add(this.possibleTargetFlags.get(f));                                
                            }
                        }
                    }                                        
                    config.set(uP.getPath() + "flags." + uM.getName(), lFlags);                                        
                }                                                    
            }

            if (uP.isManager) managers.remove(this.getuvManager(uP));
            players.remove(uP);            
        
        }
        return false;
    }

    public void checkPlayers() {
        /*MLog.e((players != null) ? players.toString() : "PLAYERS IS NULL");
        for (int i = 0; i < players.size(); i++) {
            MLog.d(String.valueOf(i) + players.get(i).getClass().toString());
            MLog.d(String.valueOf(i) + String.valueOf(players.get(i).getBase()));
        }*/
    }

    public uvPlayer getUvPlayer(Player p) {
        try
        {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getName().equalsIgnoreCase(p.getName())) {
                    return players.get(i);
                }
            }
            return null;
        }
        catch (Exception ex) { 
            MLog.e("Failure: User " + p.getName() + " wasn't found in players");
            return null;
        }
    }

    public uvPlayer getUvPlayer(String name) {                
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equalsIgnoreCase(name)) {
                return players.get(i);
            }
        }
        return null;
    }
    
    public uvManager getuvManager (uvPlayer base) {
        if (managers != null && managers.size() > 0) {
            for (int i=0;i<managers.size();i++) {
                if (managers.get(i).getPlayerInstance().equals(base))
                    return managers.get(i);
            }
            return null;
        }
        else return null;
    }

    // Check if this Flag is set to true
    public boolean hasFlags(uvPlayer p, String flag) {        
        if (this.possibleTargetFlags.contains(flag) && p.numManagers() > 0) {
            int fID = this.possibleTargetFlags.indexOf(flag);   // the Flag ID
            for (int i=0;i<p.numManagers();i++) {
                if (p.getFlag(fID, p.getManager(i).getName()))
                    return true;
            }
            return false;
        } else return false;
    }

    public void updateVision(String msg, String pName, String flag) {
        uvPlayer uP = getUvPlayer(pName);
        if (uP != null && uP.numManagers() > 0 && this.possibleTargetFlags.contains(flag)) {
            for (int m=0;m<uP.numManagers();m++) {
                if (uP.getFlag(possibleTargetFlags.indexOf(flag), uP.getManager(m).getName()))
                    uP.getManager(m).sendMessage(ChatColor.GRAY + "[UV] " + ChatColor.WHITE + pName + " - " + ChatColor.GOLD + msg);
            }            
        }       
    }

    private boolean initSaves() {

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

        Player pBase = (Player) sender;
        uvPlayer p = getUvPlayer(pBase);

        if (p == null) {
            p.sendMessage(ChatColor.RED + "You are not authorized.");
            return true;
        }

        //log("Player " + p.getName() + ": (BASE = " + p.getBase().toString() + ")");

        if (cmd.getName().equalsIgnoreCase("ultravision")) {
            p.sendMessage(ChatColor.GOLD + fPDesc.getDescription() + " Running Version " + fPDesc.getVersion());
            p.sendMessage("Commands: supervise, uvclear, uvlist, uvdel, uvset");
        }
        
        if ( cmd.getLabel().equalsIgnoreCase("uvlogin") ) {
            if ( !auth.isRegistered(pBase) ) {
                p.sendMessage(ChatColor.GOLD + "You're not registered in the login system.");
                return true;
            }                
            if ( auth.loggedIn(pBase) ) {
                p.sendMessage(ChatColor.RED + "You're already logged in."); return true;                
            }
            // kick on wrong, or not set password
            if ( args.length != 1 ) {
                p.base.kickPlayer(MLog.real(ChatColor.RED + "Wrong password!")); return true;               
            }            
            if ( auth.login(p.getBase(), args[0]) == MResult.RES_NOACCESS ) {
                p.base.kickPlayer(MLog.real(ChatColor.RED + "Wrong password!")); return true;
            } else {
                p.sendMessage(ChatColor.GREEN + "Logged in successfully."); return true;
            }
        }
        
        if ( cmd.getLabel().equalsIgnoreCase("uvregister") ) {
            if ( auth.isRegistered(pBase) ) {
                p.sendMessage(ChatColor.GOLD + "You're already registered in the login system."); return true;               
            }
            if ( args.length != 1 ) {
                p.sendMessage(ChatColor.RED + "Please specify a password."); return true;               
            }
            MResult res = null;
            if ( (res =auth.register(pBase, args[0])) == MResult.RES_SUCCESS ) {
                p.sendMessage(ChatColor.GREEN + "Registered successfully in login system as " + pBase.getName() + ".");
            } else MLog.e("Couldn't register new player in login system (player=" + pBase.getName() + "): " + String.valueOf(res));                                     
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("supervise")) {
            // check for args
            int cnt = 0;
            uvPlayer uP;
            if (args.length > 0) { // visionize only the given Player list
                
                uvManager uM = new uvManager (p.getBase(), Arrays.asList(args));
                uM.setPlayerInstance(p);
                managers.add(uM);                
                
                cnt = 0;
                for (int i = 0; i < args.length; i++) {                     
                    uP = getUvPlayer(getServer().getPlayer(args[i]));                                                            
                    if ((uP instanceof uvPlayer) && uP != null) {                                                                            
                            uP.setAsTarget(true);
                            uP.appendManager(uM.getName(), uM); 
                            MLog.d("Trying to add Vision target (" + uP.getName() + ") to " + uM.getName());                       
                            uM.addVisionTarget(uP.getName());
                            cnt++;                        
                    }
                }
                
                if (cnt > 0)
                    p.sendMessage(ChatColor.GREEN + "You're now supervising " + cnt + " more user" + ((args.length == 1) ? "." : "s."));

                if (cnt != args.length) {
                    int offset = args.length - cnt;
                    p.sendMessage(ChatColor.RED + String.valueOf(offset) + " players aren't logged in, where never on the server or you're already supervising.");
                }
            } else {            // actually supervise every online Player
                List<String> pNames = getAllPlayerNames();
                
                uvManager uM = new uvManager (p.getBase(), pNames);
                uM.setPlayerInstance(p);
                managers.add(uM);

                cnt = 0;
                for (int i = 0; i < pNames.size(); i++) {
                    uP = getUvPlayer(getServer().getPlayer(pNames.get(i)));
                    if ((uP instanceof uvPlayer) && uP != null) {                        
                            cnt++;
                            uP.setAsTarget(true);
                            uP.appendManager(uM.getName(), uM);
                            uM.addVisionTarget(uP.getName());                        
                    }
                }

                p.sendMessage(ChatColor.GREEN + "You're now supervising " + cnt + " user" + ((args.length == 1) ? "." : "s."));

                if (cnt != pNames.size()) {
                    int offset = pNames.size() - cnt;
                    p.sendMessage(ChatColor.RED + String.valueOf(offset) + " players aren't logged in, where never on the server or you're already supervising.");
                }
            }
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
            if (p.isManager) {
                uvManager uM = getuvManager (p);
                p.sendMessage(ChatColor.GOLD + "You're supervising " + uM.numVisionTargets() + " users:");
                for (int i = 0; i < uM.numVisionTargets(); i++) {
                    uvPlayer uP = getUvPlayer(uM.getVisionTarget(i));
                    if (uP != null) {
                        String lFlags = (uP.numTrueFlags(uM.getName()) > 0) ? "" : "No Flags";                    
                        for (int n=0;n<this.possibleTargetFlags.size(); n++)
                            if (uP.getFlag(n,uM.getName()))
                                lFlags += this.possibleTargetFlags.get(n) + ", ";
                        lFlags = lFlags.trim();
                        if (lFlags.endsWith(","))
                            lFlags = lFlags.substring(0, lFlags.length() - 1);
                        p.sendMessage(ChatColor.WHITE + " - " + uM.getVisionTarget(i) + " # " + lFlags);
                    }
                    else {
                        p.sendMessage(ChatColor.WHITE + " - ! Fixed nullpointer entry: " + uM.getVisionTarget(i));
                        uM.removeVisionTarget(uM.getVisionTarget(i));
                    }
                }
            } else {
                p.sendMessage(ChatColor.GOLD + "You are not supervise any user.");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("uvdel") || cmd.getName().equalsIgnoreCase("uvremove") || cmd.getName().equalsIgnoreCase("uvdelete")) {
            if (p.isManager) {
                if (args.length == 0) {
                    p.sendMessage(ChatColor.RED + "Please define username, you don't want to supervise anymore.");
                    return false;
                }
                int cnt = 0;
                uvManager uM = getuvManager(p);
                for (int i = 0; i < args.length; i++) {
                    if (getServer().getPlayer(args[i]) instanceof Player) {
                        uvPlayer uP = getUvPlayer(getServer().getPlayer(args[i]));
                        if (uP != null) {
                            // Player is available, but isn't logged in
                            uP = this.registerPlayer(args[i]);
                        }
                        uM.removeVisionTarget(args[i]);
                        uP.setAsTarget(false);
                        uP.removeManager(uM.getName(), uM);
                        if (config.getBoolean("general.savestats", true)) {
                            config.set(uP.getPath() + "isTarget", false);
                        }
                        cnt++;
                    }
                }
                p.sendMessage(ChatColor.GOLD + "You stopped supervising of " + cnt + " users.");

                if (cnt != args.length) {
                    p.sendMessage(ChatColor.RED + String.valueOf(args.length - cnt) + " users where not found.");
                }

                if (uM.numVisionTargets() <= 0) {                    
                    uM.getPlayerInstance().isManager = false;
                    managers.remove(uM);
                    config.set(uM.getPath() + "isManager", false);
                    p.sendMessage(ChatColor.GOLD + "You are not supervising any users anymore.");
                }

            } else {
                p.sendMessage(ChatColor.RED + "You are not supervising any user.");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("uvset")) {
            if (args.length == 0) {
                p.sendMessage(ChatColor.RED + "Not enough Paramters.");
                return false;
            }
            if (!p.isManager) {
                p.sendMessage(ChatColor.RED + "You are not supervising any user.");
                return true;
            }
            Player sp = getServer().getPlayer(args[0]);
            uvManager uM = getuvManager (p);
            if (!(sp instanceof Player) || sp == null || !uM.hasAsTarget(args[0])) {
                p.sendMessage(ChatColor.RED + "You are not supervising this user or can't find it.");
                return true;
            }
            if (!this.possibleTargetFlags.contains(args[1])) {
                p.sendMessage(ChatColor.RED + "Given flags were not recognized.");
                return true;
            }
            boolean now = getUvPlayer(sp).setFlag(possibleTargetFlags.indexOf(args[1]), uM.getName());
            p.sendMessage(ChatColor.GREEN + "Set Target flag for " + args[0] + ": " + args[1] + " => " + now);
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

        return false;
    }

    public List<String> getAllPlayerNames() {
        List<String> res = new ArrayList<String>();
        for (int i = 0; i < players.size(); i++) {
            res.add(players.get(i).getName());
        }
        
        return res;                
    }
    
    public MAuthorizer getAuthorizer () {
        return auth;
    }

// ------------ HELP FUNCTIONS ---------------
    
    // Note: - Log function replaced with Mighty Logging engine
    //       - Configuration help functions replaced with Mighty Configuration Wrap        
        
}


// EOF
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author passi
 */
public class UVClickAuth {    
    private Map<String, UVClickAuthCode> players = new HashMap<String, UVClickAuthCode>();    
    private Map<String, UVClickAuthCode> registering = new HashMap<String, UVClickAuthCode>();
    private UltraVisionAPI api;
    private UVClickAuthListener listener;
    private JavaPlugin pl;
    private boolean enabled;
    
    public UVClickAuth (UltraVisionAPI uapi, JavaPlugin pl, boolean messages) {
        this.api = uapi;
        this.pl = pl;
        
        listener = new UVClickAuthListener (this, messages);
        pl.getServer().getPluginManager().registerEvents(listener, pl);                
        enabled = false;    // this module needs to be initialized                
    }
    
    public void init () {
        enabled = loadCodeFromFile();        
        // For Reload: Check all online players
        for ( Player p : pl.getServer().getOnlinePlayers() ) {
            
            if ( isRegistered(p.getName()) ) {
                start(p.getName());            
                if ( /*event.getPlayer().getInventory().getSize() == 0*/ true ) {
                    giveBlock(p.getName());
                    p.getInventory().addItem(new ItemStack(Material.DIRT, 1));
                }                
            }            
            
        }
    }
    
    public void start (String pName) {    
        players.get(pName).logging = true;
        MLog.d("Player '" + pName + "' now placing CA Blocks.");
    }
    
    public void stop (String pName) {
        players.get(pName).logging = false;
        players.get(pName).firstLoc = null;
    }
    
    public boolean toggleRegistering (Player p) {
        return toggleRegistering (p, false);
    }
    
    public boolean toggleRegistering (Player p, boolean cancel) {
        String pName = p.getName();
        if ( registering.containsKey(pName) ) {
            if ( !cancel ) {
                if ( registering.get(pName).code.length() <= 16  ) {
                    players.put(pName, new UVClickAuthCode(registering.get(pName).code));
                    players.get(pName).loggedIn = false;
                    saveToFile();
                    start (pName);
                } else {
                    p.sendMessage(ChatColor.RED + "Error: Codes were too long. Cancelled.");
                    p.sendMessage(ChatColor.AQUA + "--- Cancelled UV-ClickAuth Registering ---");
                }                
            }
            registering.remove(pName);
            return false;
        } else {
            registering.put(pName, new UVClickAuthCode(""));
            return true;
        }
    }
    
    public boolean gaveBlock (String pName) {
        return players.get(pName).gaveBlock;
    }
    
    public void giveBlock (String pName) {
        players.get(pName).gaveBlock = true;
        MLog.d("Gave Player '" + pName + "' a block for logging in or register.");
    }
    
    public void takeBlock (String pName) {
        players.get(pName).gaveBlock = false;
        MLog.d("Took Player '" + pName + "' a block as he is no longer online.");
    }
    
    public boolean isRegistered (String pName) {
        return players.containsKey(pName);
    }
    
    public boolean isRegistering (String pName) {
        return registering.containsKey(pName);
    }
    
    public void unRegister (String pName) {
        players.remove(pName);
    }
    
    public boolean isLogging (String pName) {
        if ( players.containsKey(pName) )
            return players.get(pName).logging;
        else return false;
    }
    
    public boolean isLoggedIn (String pName) {
        if ( players.containsKey(pName) )
            return players.get(pName).loggedIn;
        else
            return true;
    }
    
    public void logout (String pName) {
        if (isRegistered (pName))
            players.get(pName).loggedIn = false;
    }
    
    public String rpGetPlayerDirection(Player playerSelf){
         String dir = "";
         float y = playerSelf.getLocation().getYaw();
         if( y < 0 ){y += 360;}         
         y = (y + 45) % 360;         
         int i = (int)((y) / 90);
         if(i == 1){dir = "west";}         
         else if(i == 2){dir = "north";}         
         else if(i == 3){dir = "east";}         
         else if(i == 0){dir = "south";}         
         else {dir = "west";}
         return dir;
    }
    
    public boolean check (Player p, Location loc) {
        String pName = p.getName();        
        
        UVClickAuthCode uvc = (registering.containsKey(p.getName())) ? registering.get(p.getName()) : players.get(pName);
        
        if ( uvc.firstLoc == null ) { // It's the first Block
            if (registering.containsKey(p.getName()))
                registering.get(p.getName()).setFirstLoc(loc);
            else                
                players.get(pName).setFirstLoc(loc);
            return false;
        } else {
            
            int xdiff = 0;
            int zdiff = 0;
            
            if ( rpGetPlayerDirection (p).equalsIgnoreCase("east") ) {
                zdiff = loc.getBlockX() - uvc.firstLoc.getBlockX();
                xdiff = -(loc.getBlockZ() - uvc.firstLoc.getBlockZ());            
            } else if ( rpGetPlayerDirection(p).equalsIgnoreCase("south") ) {
                xdiff = loc.getBlockX() - uvc.firstLoc.getBlockX();
                zdiff = loc.getBlockZ() - uvc.firstLoc.getBlockZ();            
            } else if ( rpGetPlayerDirection(p).equalsIgnoreCase("north") ) {
                xdiff = -(loc.getBlockX() - uvc.firstLoc.getBlockX());
                zdiff = -(loc.getBlockZ() - uvc.firstLoc.getBlockZ());            
            } else if ( rpGetPlayerDirection (p).equalsIgnoreCase("west") ) {
                zdiff = -(loc.getBlockX() - uvc.firstLoc.getBlockX());
                xdiff = loc.getBlockZ() - uvc.firstLoc.getBlockZ();            
            }
            
            //MLog.d("Player Direction: " + rpGetPlayerDirection (p));
            //MLog.d("xdiff: " + xdiff + ", zdiff: " + zdiff);
            //p.sendMessage("xdiff: " + xdiff + ", zdiff: " + zdiff);                                    
            if ( registering.containsKey(p.getName()) ) {                
                if (registering.get(p.getName()).code.length() == 16) {
                    p.sendMessage(ChatColor.RED + "You can't add more Blocks.");
                    //toggleRegistering (p.getName());                    
                } else {
                    registering.get(p.getName()).code += String.valueOf(xdiff) + String.valueOf(zdiff);                                          
                }
                p.sendMessage("Code now: '" + registering.get(p.getName()).code + "'");
                p.sendMessage(ChatColor.GRAY + " Set " + ChatColor.DARK_AQUA + "Block " + ChatColor.AQUA + "#" + (uvc.code.length() / 2) + ChatColor.GRAY + " with " + ChatColor.DARK_AQUA + "xdiff:" + xdiff + " ; zdiff:" + zdiff + ChatColor.GRAY + ".");
            } else {
                boolean firstCorrect = uvc.isCorrect(uvc.pointer, String.valueOf(xdiff));
                boolean secondCorrect = uvc.isCorrect(uvc.pointer, String.valueOf(zdiff));
                if ( !(firstCorrect && secondCorrect) ) {                           
                    uvc.pointer = 0;
                    uvc.firstLoc = null;                    
                    doTheKick(p);                        
                }

                if ( uvc.pointer == uvc.code.length() ) {
                    uvc.pointer = 0;
                    uvc.firstLoc = null;
                    uvc.loggedIn = true;                    
                    p.sendMessage (ChatColor.GREEN + "You've logged in successfully.");
                    stop (p.getName());
                    MLog.d("Gave block: " + gaveBlock(p.getName()));
                    
                    if ( gaveBlock(p.getName()) ) {
                        takeBlock(p.getName());
                        p.getInventory().removeItem(new ItemStack(Material.DIRT, 1));
                    }                        
                    return true;
                }
            }
            
            return false;
        }              
    }
    
    public boolean saveToFile () {
        if ( !pl.getDataFolder().exists() ) {
            if ( pl.getDataFolder().mkdirs() )
                MLog.w("Data Folder of Plugin didn't exist. Created.");
            else {
                MLog.e("Can't create Plugin Data folder for '" + pl.getName() + "'");
                return false;
            }                
        }            
                
        File cFile = new File ( pl.getDataFolder().getAbsolutePath(), "cacodes.cac" );
        
        if ( !cFile.exists() ) {
            try {
                cFile.createNewFile();
            } catch (IOException ioex) {
                MLog.e("Can't create ClickAuth Code Base.");
                return false;
            }
        }                
        
        // Now open input stream
        DataOutputStream fin;
        try {
            fin = new DataOutputStream (new FileOutputStream(cFile));
        } catch (FileNotFoundException ex) {
            MLog.e("Can't open ClickAuth Database for read, at " + MConfiguration.normalizePath(cFile));
            return false;
        }
                                  
        try {
            
            for ( String p : players.keySet() ) {
                                
                fin.write(MAuthorizer.getCharArrayB(p, 16));
                fin.write(MAuthorizer.getCharArrayB(players.get(p).code, 16));                
                
            }
            
            fin.close();                               
        } catch (IOException ex) {
            MLog.e ("Error while saving ClickAuth Database, at " + MConfiguration.normalizePath(cFile));
            return false;
        }
        
        return true;
    }
    
    public boolean loadCodeFromFile () {
        
        if ( !pl.getDataFolder().exists() ) {
            if ( pl.getDataFolder().mkdirs() )
                MLog.w("Data Folder of Plugin didn't exist. Created.");
            else {
                MLog.e("Can't create Plugin Data folder for '" + pl.getName() + "'");
                return false;
            }                
        }            
                
        File cFile = new File ( pl.getDataFolder().getAbsolutePath(), "cacodes.cac" );
        
        if ( !cFile.exists() ) {
            try {
                cFile.createNewFile();
            } catch (IOException ioex) {
                MLog.e("Can't create ClickAuth Code Base.");
                return false;
            }
        }                
        
        // Now open input stream
        DataInputStream fin;
        try {
            fin = new DataInputStream (new FileInputStream(cFile));
        } catch (FileNotFoundException ex) {
            MLog.e("Can't open ClickAuth Database for read, at " + MConfiguration.normalizePath(cFile));
            return false;
        }
                        
        int reads = 0;
        String theName = "";
        byte[] str;        
        try {
            while ( !MStream.isZero( (str = MStream.readStringBuf(fin, 16)) ) && reads < 1000 ) {                                                                                
                                
                if (theName.equalsIgnoreCase(""))
                    theName = (new String(str)).trim();
                else {                    
                    players.put(theName, new UVClickAuthCode ((new String(str)).trim()));
                    theName = "";                
                }
                
                reads++;
            }
            fin.close();            
            if ( reads >= 1000 ) {
                MLog.e(("Whoops, there was too much data in the ClickAuth base."));                
                return  false;
            }                
        } catch (IOException ex) {
            MLog.e ("Error while reading from ClickAuth Database (string:" + reads + "), at " + MConfiguration.normalizePath(cFile));
            return false;
        }
        
        return true;
    }
    
    public void doTheKick (Player p) {
        api.backendKick(p, "You're not permitted to play on this server.");
    }            
}

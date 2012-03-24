/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.JMessage;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import com.prosicraft.ultravision.util.MLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class JMessage {        
    
    private List<String> joinmsg = new ArrayList<String>();
    private List<String> joinmsgpri = new ArrayList<String>();
    private List<String> leavemsg = new ArrayList<String>();
    private Map<String,List<String>> indimsg = new HashMap<String, List<String>>();
    private boolean clearStandard = true;   
    private List<Player> ingamelogger = new ArrayList<Player>();
    private List<String> fakeoffliner = new ArrayList<String>();
    private JMPlayerListener listener = null;
    
    public JMessage ( MConfiguration config ) {
        
        clearStandard = config.getBoolean("JMessage.clear-standard-messages", clearStandard);
        joinmsg = config.getStringList("JMessage.join-message", joinmsg);
        joinmsgpri = config.getStringList("JMessage.join-message-private", joinmsgpri);
        leavemsg = config.getStringList("JMessage.leave-message", leavemsg); 
        
        Set<String> keys = config.getKeys("JMessage.individual-messages");        
        if ( keys == null || keys.isEmpty() )
            return;
        for ( String pn : keys ) {
            indimsg.put(pn, config.getStringList("JMessage.individual-messages." + pn, null));            
        } 
        
    }        
    
    public void init ( JavaPlugin plug, MAuthorizer mauth ) {        
        (listener = new JMPlayerListener (plug, this, mauth)).init();                        
    }
    
    public void assignIndividual ( String pname, String txt ) {
        List<String> thelist = new ArrayList<String>();
        if ( indimsg.containsKey(pname) ){
            thelist = indimsg.get(pname);
            indimsg.remove(pname);
        }
        thelist.add(txt);
        MLog.d("[JM] Assigned '" + txt + "' to '" + pname + "'");
        indimsg.put(pname, thelist);
    }
    
    public void load ( MConfiguration config ) {
        indimsg.clear();
        config.load();
        clearStandard = config.getBoolean("JMessage.clear-standard-messages", clearStandard);
        joinmsg = config.getStringList("JMessage.join-message", joinmsg);
        joinmsgpri = config.getStringList("JMessage.join-message-private", joinmsgpri);
        leavemsg = config.getStringList("JMessage.leave-message", leavemsg); 
        
        Set<String> keys = config.getKeys("JMessage.individual-messages");        
        if ( keys == null || keys.isEmpty() )
            return;
        for ( String pn : keys ) {
            indimsg.put(pn, config.getStringList("JMessage.individual-messages." + pn, null));            
        } 
    }
    
    public void save ( MConfiguration config ) {
        
        config.set("JMessage.clear-standard-messages", clearStandard);
        config.set("JMessage.join-message", joinmsg);
        config.set("JMessage.join-message-private", joinmsgpri);        
        config.set("JMessage.leave-message", leavemsg);
        
        Set<String> keys = indimsg.keySet();
        for ( String pn : keys ) {
            config.set("JMessage.individual-messages." + pn, indimsg.get(pn));
        }
        
        config.save();
        
    }   
    
    public String untag ( String src, Player p ) {                                
        
        String res = src.replaceAll("%nm", p.getName())    // Normal name
                        .replaceAll("%dnm", p.getDisplayName())   // Display name
                        .replaceAll("%ol", getOnlinePlayerList (p))
                        .replaceAll("&uuml;", "ü")
                        .replaceAll("&ouml;", "ö")
                        .replaceAll("&aauml;", "ä")
                        .replaceAll("%snm", p.getServer().getServerName());    // Server name                
        
        return MLog.real(res);
        
    }
    
    public String getOnlinePlayerList ( Player p ) {
        
        String res = "";
        
        for ( Player tp : p.getServer().getOnlinePlayers() ) {            
            if ( !tp.equals(p) ) {
                res += tp.getName() + ", ";
            }                        
        }              

        if ( res.length() > 2 )
            res = res.substring(0, res.length() - 2);
    
        return res;             
        
    }
    
    public void doJoin ( Player p ) {                    
        
        if ( !joinmsgpri.isEmpty() ) {
            for ( String s : joinmsgpri )
                p.sendMessage( untag(s, p) );
        }  
        
        if ( !joinmsg.isEmpty() ) {
            for ( String s : joinmsg ) {                
                broadcast ( untag(s, p) );                
            }
        }                     
        
        MLog.d("Do individual: containsKey = " + indimsg.containsKey(p.getName()) );
        if ( indimsg.containsKey(p.getName()) ) {
            for ( String s : indimsg.get(p.getName()) ) {
                broadcast ( untag (s, p) );
            }
        }
    }
    
    public void doLeave ( Player p ) {
        if ( !leavemsg.isEmpty() )
            for ( String s : leavemsg )
                broadcast ( untag( s, p) );
    }
    
    public boolean isClearingStandard () {
        return clearStandard;
    }
    
    public void broadcast ( String txt ) {
        // This function needs to be overridden!
    }   
    
    public void addIngameLogger ( Player pl ) {
        pl.sendMessage(ChatColor.YELLOW + "You're now registered as IngameLogger.");
        ingamelogger.add(pl);
    }
    
    public void removeIngameLogger ( Player pl ) {
        pl.sendMessage(ChatColor.YELLOW + "You're no longer ingame logging.");
        ingamelogger.remove(pl);
    }
    
    public void addFakeOffliner (String name) {
        if (!fakeoffliner.contains(name))
            fakeoffliner.add(name);
    }    
    
    public void removeFakeOffliner (String name) {
        if (fakeoffliner.contains(name))
            fakeoffliner.add(name);
    }
    
    public List<Player> getIngameLogger () {
        return ingamelogger;
    }
    
}

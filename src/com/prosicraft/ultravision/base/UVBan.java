/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.commands.timeInterpreter;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class UVBan {       
    private String reason = "Not provided.";
    private String banner = null; // if player == banner --> unban
    private boolean global = false;    
    private Time timedif = null;
    private Time mTimeDif = null;    
    private String ServerName = "Not provided";
    
    public UVBan () {        
    }
    
    public UVBan (String reason, Player banner, boolean global, Time timedif) {
        this.reason = reason;
        this.banner = banner.getName();
        this.global = global;
        this.timedif = timedif;        
        this.mTimeDif = new Time(Calendar.getInstance().getTimeInMillis());
        this.ServerName = ((banner != null) ? banner.getServer().getServerName() : ServerName);
    }       
    
    public boolean isTempBan () {
        return timedif != null;
    }
    
    public void setTempBan (Time dif) {
        timedif = dif;
    }
    
    public Time getTimeRemain () {
        if ( timedif == null )
            return null;
        return new Time (timedif.getTime() -
                (Calendar.getInstance().getTimeInMillis() - this.mTimeDif.getTime()));
    }
    
    public String getFormattedTimeRemain () {
        Time td = getTimeRemain();
        return ((td != null && td.getTime() != 0) ? timeInterpreter.getText(td.getTime()) : "" );
    }
    
    public boolean isGlobal () {
        return global;
    }                
    
    public String getBanner () {
        return banner;
    }
    
    public String getReason () {
        return reason;
    }               

    public String getServerName() {
        return ServerName;
    }

    
    public void setReason (String reason) {
        this.reason = reason;
    }
    
    public String getFormattedInfo () {            
            return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + mTimeDif.toString() + ChatColor.DARK_GRAY + "] " +
                    ((global) ? "globally " : "") + "banned by " + ChatColor.AQUA + banner + ChatColor.DARK_GRAY + ((timedif != null) ? ChatColor.AQUA + " for " + timeInterpreter.getText(timedif.getTime()) : "" ) +
                            ChatColor.DARK_AQUA + ". Reason: " + ChatColor.GOLD + reason;
    }
    
    public boolean read ( DataInputStream in, UVFileInformation fi ) throws IOException {                
        
        this.banner = MStream.readString(in, 16).trim();        
        if ( this.banner.trim().equalsIgnoreCase("") )
            return false;
        this.reason = MStream.readString(in, 60).trim();
        this.global = MStream.readBool(in);
        if ( fi.getVersion() >= 1 ) {
            this.timedif = new Time ( in.readLong() );
            this.mTimeDif = new Time ( in.readLong() );
        }                             
        this.ServerName = MStream.readString(in, 16).trim();
        
        return true;
    }
    
    public void write ( DataOutputStream out ) throws IOException {        
        out.write(MAuthorizer.getCharArrayB(banner, 16));
        out.write(MAuthorizer.getCharArrayB(reason, 60));                
        out.write( global ? 1 : 0 );        
        out.writeLong( ((timedif == null) ? 0 : timedif.getTime()) );
        out.writeLong( ((mTimeDif == null) ? 0 : mTimeDif.getTime()) );        
        out.write(MAuthorizer.getCharArrayB(ServerName, 16));

        out.flush();       
    }
    
    public static void writeNull ( DataOutputStream out ) throws IOException {
        
        out.write(MAuthorizer.getCharArrayB("", 16));        
        out.flush();
        
    }
    
}

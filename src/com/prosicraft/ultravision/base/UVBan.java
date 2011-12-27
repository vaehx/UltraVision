/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Time;
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
        this.mTimeDif = timedif;
        this.ServerName = ((banner != null) ? banner.getServer().getServerName() : ServerName);
    }       
    
    public boolean isTempBan () {
        return timedif != null;
    }
    
    public void setTempBan (Time dif) {
        timedif = dif;
    }
    
    public Time getTimeRemain () {
        return timedif;
    }
    
    public String getFormattedTimeRemain () {
        return ((timedif != null && timedif.getTime() != 0) ? timedif.toString() : "" );
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
        return ((global) ? "globally " : "") + "banned by " + banner + ((mTimeDif != null) ? " for " + mTimeDif.toString() : "" ) + ". Reason: " + reason;
    }
    
    public boolean read ( DataInputStream in ) throws IOException {                
        
        this.banner = MStream.readString(in, 16).trim();        
        if ( this.banner.trim().equalsIgnoreCase("") )
            return false;
        this.reason = MStream.readString(in, 60).trim();
        this.global = MStream.readBool(in);
        this.timedif = new Time ( (long)in.read() );
        this.mTimeDif = new Time ( (long)in.read() );
        this.ServerName = MStream.readString(in, 16).trim();
        
        return true;
    }
    
    public void write ( DataOutputStream out ) throws IOException {        
        out.write(MAuthorizer.getCharArrayB(banner, 16));
        out.write(MAuthorizer.getCharArrayB(reason, 60));                
        out.write( global ? 1 : 0 );        
        out.write( (int)((timedif == null) ? 0 : timedif.getTime()) );
        out.write( (int)((mTimeDif == null) ? 0 : mTimeDif.getTime()) );
        out.write(MAuthorizer.getCharArrayB(ServerName, 16));

        out.flush();       
    }
    
    public static void writeNull ( DataOutputStream out ) throws IOException {
        
        out.write(MAuthorizer.getCharArrayB("", 16));        
        out.flush();
        
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class UVKick {
    private String reason = "No reason provided";    
    private String kicker = null;
    private String servername = "Not provided";
    private Time timestamp = null;
    
    public UVKick ()  {
        
    }
            
    public UVKick (String reason, Player dest, Time time) {
        this.reason = reason;
        this.kicker = dest.getName();
        this.servername = dest.getServer().getName();        
        this.timestamp = time;
    }        
    
    public String getReason () {
        return reason;
    }
    
    public String getKicker () {
        return kicker;
    }
    
    public String getServername () {
        return servername;
    }       
    
    public boolean read ( FileInputStream in ) throws IOException {
        
        if ( (this.kicker = MStream.readString(in, 16)).trim().equalsIgnoreCase("") )
            return false;
        this.reason = MStream.readString(in, 60);
        this.servername = MStream.readString(in, 16);
        this.timestamp = new Time ( (long)in.read() );        
        
        return true;
        
    }
    
    public void write ( PrintWriter out ) {
        
        out.write(MAuthorizer.getCharArray(kicker, 16));
        out.write(MAuthorizer.getCharArray(reason, 60));
        out.write(MAuthorizer.getCharArray(servername, 16));
        out.write( (int)timestamp.getTime() );
        
        out.flush();
        
    }
    
    public static void writeNull ( PrintWriter out ) {
        
        out.write(MAuthorizer.getCharArray("", 16));
        out.flush();
        
    }
    
}
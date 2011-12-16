/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.util.MAuthorizer;
import java.io.PrintWriter;
import java.sql.Time;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class UVWarning {
    private String reason = "Not provided";
    private Player warner = null;
    private Time warnTime = null;   // Time decremented by thread
    private Time mWarnTime = null;
    private String ServerName = "Not provided";
    private boolean global = false;
    
    public UVWarning (String reason, Player warner, boolean global, Time warnTime) {
        this.reason = reason;
        this.warner = warner;
        this.warnTime = warnTime;
        this.mWarnTime = warnTime;
        this.ServerName = ((warner != null) ? warner.getServer().getName() : ServerName);
        this.global = global;
    }
    
    public boolean isGlobal () {
        return this.global;
    }
    
    public Time getRemainingWarnTime () {
        return warnTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public Player getWarner () {
        return warner;
    }
    
    public String getFormattedInfo () {
        return ((global) ? "globally " : "") + "warned by " + warner.getName() + ((mWarnTime != null) ? " for " + mWarnTime.toString() : "") + ". Reason: " + reason;                  
    }
    
    public void write ( PrintWriter out ) {
        
        out.write(MAuthorizer.getCharArray(warner.getName(), 16));
        out.write(MAuthorizer.getCharArray(reason, 60));        
        out.write( (int) warnTime.getTime() );
        out.write( (int) mWarnTime.getTime() );
        out.write(MAuthorizer.getCharArray(ServerName, 16));
        out.write( global ? 1 : 0 );
        
        out.flush();
        
    }
    
    public static void writeNull ( PrintWriter out ) {
        
        out.write(MAuthorizer.getCharArray("", 16));        
        out.flush();
        
    }
}

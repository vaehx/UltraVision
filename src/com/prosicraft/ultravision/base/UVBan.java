/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import java.sql.Time;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class UVBan {
    private String reason = "Not provided.";
    private Player banner = null;
    private boolean global = false;
    private Time timedif = null;
    private Time mTimeDif = null;
    private String ServerName = "Not provided";
    
    public UVBan (String reason, Player banner, boolean global, Time timedif) {
        this.reason = reason;
        this.banner = banner;
        this.global = global;
        this.timedif = timedif;
        this.mTimeDif = timedif;
        this.ServerName = ((banner != null) ? banner.getServer().getName() : ServerName);
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
    
    public boolean isGlobal () {
        return global;
    }                
    
    public Player getBanner () {
        return banner;
    }
    
    public String getReason () {
        return reason;
    }       
    
    public void setReason (String reason) {
        this.reason = reason;
    }
    
    public String getFormattedInfo () {
        return ((global) ? "globally " : "") + "banned by " + banner.getName() + ((mTimeDif != null) ? " for " + mTimeDif.toString() : "" ) + ". Reason: " + reason;
    }
    
}

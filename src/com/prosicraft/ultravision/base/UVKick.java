/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class UVKick {
    private String reason = "No reason provided";    
    private Player kicker = null;
    private String servername = "Not provided";
    
    public UVKick (String reason, Player dest) {
        this.reason = reason;
        this.kicker = dest;
        this.servername = dest.getServer().getName();
    }
    
    public String getReason () {
        return reason;
    }
    
    public Player getKicker () {
        return kicker;
    }
    
    public String getServername () {
        return servername;
    }       
    
}

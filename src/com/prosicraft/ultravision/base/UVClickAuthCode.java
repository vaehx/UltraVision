/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import org.bukkit.Location;

/**
 *
 * @author passi
 */
public class UVClickAuthCode {
    public String code;
    public Location firstLoc;
    public Location secondLoc;
    public short pointer;
    public boolean logging;
    public boolean loggedIn;
    public boolean gaveBlock;

    public UVClickAuthCode (String c) {
        code = c;
        loggedIn = false;
    }

    public void setFirstLoc (Location l) {
        firstLoc = l;
    }

    public void increase() {
        pointer++;
    }

    public boolean isCorrect (int i, String val) {
        if (code.substring(i, i+1).equalsIgnoreCase("-")) {
            pointer += 2;
            return code.substring(i, i+2).equalsIgnoreCase(val);
        } else {
            pointer++;
            return code.substring(i, i+1).equalsIgnoreCase(val);
        }
    }
}

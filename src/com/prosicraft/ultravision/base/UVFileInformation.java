/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

/**
 *
 * @author passi
 */
public class UVFileInformation {
    
    // STATIC -------------------------------
    
    public static int uVersion = 2;    
    
    
    // UNSTATIC -----------------------------
    
    private int version;        
    
    /**
     * Create new UVFileInformation instance
     * @param v File Data Version
     */    
    public UVFileInformation ( int v ) {
        version = 2;
    }
    
    public int getVersion () {
        return version;
    }
    
    public void setVersion (int v) {
        version = v;
    }            
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author passi
 */
public class UVPlayerInfo {
    public UVBan           ban         = null; // only one ban on local
    public List<UVBan>     banHistory  = new ArrayList<UVBan>();
    public UVWarning          warning  = null; // only one warning on local    
    public List<UVWarning> warnHistory = new ArrayList<UVWarning>();    
    public List<UVKick>    kickHistory = new ArrayList<UVKick>();
    public List<String>    friends     = new ArrayList<String>();    
    public Map<String, String> notes   = new HashMap<String, String>();
    public int             praise      = 0;
    public List<String>    praiser     = new ArrayList<String>();
    public boolean         isMute      = false;
    public Time            onlineTime  = new Time (0);
    public Time            lastLogin   = null;        
    public PrintWriter     logOut      = null;
    public boolean         offline     = true;
    
    public UVPlayerInfo () {        
    }        
}

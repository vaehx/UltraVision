/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 *
 * TODO: Expand flag management for multiple Managers
 *
 */
package com.prosicraft.ultravision;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class uvPlayer {     // TARGETABLE!!

    // ------------------- VARIABLES --------------------

    // FOR BOTH
    public Player base;
    public String name = "";
    private long joinTime, leaveTime;
    public boolean isOnline = false;

    // FOR TARGET
    private boolean isTarget = false;                               // player is watched, or not
    public boolean isManager = false;                               // player is watching or not
    private List<String> managerNames = new ArrayList<>();    // Database of names of managers, supervising THIS
    private List<uvManager> managers;  // Array of Manager Class Instances
    private boolean[][] flags = {{false, false}, {false, false}};   // look at ultravision.java for flag list, firt array is the managerID




    // ------------------- FUNCTIONS ----------------------


    // FOR BOTH

    public uvPlayer (Player base) {
		this.managers = new ArrayList<>();
        this.base = base;
        if (this.base != null )
            this.name = base.getName();
    }

    public void setBase (Player base) { this.base = base; if (this.base != null ) this.name = base.getName(); }
    public Player getBase () { return base; }
    public final uvPlayer get () { return this; }

    public final uvPlayer join () {
        isOnline = true;
        joinTime = System.currentTimeMillis() / 1000;
        return this;
    }

    public void leave () {
        isOnline = false;
        leaveTime = System.currentTimeMillis() / 1000;
    }

    public String getName () { return this.name; }
    public String getPath () { return "users." + this.name + "."; }

    public void sendMessage (String msg) { base.sendMessage(ChatColor.DARK_GRAY + "[UV] " + ChatColor.WHITE + msg); }

    public long getJoinTime() { return joinTime; }
    public long getLeaveTime() { return leaveTime; }
    public long getOnlineTime() { return leaveTime - joinTime; }

    // FOR TARGET
    public void setAsTarget (boolean state) { this.isTarget = state; }
    public boolean isTarget () { return this.isTarget; }

    public int appendManager (String managerName, uvManager managerInstance) {
        this.managers.add(managerInstance);
        this.managerNames.add(managerName);
        return this.managers.size() - 1;
    }

    public void removeManager (String managerName, uvManager managerInstance) {
        if (this.managerNames.contains(managerName)) {
            this.managerNames.remove(managerName);
        }
        if (this.managers.contains(managerInstance)) {
            this.managers.remove(managerInstance);
        }
    }

    public int getManagerID  (String managerName) {
        if (this.isTarget && this.managerNames.contains(managerName))
            return managerNames.indexOf(managerName);
        else return -1;
    }

    public uvManager getManager (String managerName) {
        int mID =  getManagerID(managerName);
        return ((mID != -1) ? managers.get(mID) : null);
    }

    public uvManager getManager (int managerID) {
        int mID =  managerID;
        if (mID > -1 && managers.size() > mID) {
            return managers.get(mID);
        } else return null;
    }

   public boolean setFlag (int flagID, String managerName) {
        if (this.isTarget && managerNames.contains(managerName)) {
            //System.out.println("Set flag ID " + flagID + " for " + getManagerID(managerName) + " to " + !flags[getManagerID(managerName)][flagID]);
            flags[getManagerID(managerName)][flagID] = !flags[getManagerID(managerName)][flagID];
            return flags[getManagerID(managerName)][flagID];
        } else return false;
    }

    public boolean getFlag (int flagID, String managerName) {
        if (this.isTarget && flags.length > flagID && getManagerID(managerName) >= 0) {
            return flags[getManagerID(managerName)][flagID];
        } else return false;
    }

    public int numTrueFlags (String managerName) {
        if (this.isTarget && managerNames.contains(managerName)) {
            int cnt = 0;
            int mID = getManagerID(managerName);
            for (int i=0;i<flags.length;i++)
                if (flags[mID][i]) cnt++;
            return cnt;
        } return -1;
    }

    // -- very questionable function...
    public int numFlags (String managerName) {
        if (this.isTarget && getManagerID(managerName) >= 0)
            return flags[getManagerID(managerName)].length;
        else return -1;
    }

    public int numManagers () {
        if (this.managerNames != null && this.managerNames.size() > 0)
            return managerNames.size();
        else return 0;
    }

}

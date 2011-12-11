/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.global;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class globalEngine implements UltraVisionAPI {           
    

    @Override
    public Map<String, String> getAll(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerAuthorizer(MAuthorizer authorizer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MAuthorizer getAuthorizer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doBan(CommandSender cs, Player p, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doTempBan(CommandSender cs, Player p, String reason, Time time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void pardon(CommandSender cs, Player p, String note) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getBans(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getBanHistory(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doKick(CommandSender cs, Player p, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getKickHistory(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWarn(CommandSender cs, Player p, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTempWarn(CommandSender cs, Player p, String reason, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unsetWarn(CommandSender cs, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWarned(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWarnReason(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getWarnHistory(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void praise(CommandSender cs, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPraiseCount(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addNote(CommandSender cs, Player p, String note) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delNote(CommandSender cs, Player p, int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getNotes(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMute(CommandSender cs, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMute(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void subTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Time getOnlineTime(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void log(String target, String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addLogger(String target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearLogger(String target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(String target, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(String target, String pluginfilter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(String target, String pluginfilter, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addFriend(Player p, Player p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delFriend(Player p, Player p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getFriends(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setProperty(Player p, String prop) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getProperties(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
}

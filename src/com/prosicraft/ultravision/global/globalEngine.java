/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.global;

import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UVKick;
import com.prosicraft.ultravision.base.UVWarning;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MResult;
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
    public MResult registerAuthorizer(MAuthorizer authorizer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MAuthorizer getAuthorizer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAuthInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult doBan(CommandSender cs, Player p, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult doBan(CommandSender cs, Player p, String reason, boolean global) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult doTempBan(CommandSender cs, Player p, String reason, Time time, boolean global) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult pardon(CommandSender cs, Player p, String note) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isBanned(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UVBan> getBans(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UVBan> getBanHistory(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult doKick(CommandSender cs, Player p, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UVKick> getKickHistory(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setWarn(CommandSender cs, Player p, String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setWarn(CommandSender cs, Player p, String reason, Time tdiff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setTempWarn(CommandSender cs, Player p, String reason, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult unsetWarn(CommandSender cs, Player p) {
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
    public UVWarning getWarning(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UVWarning> getWarnHistory(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult praise(CommandSender cs, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPraiseCount(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult addNote(CommandSender cs, Player p, String note) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult delNote(CommandSender cs, Player p, int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> getNotes(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setMute(CommandSender cs, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMute(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult addTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult subTime(Time time, Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Time getOnlineTime(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult log(Player p, String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult clearLog(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(Player p, Time timefrom, Time timeto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(Player p, String pluginfilter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getLog(Player p, String pluginfilter, Time timediff) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult addFriend(Player p, Player p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult delFriend(Player p, Player p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getFriends(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult setProperty(Player p, String prop) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getProperties(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MResult flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UVBan getBan(Player p, String servername) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playerJoin(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playerLeave(Player p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}

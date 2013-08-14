/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.global;

import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UVKick;
import com.prosicraft.ultravision.base.UVPlayerInfo;
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
public class globalEngine implements UltraVisionAPI
{

	@Override
	public Map<String, String> getAllPlayerInformation( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Player getPlayer( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public UVPlayerInfo getPlayerInfo( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult savePlayer( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public UVPlayerInfo readPlayer( String playerName, boolean forceNewFile )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void onPlayerJoin( Player p )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void onPlayerLogin( Player p )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void onPlayerLeave( Player p )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult setAuthorizer( MAuthorizer authorizer )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MAuthorizer getAuthorizer()
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isAuthorizerEnabled()
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult banPlayerLocally( CommandSender commandSender, String playerName, String reason )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult banPlayer( CommandSender commandSender, String playerName, String reason, boolean global )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult banPlayerTemporarily( CommandSender commandSender, String playerName, String reason, Time time, boolean global )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult pardonPlayer( CommandSender commandSender, String playerName, String note )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isPlayerBanned( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<UVBan> getPlayerBans( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public UVBan getPlayerBan( String playerName, String servername )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<UVBan> getPlayerBanHistory( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult kickPlayer( CommandSender commandSender, String playerName, String reason )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult kickPlayerHard( String playerName, String reason )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<UVKick> getPlayerKickHistory( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult warnPlayer( CommandSender commandSender, String playerName, String reason )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult warnPlayerTemporarily( CommandSender commandSender, String playerName, String reason, Time time )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult unwarnPlayer( CommandSender commandSender, String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isPlayerWarned( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getPlayerWarnReason( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public UVWarning getPlayerWarning( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<UVWarning> getPlayerWarnHistory( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult praisePlayer( CommandSender commandSender, String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult unpraisePlayer( CommandSender commandSender, String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isPlayerPraisedBy( String playerName, String otherPlayerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getPlayerPraiseCount( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult addPlayerNote( CommandSender commandSender, String playerName, String note )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult delPlayerNote( CommandSender commandSender, String playerName, int id )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Map<String, String> getPlayerNotes( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult mutePlayer( CommandSender commandSender, String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isPlayerMuted( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult setPlayerOnlineTime( Time time, String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult addPlayerOnlineTime( Time time, String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult subPlayerOnlineTime( Time time, String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Time getPlayerOnlineTime( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult addPlayerLogLine( String playerName, String message )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult clearPlayerLog( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getPlayerLog( String playerName, Time timeFrom, Time timeTo )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getPlayerLog( String playerName, String pluginFilter )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult requestFriendship( String performingPlayerName, String requestedPlayerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult acceptFriendship( String requestedPlayerName, String performingPlayerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult rejectFriendship( String requestedPlayerName, String performingPlayerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult delPlayerFriend( String performingPlayerName, String friendName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getPlayerFriends( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public MResult setPlayerProperty( String playerName, String property )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getPlayerProperties( String playerName )
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean shutdown()
	{
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}
}

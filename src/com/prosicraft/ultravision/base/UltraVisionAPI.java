/*
 * Base interface for UltraVision Engine
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MResult;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public interface UltraVisionAPI
{
	/**
	 * UltraVision API Version
	 */
	public String version = "v0.3";

	/**
	 * Directory for saving player data
	 */
	public String userDataDir = "//userdata//";

	/**
	 * Command-logfile for players
	 */
	public String userLogDir = "//userlog//";

	/****************************************************************************************/

	/**
	 * Save all users and clear lists
	 */
	public boolean shutdown();

	/**
	 * Collect all informations about a Player
	 * @param player The Player
	 * @return Map of informations
	 */
	public Map<String, String> getAllPlayerInformation( PlayerIdent uid );

	/**
	 * Get the UVPlayer Instance of a player
	 * @param playerName
	 * @return
	 */
	public Player getPlayer( PlayerIdent uid );

	/**
	 * Get Information about a player
	 * @param playerName
	 * @return
	 */
	public UVPlayerInfo getPlayerInfo( PlayerIdent uid );
	
	public String tryGetPlayerNameByUID(PlayerIdent uid);
	
	
	
	
	public class MatchUserResult
	{
		public String name;			
		public boolean isOnline;
		public PlayerIdent pIdent;
		
		public MatchUserResult(String name, boolean isOnline, PlayerIdent uid)
		{
			this.name = name;
			this.isOnline = isOnline;
			this.pIdent = uid;
		}
	}	
	public List<MatchUserResult> matchUser(String name, boolean needsToBeEqual);

	/****************************************************************************************/

	/**
	 * Save player files
	 * @param playerName
	 */
	public MResult savePlayer( PlayerIdent uid );

	/**
	 * Read player data from files
	 * @param playerName
	 */
	public UVPlayerInfo readPlayer( PlayerIdent uid, boolean forceNewFile );

	/****************************************************************************************/

	/**
	 * When player joins the server
	 * @param p
	 */
	public void onPlayerJoin( Player p );

	/**
	 * When player gave correct password
	 * @param p
	 */
	public void onPlayerLogin( Player p );

	/**
	 * When player leaves the game
	 * @param p
	 */
	public void onPlayerLeave( Player p );

	/****************************************************************************************/

	/**
	 * Register an authorizer instance
	 * @param authorizer
	 * @return
	 */
	public MResult setAuthorizer( MAuthorizer authorizer );

	/**
	 * Get the authorizer Instance
	 * @return
	 */
	public MAuthorizer getAuthorizer();

	/**
	 * Check if Authorizer is inited and used
	 * @return
	 */
	public boolean isAuthorizerEnabled();

	/****************************************************************************************/

	/**
	 * Do a local Ban (forever-temp-ban)
	 *
	 * @param commandSender The One who perfomed this command
	 * @param playerName the players name
	 * @param reason
	 * @return
	 */
	public MResult banPlayerLocally( CommandSender commandSender, PlayerIdent uid, String reason );

	/**
	 * Do a global Ban (forever-temp-ban)
	 *
	 * @param commandSender the One who perfomed the command
	 * @param playerName the players name
	 * @param reason
	 * @param global
	 * @return
	 */
	public MResult banPlayer( CommandSender commandSender, PlayerIdent uid, String reason, boolean global );

	/**
	 * Ban a player for a specified time by given Player name
	 * If time is not set, the Ban will be infinite
	 *
	 * @param commandSender the one who perfomed this command
	 * @param playerName the name of the player
	 * @param reason
	 * @param time
	 * @param global
	 * @return
	 */
	public MResult banPlayerTemporarily( CommandSender commandSender, PlayerIdent uid, String reason, Time time, boolean global );

	/**
	 * Pardon a banned player
	 *
	 * @param commandSender the one who perfomed the command
	 * @param pname
	 * @param note
	 * @return
	 */
	public MResult pardonPlayer( CommandSender commandSender, PlayerIdent uid, String note );

	/**
	 * Check if Player is banned or not
	 *
	 * @param playerName
	 * @return
	 */
	public boolean isPlayerBanned( PlayerIdent uid );

	/**
	 * Get a list of bans where specified player was was banned
	 * In local system this will always return only one result.
	 *
	 * @param p
	 * @return
	 */
	public List<UVBan> getPlayerBans( PlayerIdent uid );

	/**
	 * Get the current ban on given server
	 * In local system servername is not used.
	 *
	 * @param p
	 * @param servername
	 * @return
	 */
	public UVBan getPlayerBan( PlayerIdent uid, String servername );

	/**
	 * Get older Bans (tempbans)
	 *
	 * @param p
	 * @return
	 */
	public List<UVBan> getPlayerBanHistory( PlayerIdent uid );

	/****************************************************************************************/

	/**
	 * Kick a player
	 *
	 * @param commandSender the one who perfomed the command
	 * @param p
	 * @param reason
	 * @return
	 */
	public MResult kickPlayer( CommandSender commandSender, PlayerIdent uid, String reason );

	/**
	 * Do a Hard-Kick.
	 * This forces any player instance to leave the server altough the connection is lost.
	 *
	 * @param p
	 * @param reason
	 * @return
	 */
	public MResult kickPlayerHard( PlayerIdent uid, String reason );

	/**
	 * Get kick History of a player
	 * @param p
	 * @return
	 */
	public List<UVKick> getPlayerKickHistory( PlayerIdent uid );

	/****************************************************************************************/

	/**
	 * Warn a player forever
	 * @param cs
	 * @param p
	 * @param reason
	 * @return
	 */
	public MResult warnPlayer( CommandSender commandSender, PlayerIdent uid, String reason );

	/**
	 * Warn a player for given time
	 * @param cs
	 * @param playerName
	 * @param reason
	 * @param timediff
	 * @return
	 */
	public MResult warnPlayerTemporarily( CommandSender commandSender, PlayerIdent uid, String reason, Time time );

	/**
	 * Unwarn a player
	 * @param cs
	 * @param p
	 * @return
	 */
	public MResult unwarnPlayer( CommandSender commandSender, PlayerIdent uid );

	/**
	 * Check if a player is warned
	 * @param playerName
	 * @return
	 */
	public boolean isPlayerWarned( PlayerIdent uid );

	/**
	 * Get the reason for a warning
	 * @param playerName
	 * @return
	 */
	public String getPlayerWarnReason( PlayerIdent uid );

	/**
	 * Get the instance of a warning
	 * @param playerName
	 * @return
	 */
	public UVWarning getPlayerWarning( PlayerIdent uid );

	/**
	 * Get older warnings
	 * @param p
	 * @return
	 */
	public List<UVWarning> getPlayerWarnHistory( PlayerIdent uid );

	/****************************************************************************************/

	/**
	 * Praise a player
	 * @param cs
	 * @param p
	 * @return
	 */
	public MResult praisePlayer( CommandSender commandSender, PlayerIdent uid );  // one command sender can praise only once

	/**
	 * Unpraise a player
	 * @param commandSender
	 * @param playerName
	 * @return
	 */
	public MResult unpraisePlayer( CommandSender commandSender, PlayerIdent uid );

	/**
	 * check if a player is praised by another player
	 * @param s
	 * @param p
	 * @return
	 */
	public boolean isPlayerPraisedBy( PlayerIdent uid, PlayerIdent otherUid );

	/**
	 * The the total count of praises for a player
	 * @param p
	 * @return
	 */
	public int getPlayerPraiseCount( PlayerIdent uid );

	/****************************************************************************************/

	/**
	 * Add a note to a player
	 * @param cs
	 * @param p
	 * @param note
	 * @return
	 */
	public MResult addPlayerNote( CommandSender commandSender, PlayerIdent uid, String note );

	/**
	 * removes a note from a player with given id
	 * @param commandSender
	 * @param playerName
	 * @param id
	 * @return
	 */
	public MResult delPlayerNote( CommandSender commandSender, PlayerIdent uid, int id );

	/**
	 * get all players notes
	 * @param p
	 * @return
	 */
	public Map<String, String> getPlayerNotes( PlayerIdent uid );

	/**
	 * Mute a player
	 * @param cs
	 * @param p
	 * @return
	 */
	public MResult mutePlayer( CommandSender commandSender, PlayerIdent uid );

	/**
	 * Check if a player has been muted
	 * @param p
	 * @return
	 */
	public boolean isPlayerMuted( PlayerIdent uid );

	/****************************************************************************************/

	/**
	 * Set the online time of a player
	 * @param time
	 * @param p
	 * @return
	 */
	public MResult setPlayerOnlineTime( Time time, PlayerIdent uid );

	/**
	 * Add amount to time of player is online
	 * @param time
	 * @param playerName
	 * @return
	 */
	public MResult addPlayerOnlineTime( Time time, PlayerIdent uid );

	/**
	 * Subtract from time of player is online
	 * @param time
	 * @param p
	 * @return
	 */
	public MResult subPlayerOnlineTime( Time time, PlayerIdent uid );

	/**
	 * get the online time, a player is online
	 * @param p
	 * @return
	 */
	public Time getPlayerOnlineTime( PlayerIdent uid );

	/****************************************************************************************/

	/**
	 * Write something to a player log
	 * @param p
	 * @param message
	 * @return
	 */
	public MResult addPlayerLogLine( PlayerIdent uid, String message );

	/**
	 * Clear the log of a player
	 * @param p
	 * @return
	 */
	public MResult clearPlayerLog( PlayerIdent uid );

	/**
	 * get the log entries from a player in given time range
	 * @param p
	 * @param timefrom
	 * @param timeto
	 * @return
	 */
	public List<String> getPlayerLog( PlayerIdent uid, Time timeFrom, Time timeTo );

	/**
	 * Get the log entries of a player filtered by plugin
	 * @param p
	 * @param pluginfilter
	 * @return
	 */
	public List<String> getPlayerLog( PlayerIdent uid, String pluginFilter );

	/****************************************************************************************/

	/**
	 * Send friendship request to a player
	 * @param p
	 * @param p2
	 * @return
	 */
	public MResult requestFriendship( PlayerIdent performerUID, PlayerIdent requestedUID );

	/**
	 * Accept the friendship
	 * @param p
	 * @param p2
	 * @return
	 */
	public MResult acceptFriendship( PlayerIdent requestedUID, PlayerIdent performingUID );

	/**
	 * Reject a friendship request
	 * @param p
	 * @param p2
	 * @return
	 */
	public MResult rejectFriendship( PlayerIdent requestedUID, PlayerIdent performingUID );

	/**
	 * Remove a friend
	 * @param p
	 * @param p2
	 * @return
	 */
	public MResult delPlayerFriend( PlayerIdent performingUID, PlayerIdent friendUID );

	/**
	 * Get a list of all friends a player has
	 * @param p
	 * @return
	 */
	public List<String> getPlayerFriends( PlayerIdent uid );

	/**
	 * Set a custom property for a user
	 * @param p
	 * @param prop
	 * @return
	 */
	public MResult setPlayerProperty( PlayerIdent uid, String property );

	/**
	 * Get the list of all custom player properties
	 * @param playerName
	 * @return
	 */
	public List<String> getPlayerProperties( PlayerIdent uid );
}

/*
 * Base interface for UltraVision Engine
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MResult;
import java.io.IOException;
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
	public Map<String, String> getAllPlayerInformation( String playerName );

	/**
	 * Get the UVPlayer Instance of a player
	 * @param playerName
	 * @return
	 */
	public Player getPlayer( String playerName );

	/**
	 * Get Information about a player
	 * @param playerName
	 * @return
	 */
	public UVPlayerInfo getPlayerInfo( String playerName );

	/****************************************************************************************/

	/**
	 * Save player files
	 * @param playerName
	 */
	public MResult savePlayer( String playerName );

	/**
	 * Read player data from files
	 * @param playerName
	 */
	public UVPlayerInfo readPlayer( String playerName, boolean forceNewFile );

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
	public MResult banPlayerLocally( CommandSender commandSender, String playerName, String reason );

	/**
	 * Do a global Ban (forever-temp-ban)
	 *
	 * @param commandSender the One who perfomed the command
	 * @param playerName the players name
	 * @param reason
	 * @param global
	 * @return
	 */
	public MResult banPlayer( CommandSender commandSender, String playerName, String reason, boolean global );

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
	public MResult banPlayerTemporarily( CommandSender commandSender, String playerName, String reason, Time time, boolean global );

	/**
	 * Pardon a banned player
	 *
	 * @param commandSender the one who perfomed the command
	 * @param pname
	 * @param note
	 * @return
	 */
	public MResult pardonPlayer( CommandSender commandSender, String playerName, String note );

	/**
	 * Check if Player is banned or not
	 *
	 * @param playerName
	 * @return
	 */
	public boolean isPlayerBanned( String playerName );

	/**
	 * Get a list of bans where specified player was was banned
	 * In local system this will always return only one result.
	 *
	 * @param p
	 * @return
	 */
	public List<UVBan> getPlayerBans( String playerName );

	/**
	 * Get the current ban on given server
	 * In local system servername is not used.
	 *
	 * @param p
	 * @param servername
	 * @return
	 */
	public UVBan getPlayerBan( String playerName, String servername );

	/**
	 * Get older Bans (tempbans)
	 *
	 * @param p
	 * @return
	 */
	public List<UVBan> getPlayerBanHistory( String playerName );

	/****************************************************************************************/

	/**
	 * Kick a player
	 *
	 * @param commandSender the one who perfomed the command
	 * @param p
	 * @param reason
	 * @return
	 */
	public MResult kickPlayer( CommandSender commandSender, String playerName, String reason );

	/**
	 * Do a Hard-Kick.
	 * This forces any player instance to leave the server altough the connection is lost.
	 *
	 * @param p
	 * @param reason
	 * @return
	 */
	public MResult kickPlayerHard( String playerName, String reason );

	/**
	 * Get kick History of a player
	 * @param p
	 * @return
	 */
	public List<UVKick> getPlayerKickHistory( String playerName );

	/****************************************************************************************/

	/**
	 * Warn a player forever
	 * @param cs
	 * @param p
	 * @param reason
	 * @return
	 */
	public MResult warnPlayer( CommandSender commandSender, String playerName, String reason );

	/**
	 * Warn a player for given time
	 * @param cs
	 * @param playerName
	 * @param reason
	 * @param timediff
	 * @return
	 */
	public MResult warnPlayerTemporarily( CommandSender commandSender, String playerName, String reason, Time time );

	/**
	 * Unwarn a player
	 * @param cs
	 * @param p
	 * @return
	 */
	public MResult unwarnPlayer( CommandSender commandSender, String playerName );

	/**
	 * Check if a player is warned
	 * @param playerName
	 * @return
	 */
	public boolean isPlayerWarned( String playerName );

	/**
	 * Get the reason for a warning
	 * @param playerName
	 * @return
	 */
	public String getPlayerWarnReason( String playerName );

	/**
	 * Get the instance of a warning
	 * @param playerName
	 * @return
	 */
	public UVWarning getPlayerWarning( String playerName );

	/**
	 * Get older warnings
	 * @param p
	 * @return
	 */
	public List<UVWarning> getPlayerWarnHistory( String playerName );

	/****************************************************************************************/

	/**
	 * Praise a player
	 * @param cs
	 * @param p
	 * @return
	 */
	public MResult praisePlayer( CommandSender commandSender, String playerName );  // one command sender can praise only once

	/**
	 * Unpraise a player
	 * @param commandSender
	 * @param playerName
	 * @return
	 */
	public MResult unpraisePlayer( CommandSender commandSender, String playerName );

	/**
	 * check if a player is praised by another player
	 * @param s
	 * @param p
	 * @return
	 */
	public boolean isPlayerPraisedBy( String playerName, String otherPlayerName );

	/**
	 * The the total count of praises for a player
	 * @param p
	 * @return
	 */
	public int getPlayerPraiseCount( String playerName );

	/****************************************************************************************/

	/**
	 * Add a note to a player
	 * @param cs
	 * @param p
	 * @param note
	 * @return
	 */
	public MResult addPlayerNote( CommandSender commandSender, String playerName, String note );

	/**
	 * removes a note from a player with given id
	 * @param commandSender
	 * @param playerName
	 * @param id
	 * @return
	 */
	public MResult delPlayerNote( CommandSender commandSender, String playerName, int id );

	/**
	 * get all players notes
	 * @param p
	 * @return
	 */
	public Map<String, String> getPlayerNotes( String playerName );

	/**
	 * Mute a player
	 * @param cs
	 * @param p
	 * @return
	 */
	public MResult mutePlayer( CommandSender commandSender, String playerName );

	/**
	 * Check if a player has been muted
	 * @param p
	 * @return
	 */
	public boolean isPlayerMuted( String playerName );

	/****************************************************************************************/

	/**
	 * Set the online time of a player
	 * @param time
	 * @param p
	 * @return
	 */
	public MResult setPlayerOnlineTime( Time time, String playerName );

	/**
	 * Add amount to time of player is online
	 * @param time
	 * @param playerName
	 * @return
	 */
	public MResult addPlayerOnlineTime( Time time, String playerName );

	/**
	 * Subtract from time of player is online
	 * @param time
	 * @param p
	 * @return
	 */
	public MResult subPlayerOnlineTime( Time time, String playerName );

	/**
	 * get the online time, a player is online
	 * @param p
	 * @return
	 */
	public Time getPlayerOnlineTime( String playerName );

	/****************************************************************************************/

	/**
	 * Write something to a player log
	 * @param p
	 * @param message
	 * @return
	 */
	public MResult addPlayerLogLine( String playerName, String message );

	/**
	 * Clear the log of a player
	 * @param p
	 * @return
	 */
	public MResult clearPlayerLog( String playerName );

	/**
	 * get the log entries from a player in given time range
	 * @param p
	 * @param timefrom
	 * @param timeto
	 * @return
	 */
	public List<String> getPlayerLog( String playerName, Time timeFrom, Time timeTo );

	/**
	 * Get the log entries of a player filtered by plugin
	 * @param p
	 * @param pluginfilter
	 * @return
	 */
	public List<String> getPlayerLog( String playerName, String pluginFilter );

	/****************************************************************************************/

	/**
	 * Send friendship request to a player
	 * @param p
	 * @param p2
	 * @return
	 */
	public MResult requestFriendship( String performingPlayerName, String requestedPlayerName );

	/**
	 * Accept the friendship
	 * @param p
	 * @param p2
	 * @return
	 */
	public MResult acceptFriendship( String requestedPlayerName, String performingPlayerName );

	/**
	 * Reject a friendship request
	 * @param p
	 * @param p2
	 * @return
	 */
	public MResult rejectFriendship( String requestedPlayerName, String performingPlayerName );

	/**
	 * Remove a friend
	 * @param p
	 * @param p2
	 * @return
	 */
	public MResult delPlayerFriend( String performingPlayerName, String friendName );

	/**
	 * Get a list of all friends a player has
	 * @param p
	 * @return
	 */
	public List<String> getPlayerFriends( String playerName );

	/**
	 * Set a custom property for a user
	 * @param p
	 * @param prop
	 * @return
	 */
	public MResult setPlayerProperty( String playerName, String property );

	/**
	 * Get the list of all custom player properties
	 * @param playerName
	 * @return
	 */
	public List<String> getPlayerProperties( String playerName );
}

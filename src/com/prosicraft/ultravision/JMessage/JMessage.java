/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.JMessage;

import com.prosicraft.ultravision.base.PlayerIdent;
import com.prosicraft.ultravision.base.UVClickAuth;
import com.prosicraft.ultravision.base.UVPlayerInfo;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import com.prosicraft.ultravision.util.MLog;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class JMessage
{

	private List<String> joinmsg = new ArrayList<>();
	private List<String> joinmsgpri = new ArrayList<>();
	private List<String> leavemsg = new ArrayList<>();
	private List<String> spawnmsg = new ArrayList<>();
	private List<String> players = new ArrayList<>();
	private Map<String, List<String>> indimsg = new HashMap<>();
	private boolean clearStandard = true;
	private boolean useUltraChat = false;
	private List<Player> ingamelogger = new ArrayList<>();
	private List<String> fakeoffliner = new ArrayList<>();
	private JMPlayerListener listener = null;
	private UltraVisionAPI uv = null;

	public JMessage( MConfiguration config, UltraVisionAPI ultravision )
	{

		clearStandard = config.getBoolean( "JMessage.clear-standard-messages", clearStandard );
		joinmsg = config.getStringList( "JMessage.join-message", joinmsg );
		joinmsgpri = config.getStringList( "JMessage.join-message-private", joinmsgpri );
		leavemsg = config.getStringList( "JMessage.leave-message", leavemsg );
		spawnmsg = config.getStringList( "JMessage.spawn-message", spawnmsg );
		players = config.getStringList( "JMessage.players", players );
		useUltraChat = config.getBoolean( "JMessage.use-ultrachat", useUltraChat );

		Set<String> keys = config.getKeys( "JMessage.individual-messages" );
		if( keys == null || keys.isEmpty() )
			return;
		for( String pn : keys )
		{
			indimsg.put( pn, config.getStringList( "JMessage.individual-messages." + pn, null ) );
		}

		uv = ultravision;

	}

	public void init( JavaPlugin plug, MAuthorizer mauth, UVClickAuth cauth )
	{
		( listener = new JMPlayerListener( plug, this, mauth, cauth ) ).init();
	}

	public void setAPI( UltraVisionAPI api )
	{
		uv = api;
	}

	public void assignIndividual( String pname, String txt )
	{
		List<String> thelist = new ArrayList<>();
		if( indimsg.containsKey( pname ) )
		{
			thelist = indimsg.get( pname );
			indimsg.remove( pname );
		}
		thelist.add( txt );
		MLog.d( "[JM] Assigned '" + txt + "' to '" + pname + "'" );
		indimsg.put( pname, thelist );
	}

	public void load( MConfiguration config )
	{
		indimsg.clear();
		config.load();
		clearStandard = config.getBoolean( "JMessage.clear-standard-messages", clearStandard );
		useUltraChat = config.getBoolean( "JMessage.use-ultrachat", useUltraChat );
		joinmsg = config.getStringList( "JMessage.join-message", joinmsg );
		joinmsgpri = config.getStringList( "JMessage.join-message-private", joinmsgpri );
		leavemsg = config.getStringList( "JMessage.leave-message", leavemsg );
		spawnmsg = config.getStringList( "JMessage.spawn-message", spawnmsg );
		players = config.getStringList( "JMessage.players", players );

		Set<String> keys = config.getKeys( "JMessage.individual-messages" );
		if( keys == null || keys.isEmpty() )
			return;
		for( String pn : keys )
		{
			indimsg.put( pn, config.getStringList( "JMessage.individual-messages." + pn, null ) );
		}
	}

	public void save( MConfiguration config )
	{

		config.set( "JMessage.clear-standard-messages", clearStandard );
		config.set( "JMessage.use-ultrachat", useUltraChat );
		config.set( "JMessage.join-message", joinmsg );
		config.set( "JMessage.join-message-private", joinmsgpri );
		config.set( "JMessage.leave-message", leavemsg );
		config.set( "JMessage.spawn-message", spawnmsg );
		config.set( "JMessage.players", players );

		Set<String> keys = indimsg.keySet();
		for( String pn : keys )
		{
			config.set( "JMessage.individual-messages." + pn, indimsg.get( pn ) );
		}

		config.save();

	}

	public String perms_getPrefix( Player p )
	{
		if( !useUltraChat )
			return "";
		return "";
	}

	public String untag( String src, Player p )
	{
		String res = src.replaceAll( "%nm", p.getName() ) // Normal name
			.replaceAll( "%dnm", p.getDisplayName() ) // Display name
			.replaceAll( "%ol", getOnlinePlayerList( p, false ) ) // Online List (w/o Prefixes)
			.replaceAll( "%preol", getOnlinePlayerList( p, true ) ) // Online List (with Prefixes)
			.replaceAll( "%size", Integer.toString( p.getServer().getOnlinePlayers().size() ) ) // Current online users
			.replaceAll( "%max", Integer.toString( p.getServer().getMaxPlayers() ) ) // Max Slots
			.replaceAll( "%mode", p.getGameMode().name() ) // Gamemode
			.replaceAll( "%laston", getLastOnlineTime( p ) ) // Last Login time
			.replaceAll( "%world", p.getWorld().getName() ) // World
			.replaceAll( "%snm", p.getServer().getServerName() ) // Server name
			.replaceAll( "&uuml;", "ü" )
			.replaceAll( "&ouml;", "ö" )
			.replaceAll( "&aauml;", "ä" )
			.replaceAll( "&szlig", "ß" );

		if( listener != null )
			res = listener.untag2( res, p );

		return MLog.real( res );
	}

	public String getLastOnlineTime( Player p )
	{
		if( uv == null || p == null )
			return "";

		DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
		UVPlayerInfo pi = uv.getPlayerInfo( new PlayerIdent(p) );
		if( pi == null )
		{
			MLog.w( "Could not retrieve UVPlayerInfo for user '" + p.getName() + "'" );
			return "";
		}

		Time t = pi.lastOnline;
		Date date = new Date( t.getTime() );
		return dateFormat.format( date );
	}

	public String getOnlinePlayerList( Player p, boolean prefixed )
	{

		String res = "";
		String pre = "";

		if( prefixed )
			listener.checkVault();

		for( Player tp : p.getServer().getOnlinePlayers() )
		{
			if( !tp.equals( p ) )
			{
				if( listener.getChat() != null )
				{
					if( prefixed )
						pre = listener.getChat().getPlayerPrefix( tp );
				}
				res += pre + tp.getName() + ChatColor.GRAY + ", ";
			}
		}
		if( listener.getChat() != null )
		{
			if( prefixed )
				pre = listener.getChat().getPlayerPrefix( p );
		}
		res += pre + p.getName();

		return res;

	}

	public void doJoin( Player p )
	{

		// Join to UltraVision if not happened already
		if( uv != null )
		{
			Player testPlayer = uv.getPlayer(new PlayerIdent(p));
			if (testPlayer == null)
			{
				// try to load it now.
				uv.onPlayerJoin( p );
			}
		}

		if( !joinmsgpri.isEmpty() )
		{
			for( String s : joinmsgpri )
				p.sendMessage( untag( s, p ) );
		}

		if( !spawnmsg.isEmpty() && !players.contains( p.getName() ) )
		{
			for( String s : spawnmsg )
				p.sendMessage( untag( s, p ) );
			players.add( p.getName() );
		}

		if( !joinmsg.isEmpty() )
		{
			for( String s : joinmsg )
			{
				broadcast( untag( s, p ) );
			}
		}

		MLog.d( "Do individual: containsKey = " + indimsg.containsKey( p.getName() ) );
		if( indimsg.containsKey( p.getName() ) )
		{
			for( String s : indimsg.get( p.getName() ) )
			{
				broadcast( untag( s, p ) );
			}
		}
	}

	public void doLeave( Player p )
	{
		if( !leavemsg.isEmpty() )
			for( String s : leavemsg )
				broadcast( untag( s, p ) );
	}

	public void doJoinTest( Player p )
	{
		if( !joinmsgpri.isEmpty() )
			for( String s : joinmsgpri )
				p.sendMessage( untag( s, p ) );
		if( !spawnmsg.isEmpty() )
			for( String s : spawnmsg )
				p.sendMessage( untag( s, p ) );
		if( !joinmsg.isEmpty() )
			for( String s : joinmsg )
				p.sendMessage( untag( s, p ) );
		if( indimsg.containsKey( p.getName() ) )
			for( String s : indimsg.get( p.getName() ) )
				p.sendMessage( untag( s, p ) );
	}

	public void doLeaveTest( Player p )
	{
		if( !leavemsg.isEmpty() )
			for( String s : leavemsg )
				p.sendMessage( untag( s, p ) );
	}

	public boolean isClearingStandard()
	{
		return clearStandard;
	}

	public void broadcast( String txt )
	{
		// This function needs to be overridden!
	}

	public void addIngameLogger( Player pl )
	{
		pl.sendMessage( ChatColor.YELLOW + "You're now registered as IngameLogger." );
		ingamelogger.add( pl );
	}

	public void removeIngameLogger( Player pl )
	{
		pl.sendMessage( ChatColor.YELLOW + "You're no longer ingame logging." );
		ingamelogger.remove( pl );
	}

	public void addFakeOffliner( String name )
	{
		if( !fakeoffliner.contains( name ) )
			fakeoffliner.add( name );
	}

	public void removeFakeOffliner( String name )
	{
		if( fakeoffliner.contains( name ) )
			fakeoffliner.add( name );
	}

	public List<Player> getIngameLogger()
	{
		return ingamelogger;
	}
}

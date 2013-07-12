/*              THIS FILE IS PART OF ULTRAVISION                              */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UVPlayerInfo;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

/**
 * ----- T H E P L A Y E R L I S T E N E R -----
 */
public class uvPlayerListener implements Listener
{

	private ultravision parent = null;
	private MAuthorizer auth = null;
	private UltraVisionAPI uv = null;

	public uvPlayerListener( ultravision parent )
	{
		this.parent = parent;
		auth = parent.getAuthorizer();
	}

	public void initUV( UltraVisionAPI uva )
	{
		uv = uva;
	}

	public boolean loggedIn( Player p )
	{
		boolean res = true;
		if( auth != null )
			res = auth.loggedIn( p );
		if( parent.getClickAuth() != null )
		{
			if( res && !parent.getClickAuth().isLoggedIn( p.getName() ) )
				res = false;
		}
		return res;
	}

	public boolean registered( Player p )
	{
		boolean res = false;
		if( auth != null )
			res = auth.isRegistered( p );
		else
			MLog.d( "auth is null" );
		if( parent.getClickAuth() != null )
		{
			if( !res && parent.getClickAuth().isRegistered( p.getName() ) )
				res = true;
		}
		else
			MLog.d( "clickauth is null" );
		return res;
	}

	@EventHandler( priority = EventPriority.LOW )
	public void onPlayerLogin( PlayerLoginEvent e )
	{
		if( e.getPlayer() instanceof Player )
		{
			// Check Mac Address With CrashHack
                        /*if (parent.getCrashHack() != null)
			 {
			 String msg = parent.getCrashHack().join(e.getPlayer());
			 if (!msg.equalsIgnoreCase("valid"))
			 {
			 e.setKickMessage(MLog.real(ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + msg));
			 }
			 }*/

			// Check if player is already online
			for( Player p : parent.getServer().getOnlinePlayers() )
			{
				if( p.getName().equalsIgnoreCase( e.getPlayer().getName() ) )
				{
					MLog.i( "Player " + p.getName() + " got hacked. Kick." );
					e.setKickMessage( MLog.real( ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "You're hacking a user!" ) );
					e.setResult( PlayerLoginEvent.Result.KICK_OTHER );
					uv.playerLeave( e.getPlayer() );
					return;
				}
			}

			// Check if Username is valid
			if( e.getPlayer().getName().equalsIgnoreCase( "" ) || ( !e.getPlayer().getName().matches( "[A-Za-z0-9_]*" ) ) )
			{
				e.setKickMessage( MLog.real( ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "Invalid username. (No special characters allowed!)" ) );
				e.setResult( PlayerLoginEvent.Result.KICK_OTHER );
				uv.playerLeave( e.getPlayer() );
			}

			// Check if player is banned
			if( !parent.playerJoin( e.getPlayer() ) )
			{
				UVBan theBan = uv.getBan( e.getPlayer(), parent.getServer().getServerName() );

				if( theBan == null )
				{
					MLog.e( "Player '" + e.getPlayer().getName() + "' failed ban check: theBan = null." );
					return;
				}

				e.setKickMessage( MLog.real( ChatColor.DARK_GRAY + "[UltraVision] " + ChatColor.AQUA + "You're banned. Reason: " + theBan.getReason() + " (" + ( ( theBan.isGlobal() ) ? "global, " : "local, " ) + ( ( !theBan.isTempBan() ) ? "perma" : theBan.getFormattedTimeRemain() + " left" ) + ")" ) );
				e.setResult( PlayerLoginEvent.Result.KICK_OTHER );
				uv.playerLeave( e.getPlayer() );
			}

		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerJoin( PlayerJoinEvent e )
	{
		if( e.getPlayer() instanceof Player )
		{
			if( parent.showWelcomeMessage )
				e.getPlayer().sendMessage( ChatColor.DARK_GRAY + " ==== " + ChatColor.GRAY + "This Server is" + ChatColor.DARK_AQUA + " powered by " + ChatColor.AQUA + "UltraVision" + ChatColor.DARK_GRAY + " ====" );

			uv.playerJoin( e.getPlayer() );

			if( !registered( e.getPlayer() ) )
			{
				e.getPlayer().sendMessage( ChatColor.YELLOW + "Warning: You're not registered in the login system yet!" );
			}
		}
	}

	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerJoinAfter( PlayerJoinEvent e )
	{
		if( e.getPlayer() instanceof Player )
		{
			UVPlayerInfo ui;
			if( ( ui = uv.getPlayerInfo( e.getPlayer().getName() ) ) != null )
			{
				for( String fr : ui.friendRequests )
				{
					e.getPlayer().sendMessage( ChatColor.AQUA + fr + ChatColor.DARK_AQUA + " wants to be a friend of you." );
					e.getPlayer().sendMessage( ChatColor.DARK_AQUA + "Please answer with " + ChatColor.GOLD + "/accfriend " + fr + " yes" + ChatColor.DARK_AQUA + " or " + ChatColor.GOLD + "no" );
				}
			}
			else
			{
				MLog.e( "Can't lookup playerinfo for '" + e.getPlayer().getName() + "'" );
			}
		}
	}

	@EventHandler( priority = EventPriority.NORMAL )
	public void onPlayerQuit( PlayerQuitEvent e )
	{
		parent.playerLeave( e.getPlayer() );
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerCommandPreprocess( PlayerCommandPreprocessEvent event )
	{
		final boolean loggedIn = loggedIn( event.getPlayer() );
		if( ( !event.getMessage().contains( "/login" ) ) && !loggedIn )
		{
			event.getPlayer().sendMessage( ChatColor.RED + "You're not logged in." );
			event.setCancelled( true );
		}
		if( uv != null && parent.useCommandLog() )
		{
			uv.log( event.getPlayer(), event.getMessage() );
		}
	}

	@EventHandler( priority = EventPriority.LOW )
	public void onPlayerDamage( EntityDamageEvent event )
	{
		if( event.getEntity() instanceof Player && parent.IsUsingAuthorizer() )
		{
			if( !loggedIn( ( Player ) event.getEntity() ) || !uv.getAuthorizer().isRegistered( ( Player ) event.getEntity() ) )
			{
				event.setCancelled( true );
			}
		}
	}
}

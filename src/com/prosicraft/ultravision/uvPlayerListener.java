/*              THIS FILE IS PART OF ULTRAVISION                              */
package com.prosicraft.ultravision;

import com.prosicraft.ultravision.base.PlayerIdent;
import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UVPlayerInfo;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
		this.uv = parent.getAPI();
		auth = parent.getAuthorizer();
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

		if( parent.getClickAuth() != null )
		{
			if( !res && parent.getClickAuth().isRegistered( p.getName() ) )
				res = true;
		}

		return res;
	}

	@EventHandler( priority = EventPriority.LOW )
	public void onPlayerLogin( PlayerLoginEvent e )
	{
		if( e.getPlayer() instanceof Player )
		{
			uv.onPlayerJoin( e.getPlayer() );

			// Check if player is already online
			if (!e.getPlayer().getServer().getOnlineMode())
			{
				for( Player p : parent.getServer().getOnlinePlayers() )
				{
					// Test UUID and name (prevent multiple users with same nickname)
					if( e.getPlayer().getUniqueId() == p.getUniqueId()
						|| e.getPlayer().getName().equalsIgnoreCase(p.getName()) )
					{
						MLog.i( "Player " + p.getName() + " got hacked. Kick." );
						e.setKickMessage( MLog.real( ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "You're hacking a user!" ) );
						e.setResult( PlayerLoginEvent.Result.KICK_OTHER );
						uv.onPlayerLeave( e.getPlayer() );
						return;
					}
				}
			}

			// Check if Username is valid
			if( e.getPlayer().getName().equalsIgnoreCase( "" ) || ( !e.getPlayer().getName().matches( "[A-Za-z0-9_]*" ) ) )
			{
				e.setKickMessage( MLog.real( ChatColor.DARK_GRAY + "[UltraVision " + ChatColor.DARK_AQUA + "Kick" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + "Invalid username. (No special characters allowed!)" ) );
				e.setResult( PlayerLoginEvent.Result.KICK_OTHER );
				uv.onPlayerLeave( e.getPlayer() );
			}

			// Check if player is banned
			if( !parent.playerJoin( e.getPlayer() ) )
			{
				UVBan theBan = uv.getPlayerBan( new PlayerIdent(e.getPlayer()), parent.getServer().getServerName() );

				if( theBan == null )
				{
					MLog.e( "Player '" + e.getPlayer().getName() + "' failed ban check: theBan = null." );
					return;
				}

				e.setKickMessage( MLog.real( ChatColor.DARK_GRAY + "[UltraVision] " + ChatColor.AQUA + "You're banned. Reason: " + theBan.getReason() + " (" + ( ( theBan.isGlobal() ) ? "global, " : "local, " ) + ( ( !theBan.isTempBan() ) ? "perma" : theBan.getFormattedTimeRemain() + " left" ) + ")" ) );
				e.setResult( PlayerLoginEvent.Result.KICK_OTHER );
				uv.onPlayerLeave( e.getPlayer() );
			}

		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerJoin( PlayerJoinEvent e )
	{
		if( e.getPlayer() instanceof Player )
		{
			PlayerIdent pIdent = new PlayerIdent(e.getPlayer());
			parent.getAPI().addPlayerLogLine( pIdent, "** Joined successfully (ip " + e.getPlayer().getAddress().toString() + ")" );
			if( parent.getAPI().isPlayerWarned( pIdent ) )
			{
				parent.getAPI().addPlayerLogLine( pIdent, "* player is warned." );
			}

			if( parent.showWelcomeMessage )
				e.getPlayer().sendMessage( ChatColor.DARK_GRAY + " ==== " + ChatColor.GRAY + "This Server is" + ChatColor.DARK_AQUA + " powered by " + ChatColor.AQUA + "UltraVision" + ChatColor.DARK_GRAY + " ====" );

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
			if( ( ui = uv.getPlayerInfo( new PlayerIdent(e.getPlayer()) ) ) != null )
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
		if( parent.disableIngameOp && event.getMessage().contains( "/op" ) )
		{
			event.getPlayer().sendMessage( ChatColor.RED + "Oops. Ingame opping is disabled on this server!" );
			event.setCancelled( true );
		}

		if (registered(event.getPlayer()))
		{
			final boolean loggedIn = loggedIn( event.getPlayer() );
			if (!loggedIn)
			{
				if( !event.getMessage().contains( "/login" ) )
				{
					event.getPlayer().sendMessage( ChatColor.RED + "You're not logged in." );
					event.setCancelled( true );
				}
				else
				{
					String message = event.getMessage();
					String[] params = message.split( "\\s+" );
					if( params.length == 1 )
					{
						event.getPlayer().sendMessage( ChatColor.RED + "You didn't specify any password!" );
						event.setCancelled( true );
					}
					else
					{
						if( parent.doLoginCommand( event.getPlayer(), params[1] ) )
						{
							// Prevent Password to be written into console / serverlog
							MLog.i( "Player " + event.getPlayer().getName() + " logged in successfully." );
							event.setCancelled( true );
						}
					}
				}
			}
			else if (event.getMessage().contains("/login"))
			{
				event.getPlayer().sendMessage(ChatColor.YELLOW + "You're already logged in.");
				event.setCancelled(true);
			}
		}
		else
		{
			if (event.getMessage().contains("/login"))
			{
				event.getPlayer().sendMessage(ChatColor.RED + "You are not registered, so you cannot log in!");

				// set cancelled, so that we don't process this command further
				event.setCancelled(true);
			}
		}

		if( uv != null && parent.useCommandLog() )
		{
			uv.addPlayerLogLine( new PlayerIdent(event.getPlayer()), event.getMessage() );
		}
	}

	@EventHandler( priority = EventPriority.LOW )
	public void onPlayerDamage( EntityDamageEvent event )
	{
		if( event.getEntity() instanceof Player && parent.IsUsingAuthorizer() )
		{
			if( !loggedIn( ( Player ) event.getEntity() ) && uv.getAuthorizer().isRegistered( ( Player ) event.getEntity() ) )
			{
				event.setCancelled( true );
			}
		}
	}
}

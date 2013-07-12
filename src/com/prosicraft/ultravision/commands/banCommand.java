/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class banCommand extends extendedCommand
{

	public banCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /ban <player> [reason]   --> localban
			if( this.numArgs() >= 1 )
			{

				this.ev( p );

				List<Player> mayKick = this.getParent().getServer().matchPlayer( this.getArg( 0 ) );

				if( mayKick == null || mayKick.isEmpty() )
					return err( p, ChatColor.RED + "There's no player called '" + this.getArg( 0 ) + "'." );

				if( mayKick.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg( 0 ) + "'" );
					String plist = "";
					for( Player toKick : mayKick )
					{
						plist += ChatColor.GRAY + toKick.getName() + ( ( mayKick.indexOf( toKick ) != ( mayKick.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
					}
					p.sendMessage( plist );
					return suc();
				}
				else
				{    // Got ONE player
					if( mayKick.get( 0 ).getName().equalsIgnoreCase( "prosicraft" ) )
					{
						return err( p, "You can't ban such an important person!" );
					}
					String reason = "";
					for( int i = 1; i < this.numArgs(); i++ )
						reason += this.getArg( i ).trim() + " ";
					if( reason.trim().equalsIgnoreCase( "" ) )
						return suc( p, ChatColor.RED + "No permanent ban without a reason." );
					MResult res;
					UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();
					if( ( res = api.doBan( p, mayKick.get( 0 ), ( ( getArgs().length >= 2 ) ? reason.trim() : "No reason provided." ) ) ) == MResult.RES_SUCCESS )
					{
						( ( ultravision ) getParent() ).ownBroadcast( ChatColor.AQUA + mayKick.get( 0 ).getName() + ChatColor.DARK_AQUA + " permanently " + ChatColor.DARK_GRAY + " banned by " + ChatColor.AQUA + p.getName() + ChatColor.DARK_GRAY + " (local)." );
						( ( ultravision ) getParent() ).ownBroadcast( ChatColor.DARK_GRAY + "Reason: " + ChatColor.GOLD + ( ( numArgs() >= 2 ) ? reason.trim() : "No reason." ) );
					}
					else
					{
						return err( p, ChatColor.RED + "Can't ban player: " + res.toString() );
					}
					return suc( p, "Locally banned player. (permanent)" );
				}

			}
			else
			{
				return err( p, "Too few arguments." );
			}

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[BANCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

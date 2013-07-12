/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import java.sql.Time;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class tempwarnCommand extends extendedCommand
{

	public tempwarnCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /warn <player> <reason>
			if( this.numArgs() >= 2 )
			{

				List<Player> mayWarn = getServer().matchPlayer( getArg( 0 ) );

				if( mayWarn == null || mayWarn.isEmpty() )
				{
					return err( p, ChatColor.RED + "Theres no player called '" + getArg( 0 ) + "'." );
				}

				if( mayWarn.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + getArg( 0 ) + "'" );
					String plist = "";
					for( Player toWarn : mayWarn )
					{
						plist += ChatColor.GRAY + toWarn.getName() + ( ( mayWarn.indexOf( toWarn ) != ( mayWarn.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
					}
					p.sendMessage( plist );
					return suc();
				}
				else
				{    // Got ONE player
					String reason = "";
					for( int i = 2; i < numArgs(); i++ )
						reason += getArg( i ).trim();

					String t = this.getArg( 1 );
					if( t.startsWith( "t:" ) )
						t = t.substring( 2, t.length() - 2 );

					long thetime = timeInterpreter.getTime( t );
					if( thetime <= 0 )
						return err( p, "Given Time is not valid!" );

					Time tt = new Time( thetime );

					MResult res;
					UltraVisionAPI api = ( ( ultravision ) getParent() ).getAPI();
					if( ( res = api.setTempWarn( p, mayWarn.get( 0 ), ( ( numArgs() >= 2 ) ? reason : "No reason provided." ), tt ) ) == MResult.RES_SUCCESS )
					{
						( ( ultravision ) getParent() ).ownBroadcast( ChatColor.AQUA + "Player " + mayWarn.get( 0 ).getName() + " has been warned by " + p.getName() + " for " + timeInterpreter.getText( thetime ) + "." );
					}
					else if( res == MResult.RES_ALREADY )
					{
						return suc( p, ChatColor.RED + "This player is already warned." );
					}
					else
					{
						p.sendMessage( ChatColor.RED + "Can't warn player: " + res.toString() );
					}
					return suc( p, "Warned player successfully for " + timeInterpreter.getText( thetime ) );
				}

			}
			else
			{
				return err( p, "Too few arguments." );
			}

		}
		catch( Exception ex )
		{
			MLog.e( "[WARNCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

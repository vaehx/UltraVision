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
public class unwarnCommand extends extendedCommand
{

	public unwarnCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /unwarn <player>
			if( this.numArgs() == 1 )
			{

				List<Player> mayWarn = getServer().matchPlayer( getArg( 0 ) );

				if( mayWarn == null || mayWarn.isEmpty() )
				{
					return suc( p, ChatColor.RED + "Theres no player called '" + getArg( 0 ) + "'." );
				}

				if( mayWarn.size() > 1 )
				{
					norm( p, ChatColor.DARK_AQUA + "There are some players matching '" + getArg( 0 ) + "'" );
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
					UltraVisionAPI api = ( ( ultravision ) getParent() ).getAPI();
					if( !api.isPlayerWarned( mayWarn.get( 0 ).getName() ) )
						return suc( p, ChatColor.RED + "Player is not warned." );

					String reason = "";
					for( int i = 1; i < numArgs(); i++ )
						reason += getArg( i ).trim();
					MResult res;
					if( ( res = api.unwarnPlayer( p, mayWarn.get( 0 ).getName() ) ) == MResult.RES_SUCCESS )
					{
						( ( ultravision ) getParent() ).ownBroadcast( ChatColor.AQUA + "Player " + mayWarn.get( 0 ).getName() + " has been unwarned by " + p.getName() + "." );
					}
					else
					{
						return err( p, ChatColor.RED + "Can't unwarn player: " + res.toString() );
					}
					return suc( p, "Unwarned player successfully." );
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

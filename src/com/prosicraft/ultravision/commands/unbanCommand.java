/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class unbanCommand extends extendedCommand
{

	public unbanCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /unban <player> [note]   --> localban
			if( this.numArgs() >= 1 )
			{

				this.ev( p );

				String reason = "";
				for( int i = 1; i < this.numArgs(); i++ )
					reason += this.getArg( i ).trim() + " ";
				MResult res;
				UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();
				if( ( res = api.pardonPlayer( p, getArg( 0 ), ( ( getArgs().length >= 2 ) ? reason.trim() : "No reason provided." ) ) ) == MResult.RES_SUCCESS )
				{
					( ( ultravision ) getParent() ).ownBroadcast( ChatColor.AQUA + getArg( 0 ) + ChatColor.DARK_AQUA + " pardoned by " + ChatColor.AQUA + p.getName() + ChatColor.DARK_AQUA + " (local). " );
					( ( ultravision ) getParent() ).ownBroadcast( ChatColor.DARK_AQUA + "Reason: " + ChatColor.AQUA + ( ( numArgs() >= 2 ) ? reason.trim() : "No reason." ) );
				}
				else if( res == MResult.RES_NOTINIT )
				{
					return err( p, ChatColor.RED + "Player '" + getArg( 0 ) + "' was never seen on this server." );
				}
				else
				{
					return err( p, ChatColor.RED + "Can't unban player: " + res.toString() );
				}
				return suc( p, "Unbanned player. (local)" );

			}
			else
			{
				return err( p, "Too few arguments." );
			}

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[UNBANCMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}

	}
}

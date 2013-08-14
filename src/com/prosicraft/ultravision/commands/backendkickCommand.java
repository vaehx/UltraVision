/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class backendkickCommand extends extendedCommand
{

	public backendkickCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult consoleRun( CommandSender s )
	{
		try
		{

			// /kick <player>
			if( this.numArgs() >= 1 )
			{

				UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();
				if( !( api == null ) )
				{
					Server srv = getParent().getServer();
					Player p = srv.getPlayer( getArg( 0 ) );
					if( p != null )
					{
						String reason = "";
						for( int i = 1; i < this.numArgs(); i++ )
							reason += this.getArg( i ).trim() + " ";
						reason = reason.trim();
						MResult tres = MResult.RES_UNKNOWN;
						if( api.kickPlayerHard( p.getName(), reason ) == MResult.RES_SUCCESS )
						{
							MLog.i( "Backend Kicked Player successfully." );
						}
						else
							MLog.e( "Can't backend kick: " + tres );
					}
					else
					{
						MLog.e( "Sorry, we can't find that player '" + getArg( 0 ) + "'" );
					}
				}
				else
					MLog.e( "UltraVisionAPI not initialized" );

				return commandResult.RES_SUCCESS;

			}
			else
			{
				MLog.e( "Too few arguments." );
				return commandResult.RES_ERROR;
			}

		}
		catch( Exception ex )
		{
			MLog.e( "[BKICKCMD] " + ex.getMessage() );
			return commandResult.RES_ERROR;
		}
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /kick <player>
			if( this.numArgs() >= 1 )
			{

				ev( p );

				UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();
				if( !( api == null ) )
				{
					Server srv = getParent().getServer();
					Player pl = srv.getPlayer( getArg( 0 ) );
					if( pl != null )
					{
						String reason = "";
						for( int i = 1; i < this.numArgs(); i++ )
							reason += this.getArg( i ).trim() + " ";
						reason = reason.trim();
						MResult tres = MResult.RES_UNKNOWN;
						if( api.kickPlayerHard( pl.getName(), reason ) == MResult.RES_SUCCESS )
						{
							return suc( p, "Backend Kicked Player successfully." );
						}
						else
							return err( p, "Can't backend kick: " + tres );
					}
					else
					{
						return err( p, "Sorry, we can't find that player '" + getArg( 0 ) + "'" );
					}
				}
				else
					err( p, "UltraVisionAPI not initialized" );

				return commandResult.RES_SUCCESS;

			}
			else
			{
				MLog.e( "Too few arguments." );
				return commandResult.RES_ERROR;
			}

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[BKICKCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

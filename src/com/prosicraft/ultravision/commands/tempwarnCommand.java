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

			String commandSyntax = "/tempwarn <player> <time> [reason]";
			if( this.numArgs() >= 2 )
			{
				UltraVisionAPI api = ( ( ultravision ) getParent() ).getAPI();
				List<UltraVisionAPI.MatchUserResult> mayWarn = api.matchUser(getArg(0), false);

				if( mayWarn == null || mayWarn.isEmpty() )
				{
					return err( p, ChatColor.RED + "Theres no player called '" + getArg( 0 ) + "'." );
				}

				if( mayWarn.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + getArg( 0 ) + "'" );
					String plist = "";
					for( UltraVisionAPI.MatchUserResult toWarn : mayWarn )
					{
						String formattedName = toWarn.name + ((toWarn.isOnline) ? "" : " (off)");
						plist += ChatColor.GRAY + formattedName + ( ( mayWarn.indexOf( toWarn ) != ( mayWarn.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
					}

					p.sendMessage( plist );
					return suc();
				}
				else
				{    // Got ONE player
					String reason = "No reason provided.";
					if (numArgs() > 2)
					{
						for( int i = 2; i < numArgs(); i++ )
							reason += getArg( i ).trim();
					}

					String t = this.getArg( 1 );
					if( t.startsWith( "t:" ) )
						t = t.substring( 2, t.length() - 2 );

					long thetime = timeInterpreter.getTime( t );
					if( thetime <= 0 )
						return err( p, "Given Time is not valid!" );

					Time warnDuration = new Time( thetime );
					String warnDurationStr = warnDuration.toString();

					MResult res;
					ultravision uvPlugin = (ultravision)getParent();
					if(MResult.RES_SUCCESS == (res = api.warnPlayerTemporarily(p, mayWarn.get(0).pIdent, reason, warnDuration)))
					{
						uvPlugin.ownBroadcast(ChatColor.AQUA + "Player " + mayWarn.get(0).name + " has been warned by " + p.getName() + " for " + timeInterpreter.getText(thetime) + ".");
					}
					else if( res == MResult.RES_ALREADY )
					{
						return suc( p, ChatColor.RED + "This player is already warned." );
					}
					else
					{
						p.sendMessage( ChatColor.RED + "Can't warn player: " + res.toString() );
					}

					return suc( p, "Warned player successfully for " + warnDurationStr );
				}

			}
			else
			{
				return err( p, "Too few arguments. " + commandSyntax );
			}

		}
		catch( Exception ex )
		{
			MLog.e( "[TEMPWARNCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

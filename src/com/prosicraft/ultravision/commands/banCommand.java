/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 * 
 * (c) 2010-2014 prosicraft
 * All rights reserved.
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

			// /rban <player> [reason]   --> localban
			if( this.numArgs() >= 1 )
			{

				this.ev( p );

				UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();
				List<UltraVisionAPI.MatchUserResult> mayKick = api.matchUser(getArg(0), false);

				if( mayKick == null || mayKick.isEmpty() )
					return err( p, ChatColor.RED + "There's no player called '" + this.getArg( 0 ) + "'." );

				if( mayKick.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg( 0 ) + "'" );
					String plist = "";
					for( UltraVisionAPI.MatchUserResult toKick : mayKick )
					{
						String formattedName = toKick.name + ((toKick.isOnline) ? "" : " (off)");
						plist += ChatColor.GRAY + formattedName + ( ( mayKick.indexOf( toKick ) != ( mayKick.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
					}
					p.sendMessage( plist );
					return suc();
				}
				else
				{    // Got ONE player
					if( mayKick.get( 0 ).name.equalsIgnoreCase( "prosicraft" ) )				
						return err( p, "You can't ban such an important person!" );				
					
					String reason = "";
					for( int i = 1; i < this.numArgs(); i++ )
						reason += this.getArg( i ).trim() + " ";
					if( reason.trim().equalsIgnoreCase( "" ) )
						return suc( p, ChatColor.RED + "No permanent ban without a reason." );
					MResult res;					
					if( ( res = api.banPlayerLocally( p, mayKick.get(0).pIdent, ( ( getArgs().length >= 2 ) ? reason.trim() : "No reason provided." ) ) ) == MResult.RES_SUCCESS )
					{
						( ( ultravision ) getParent() ).ownBroadcast( ChatColor.AQUA + mayKick.get( 0 ).name + ChatColor.DARK_AQUA + " permanently " + ChatColor.DARK_GRAY + " banned by " + ChatColor.AQUA + p.getName() + ChatColor.DARK_GRAY + " (local)." );
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

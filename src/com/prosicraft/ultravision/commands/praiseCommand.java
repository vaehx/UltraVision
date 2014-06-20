/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 * 
 * (c) 2010-2014 prosicraft
 * All rights reserved.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.PlayerIdent;
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
public class praiseCommand extends extendedCommand
{

	public praiseCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /praise <player>
			if( hasArgs( 1 ) )
			{

				ev( p );

				List<Player> mayPraise = getParent().getServer().matchPlayer( getArg( 0 ) );

				if( mayPraise == null || mayPraise.isEmpty() )
					return err( p, ChatColor.RED + "There's no player called '" + this.getArg( 0 ) + "'." );

				if( mayPraise.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg( 0 ) + "'" );
					String plist = "";
					for( Player toKick : mayPraise )
					{
						plist += ChatColor.GRAY + toKick.getName() + ( ( mayPraise.indexOf( toKick ) != ( mayPraise.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
					}
					p.sendMessage( plist );
					return suc();
				}
				else
				{    // Got ONE player
					String reason = "";
					for( int i = 1; i < this.numArgs(); i++ )
						reason += this.getArg( i ).trim();
					MResult res;
					UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();

					if( api.isPlayerPraisedBy( new PlayerIdent(p), new PlayerIdent(mayPraise.get( 0 )) ) )
						return suc( p, "You already praised this player. Use /unpraise to unpraise." );

					if( ( res = api.praisePlayer( p, new PlayerIdent(mayPraise.get( 0 )) ) ) == MResult.RES_SUCCESS )
					{
						mayPraise.get( 0 ).sendMessage( ChatColor.DARK_AQUA + "You got praised by " + ChatColor.AQUA + p.getName() );
					}
					else if( res == MResult.RES_NOTGIVEN )
					{
						return err( p, "This user seems not to be online. But he needs to be." );
					}
					else
					{
						return err( p, "Can't praise player: " + res.toString() );
					}
					return suc( p, "Praised player successfully." );
				}

			}
			else
			{
				return err( p, "Too few arguments." );
			}

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[PRAISECMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

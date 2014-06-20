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
public class accfriendCommand extends extendedCommand
{

	public accfriendCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /accfriend <player> [yes / no]
			if( hasArgs( 1 ) || hasArgs( 2 ) )
			{

				ev( p );

				List<Player> mayFriend = getParent().getServer().matchPlayer( getArg( 0 ) );

				if( mayFriend == null || mayFriend.isEmpty() )
					return err( p, ChatColor.RED + "There's no player called '" + this.getArg( 0 ) + "'." );

				if( mayFriend.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg( 0 ) + "'" );
					String plist = "";
					for( Player toKick : mayFriend )
					{
						plist += ChatColor.GRAY + toKick.getName() + ( ( mayFriend.indexOf( toKick ) != ( mayFriend.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
					}
					p.sendMessage( plist );
					return suc();
				}
				else
				{    // Got ONE player
					MResult res;
					UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();

					boolean accept = true;
					if( hasArgs( 2 ) )
					{
						if( getArg( 1 ).equalsIgnoreCase( "no" ) )
							accept = false;
						else if( getArg( 1 ).equalsIgnoreCase( "yes" ) )
							accept = true;
						else
							return suc( p, ChatColor.RED + "No valid specification: '" + getArg( 1 ) + "'" );
					}

					if( accept )
					{
						if( ( res = api.acceptFriendship( new PlayerIdent(p), new PlayerIdent(mayFriend.get(0)) ) ) == MResult.RES_SUCCESS )
						{
							mayFriend.get( 0 ).sendMessage( ChatColor.AQUA + p.getName() + ChatColor.DARK_AQUA + " is now your friend." );
						}
						else
						{
							p.sendMessage( ChatColor.RED + "Can't add player as friend: " + res.toString() );
						}
						return suc( p, "Accepted Friendship successfully. Your now friends." );
					}
					else
					{
						if( ( res = api.rejectFriendship( new PlayerIdent(p), new PlayerIdent(mayFriend.get(0)) ) ) != MResult.RES_SUCCESS )
						{
							p.sendMessage( ChatColor.RED + "Can't cancel friendship request: " + res.toString() );
						}
						return suc( p, "Cancelled Friendship." );
					}

				}

			}
			else
			{
				return err( p, "Too few arguments." );
			}

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[ADDFRIENDCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

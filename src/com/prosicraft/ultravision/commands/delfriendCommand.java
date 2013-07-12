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
public class delfriendCommand extends extendedCommand
{

	public delfriendCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /delfriend <player>
			if( hasArgs( 1 ) )
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

					if( !api.getFriends( p ).contains( mayFriend.get( 0 ).getName() ) )
						return suc( p, "You are not in friendship with " + mayFriend.get( 0 ).getName() );

					if( ( res = api.delFriend( p, mayFriend.get( 0 ) ) ) == MResult.RES_SUCCESS )
					{
					}
					else
					{
						p.sendMessage( ChatColor.RED + "Can't remove player as friend: " + res.toString() );
					}
					return suc( p, "You are not longer Friends." );
				}

			}
			else
			{
				return err( p, "Too few arguments." );
			}

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[DELFRIENDCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

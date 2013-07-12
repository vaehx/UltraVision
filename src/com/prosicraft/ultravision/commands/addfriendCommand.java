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
public class addfriendCommand extends extendedCommand
{

	public addfriendCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /addfriend <player>
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

					if( api.getFriends( p ).contains( mayFriend.get( 0 ).getName() ) )
						return suc( p, "You are already in friendship with " + mayFriend.get( 0 ).getName() );

					if( p.getName().equalsIgnoreCase( mayFriend.get( 0 ).getName() ) )
						return suc( p, ChatColor.RED + "You can't add yourself as a friend." );

					if( ( res = api.requestFriend( p, mayFriend.get( 0 ) ) ) == MResult.RES_SUCCESS )
					{
						mayFriend.get( 0 ).sendMessage( ChatColor.AQUA + p.getName() + ChatColor.DARK_AQUA + " wants to be a friend of you." );
						mayFriend.get( 0 ).sendMessage( ChatColor.DARK_AQUA + "Please answer with " + ChatColor.GOLD + "/accfriend " + p.getName() + " yes" + ChatColor.DARK_AQUA + " or " + ChatColor.GOLD + "no" );
					}
					else
					{
						p.sendMessage( ChatColor.RED + "Can't add player as friend: " + res.toString() );
					}
					return suc( p, "You sent a friendship request. This must be accepted by the player." );
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

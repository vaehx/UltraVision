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
public class delnoteCommand extends extendedCommand
{

	public delnoteCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /delnote <player> <id>
			if( hasArgs( 2 ) )
			{

				ev( p );

				List<Player> mayNote = getParent().getServer().matchPlayer( getArg( 0 ) );

				if( mayNote == null || mayNote.isEmpty() )
					return err( p, ChatColor.RED + "There's no player called '" + this.getArg( 0 ) + "'." );

				if( mayNote.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg( 0 ) + "'" );
					String plist = "";
					for( Player toNote : mayNote )
					{
						plist += ChatColor.GRAY + toNote.getName() + ( ( mayNote.indexOf( toNote ) != ( mayNote.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
					}
					p.sendMessage( plist );
					return suc();
				}
				else
				{    // Got ONE player
					int id;
					id = Integer.parseInt( getArg( 1 ) );
					MResult res;
					UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();

					String thenote;
					try
					{
						thenote = ( api.getPlayerNotes( mayNote.get( 0 ).getName() ).get( api.getPlayerNotes( mayNote.get( 0 ).getName() ).keySet().toArray()[id].toString() ) );
					}
					catch( ArrayIndexOutOfBoundsException | NullPointerException ex )
					{
						return err( p, "There is no Note with id " + ChatColor.GOLD + id );
					}

					if( ( res = api.delPlayerNote( p, mayNote.get( 0 ).getName(), id ) ) == MResult.RES_SUCCESS )
					{
						mayNote.get( 0 ).sendMessage( ChatColor.DARK_AQUA + "You've lost a note: " + ChatColor.WHITE + thenote );
					}
					else
					{
						p.sendMessage( ChatColor.RED + "Can't remove note from player: " + res.toString() );
					}
					return suc( p, "Removed note from player successfully." );
				}

			}
			else
			{
				return err( p, "Too few arguments." );
			}

		}
		catch( wrongParentException | wrongPlayerException | NumberFormatException ex )
		{
			MLog.e( "[DELNOTECMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}

	}
}

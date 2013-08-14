/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.UVClickAuth;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class clickUnregisterCommand extends extendedCommand
{
	public clickUnregisterCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{
		try
		{
			// /clickunregister <player>
			this.ev( p );
			UVClickAuth clickauth = ( (ultravision)getParent() ).getClickAuth();
			if( clickauth == null )
			{
				return suc( p, ChatColor.YELLOW + "The UV-ClickAuth system is not used." );
			}

			if( numArgs() < 1 )
			{
				return err( p, "Too few arguments" );
			}

			String thePlayer = getArg( 0 );
			if( !clickauth.isRegistered( p.getName() ) )
			{
				p.sendMessage( ChatColor.GOLD + thePlayer + " is not registered in ClickAuth System." );
			}

			clickauth.unRegister( thePlayer );
			clickauth.saveToFile();
			for( int i = 0; i < getServer().getOnlinePlayers().length; i++ )
			{
				if( getServer().getOnlinePlayers()[i].getName().equalsIgnoreCase( thePlayer ) )
				{
					getServer().getOnlinePlayers()[i].sendMessage( ChatColor.GOLD + "You have been unregistered from ClickAuth." );
					return suc( p, thePlayer + " has been unregistered." );
				}
			}
			return err( p, "Cannot find this player" );
		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[CLICKUNREGISTERCMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}
	}
}
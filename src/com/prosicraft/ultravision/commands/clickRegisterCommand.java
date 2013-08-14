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
public class clickRegisterCommand extends extendedCommand
{
	public clickRegisterCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{
		try
		{
			// /clickregister
			this.ev( p );
			UVClickAuth clickauth = ( (ultravision)getParent() ).getClickAuth();
			if( clickauth == null )
			{
				return suc( p, ChatColor.YELLOW + "The UV-ClickAuth system is not used." );
			}

			if( clickauth.isRegistered( p.getName() ) )
			{
				return suc( p, ChatColor.GOLD + "You're already registered in the UV-ClickAuth System." );
			}

			if( clickauth.toggleRegistering( p ) )
			{
				p.sendMessage( ChatColor.AQUA + "--- Registering for UV-ChatAuth ---" );
				p.sendMessage( ChatColor.GRAY + " Place Blocks with distance from each other." );
				p.sendMessage( ChatColor.GRAY + " Remember the location of the Blocks, relatively." );
				p.sendMessage( ChatColor.GRAY + " Finish by typing " + ChatColor.AQUA + "/caregister" + ChatColor.GRAY + " again." );
			}
			else
			{
				p.sendMessage( ChatColor.AQUA + "--- Finished UV-ClickAuth Registering ---" );
				p.sendMessage( ChatColor.GRAY + " Please Login now." );
			}
			return suc();
		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[CLICKREGISTERCMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}
	}
}
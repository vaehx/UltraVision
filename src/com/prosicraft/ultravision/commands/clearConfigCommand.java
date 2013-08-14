/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class clearConfigCommand extends extendedCommand
{
	public clearConfigCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{
		try
		{
			// /uvclearconfig
			this.ev( p );
			p.sendMessage( ChatColor.GOLD + "Trying to cleanup Configuration..." );
			if( !( (ultravision)getParent() ).clearConfig() )
			{
				return err( p, "Failed to cleanup Configuration..." );
			}
			else
			{
				return suc( p, "Cleanup Configuration was successful." );
			}
		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[CLEARCONFIGCMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}
	}
}
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
 * @author passi
 */
public class gcCommand extends extendedCommand
{

	public gcCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /gc

			this.ev( p );

			p.sendMessage( "Garbage collecting..." );
			Runtime.getRuntime().gc();

			p.sendMessage( ChatColor.DARK_AQUA + "Total Memory: " + ChatColor.AQUA + ( Runtime.getRuntime().totalMemory() / 1048576 ) + " MBytes" );
			p.sendMessage( ChatColor.DARK_AQUA + "Max. Memory: " + ChatColor.AQUA + ( Runtime.getRuntime().maxMemory() / 1048576 ) + " MBytes" );
			p.sendMessage( ChatColor.DARK_AQUA + "Min. Memory: " + ChatColor.AQUA + ( ( Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() ) / 1048576 ) + " MBytes" );
			p.sendMessage( ChatColor.DARK_AQUA + "Free Memory: " + ChatColor.AQUA + ( Runtime.getRuntime().freeMemory() / 1048576 ) + " MBytes" );
			p.sendMessage( ChatColor.DARK_AQUA + "Processors: " + ChatColor.AQUA + Runtime.getRuntime().availableProcessors() );

			p.sendMessage( ChatColor.DARK_AQUA + "Java VM: " + ChatColor.AQUA + System.getProperty( "java.vm.name" ) );
			p.sendMessage( ChatColor.DARK_AQUA + "Java Version: " + ChatColor.AQUA + System.getProperty( "java.runtime.version" ) );
			p.sendMessage( ChatColor.DARK_AQUA + "OS Name: " + ChatColor.AQUA + System.getProperty( "os.name" ) );

			return suc();

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[GCCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

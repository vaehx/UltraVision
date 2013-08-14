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
public class ultravisionCommand extends extendedCommand
{
	public ultravisionCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{
		try
		{
			// /ultravision [reload]
			this.ev( p );
			ultravision uv = (ultravision)getParent();
			if( numArgs() == 1 && getArg( 0 ).equalsIgnoreCase( "reload" ) )
			{
				uv.setMConfig( null );
				uv.initConfig();
				uv.loadTemplateSelection();
				return suc( p, "Reloaded Config" );
			}

			p.sendMessage( ChatColor.DARK_GRAY + "=== " + ChatColor.DARK_AQUA + "Server running " + ChatColor.AQUA + "ULTRAVISION" + ChatColor.GRAY + " version " + ChatColor.AQUA + uv.getfPDesc().getVersion() + ChatColor.DARK_GRAY + " ===" );

			p.sendMessage( ChatColor.GOLD + "This Bukkit Plugin provides functionality for every security, as well as frondemd and logging purposes on your MC-Server." );

			String coms = "";
			for( String lecom : uv.getfPDesc().getCommands().keySet() )
			{
				coms += lecom + ChatColor.DARK_GRAY + ", " + ChatColor.GRAY;
			}

			coms = coms.substring( 0, coms.length() - 2 );
			return suc( p, ChatColor.DARK_GRAY + "Commands: " + ChatColor.GRAY + coms );
		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[CLEARCONFIGCMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}
	}
}
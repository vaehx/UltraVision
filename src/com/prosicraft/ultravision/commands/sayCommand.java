/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. edit
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class sayCommand extends extendedCommand
{

	public sayCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult consoleRun( CommandSender s )
	{

		try
		{

			// /say text

			if( numArgs() < 1 )
			{
				MLog.e( "Too few arguments." );
				return commandResult.RES_ERROR;
			}

			String out = "";
			for( int i = 0; i < numArgs(); i++ )
				out += getArg( i ) + " ";

			out = out.trim();

			if( out.equalsIgnoreCase( "reload" ) )
			{
				out = ChatColor.RED + "Serverreload. Do " + ChatColor.BOLD + "not" + ChatColor.RESET + ChatColor.RED + " chat or use any command.";
			}
			else if( out.equalsIgnoreCase( "finish" ) )
			{				
				out = ChatColor.RED + "Reload finished." + (((ultravision)getParent()).IsUsingAuthorizer() ? " You're logged out." : "");
			}

			( ( ultravision ) getParent() ).ownBroadcast( ChatColor.DARK_GRAY + "  [" + ChatColor.LIGHT_PURPLE + "Server" + ChatColor.DARK_GRAY + "] " + out );

			return suc();

		}
		catch( Exception ex )
		{
			MLog.e( "[GCCMD] " + ex.getMessage() );
			return commandResult.RES_ERROR;
		}

	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /say text

			if( numArgs() < 1 )
				return err( p, "Too few arguments." );

			this.ev( p );

			String out = "";
			for( int i = 0; i < numArgs(); i++ )
				out += getArg( i ) + " ";

			out = out.trim();

			if( out.equalsIgnoreCase( "reload" ) )
			{
				out = ChatColor.RED + "Serverreload. Do " + ChatColor.BOLD + "not" + ChatColor.RESET + ChatColor.RED + " chat or use any command.";
			}
			else if( out.equalsIgnoreCase( "finish" ) )
			{				
				out = ChatColor.RED + "Reload finished." + (((ultravision)getParent()).IsUsingAuthorizer() ? " You're logged out." : "");
			}

			( ( ultravision ) getParent() ).ownBroadcast( ChatColor.DARK_GRAY + "  [" + ChatColor.DARK_RED + p.getDisplayName() + ChatColor.DARK_GRAY + "] " + out );

			return suc();

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[GCCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

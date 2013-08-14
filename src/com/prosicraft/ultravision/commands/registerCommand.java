/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MLog;
import com.prosicraft.ultravision.util.MResult;
import java.sql.Array;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class registerCommand extends extendedCommand
{
	public registerCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{
		try
		{
			this.ev( p );

			// /register <password>
			if( numArgs() == 1 )
			{
				MAuthorizer auth = ( (ultravision)getParent() ).getAuthorizer();
				if( auth.isRegistered( p ) )
				{
					return suc( p, ChatColor.GOLD + "You're already registered in the login system." );
				}

				String givenPassword = getArg( 0 );
				String[] badPasswords = new String[] { "password", "passwort", p.getName(), p.getCustomName(), p.getDisplayName() };
				if( Arrays.asList( badPasswords ).contains( givenPassword ) )
				{
					return err( p, ChatColor.RED + "This password is too simple!" );
				}

				MResult res;
				if( ( res = auth.register( p, givenPassword ) ) == MResult.RES_SUCCESS )
				{
					p.sendMessage( ChatColor.GREEN + "Registered successfully in login system as " + p.getName() + "." );
					p.sendMessage( ChatColor.GREEN + "Login with " + ChatColor.GOLD + "/login YourPassword" + ChatColor.GREEN + "." );
				}
				else
					MLog.e( "Couldn't register new player in login system (player=" + p.getName() + "): " + String.valueOf( res ) );
				return suc();
			}
			else if( numArgs() == 0 )
			{
				return err( p, "Too few arguments (Please specify a password)!" );
			}

			return err( p, "Too much arguments." );
		}
		catch( wrongPlayerException | wrongParentException ex )
		{
			MLog.e( "[REGISTERCMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}
	}
}

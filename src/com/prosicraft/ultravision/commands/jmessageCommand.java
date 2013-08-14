/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.JMessage.JMessage;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class jmessageCommand extends extendedCommand
{
	public jmessageCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	private void showHelp( Player p )
	{
		p.sendMessage( ChatColor.DARK_GRAY + "JMessage commands:" );
		p.sendMessage( ChatColor.GOLD + "/jmessage reload " + ChatColor.GRAY + " - Reloads the JMessage configuration part" );
		p.sendMessage( ChatColor.GOLD + "/jmessage assign <player> <message> " + ChatColor.GRAY + " - Assigns login message to player" );
		p.sendMessage( ChatColor.GOLD + "/jmessage preview " + ChatColor.GRAY + " - Shows preview of login messages" );
	}

	@Override
	public commandResult run( Player p )
	{
		try
		{
			// /jmessage <reload|assign|preview>
			this.ev( p );
			JMessage jmsg = ( (ultravision)getParent() ).getMessager();
			if( numArgs() == 0 )
			{
				this.showHelp( p );
				return suc();
			}
			else
			{
				String baseParam = getArg( 0 );
				if( baseParam.equalsIgnoreCase( "reload" ) )
				{
					jmsg.load( ( (ultravision)getParent() ).getMConfig() );
					return suc( p, "Reloaded JMessage configuration successfully" );
				}
				else if( baseParam.equalsIgnoreCase( "preview" ) )
				{
					jmsg.doJoinTest( p );
					jmsg.doLeaveTest( p );
					return suc();
				}
				else if( baseParam.equalsIgnoreCase( "assign" ) )
				{
					if( numArgs() < 3 )
						return err( p, "Too few arguments." );

					String playerName = getArg( 1 );
					String message = "";
					for( int n = 2; n < ( numArgs() ); n++ )
					{
						message += getArg( n ) + " ";
					}

					jmsg.assignIndividual( playerName, message.trim() );
					return suc( p, "Assigned join message to '" + playerName + "' successfully." );
				}
				else
				{
					showHelp( p );
					return suc();
				}
			}
		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[JMESSAGECMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}
	}
}
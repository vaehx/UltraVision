/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 *
 * (c) 2010-2014 prosicraft
 * All rights reserved.
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
 * @author prosicraft
 */
public class warnCommand extends extendedCommand
{

	public warnCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			String commandSyntax = "/warn <player> [reason]";
			if( this.numArgs() >= 2 )
			{

				ultravision uvPlugin = (ultravision)getParent();
				UltraVisionAPI api = uvPlugin.getAPI();
				List<UltraVisionAPI.MatchUserResult> mayWarn = api.matchUser(getArg(0), false);

				if( mayWarn == null || mayWarn.isEmpty() )
				{
					return err( p, ChatColor.RED + "Theres no player called '" + getArg( 0 ) + "'." );
				}

				if( mayWarn.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + getArg( 0 ) + "'" );
					String plist = "";
					for(UltraVisionAPI.MatchUserResult toWarn : mayWarn)
					{
						String formattedName = (toWarn.isOnline) ? toWarn.name : toWarn.name + " (off)";
						boolean isLastItem = mayWarn.indexOf(toWarn) != (mayWarn.size() - 1 );
						plist += ChatColor.GRAY + formattedName + ((isLastItem) ? ChatColor.DARK_GRAY + ", " : "");
					}

					p.sendMessage( plist );
					return suc();
				}
				else // got only ONE player
				{
					if (mayWarn.get(0).name.equalsIgnoreCase(p.getName()))
						return err(p, "You cannot warn yourself!");

					String reason = "No reason provided.";
					if (numArgs() > 1)
					{
						for( int i = 1; i < numArgs(); i++ )
							reason += getArg( i ).trim();
					}

					MResult res;
					if(MResult.RES_SUCCESS == (res = api.warnPlayer(p, mayWarn.get(0).pIdent, reason)))
					{
						uvPlugin.ownBroadcast(ChatColor.AQUA + "Player " + mayWarn.get(0).name + " has been warned by " + p.getName() + ".");
					}
					else if(res == MResult.RES_ALREADY)
					{
						return suc(p, ChatColor.RED + "This player is already warned. For more information see " + ChatColor.ITALIC + "/uvstat warn " + mayWarn.get(0).name);
					}
					else
					{
						p.sendMessage(ChatColor.RED + "Can't warn player: " + res.toString());
					}

					return suc( p, "Permanently warned player successfully." );
				}

			}
			else
			{
				return err( p, "Too few arguments. " + commandSyntax );
			}

		}
		catch( Exception ex )
		{
			MLog.e( "[WARNCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

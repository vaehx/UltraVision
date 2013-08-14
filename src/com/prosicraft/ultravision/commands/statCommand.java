/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.base.UVBan;
import com.prosicraft.ultravision.base.UVPlayerInfo;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class statCommand extends extendedCommand
{

	// /uvstat [ban|kick|note|warning|mute|praise|time|friend|all] [playername|all]
	// ------------------+ UVSTAT Page (1/2) +--------------
	// Nothing == all all =
	//      There are 3 bans, 2 warnings and 4 muted players.
	//      More information using flags:
	//          ban|kick|note|warning|mute|praise|time|friend|all
	// ban all =
	//      prosicraft by theDido (l,1d 2min 30sec): Griefing in th...
	//      theDido by sekshun8 (g,perm): He actually said that the...
	// ban prosicraft =
	//      prosicraft by theDido (l,1d 2min 30sec): Grieifng in th...
	// note all =
	//      theDido -> prosicraft #1: He got me ...arggh he's so st...
	//      sekshun8 -> prosicraft #2: adsjsfskdfjskdfjlskdfjlskfjl...
	//      theDidio -> sekshun8 #1: afsfjsldfkjsldfkjsldfksjdlfjll...
	// warning == note
	// mute all =
	//      theDido by prosicraft: No Reason provided.
	// praise all = (TOP FIVE)
	//      #1 prosicraft: 59 praises
	//      #2 theDido: 50 praises
	//      #3 sekshun8: 2 praises
	// time all = (TOP FIVE, TOTAL TIME)
	//      #1 prosicraft: 3 Month 2 Days 1 Minute 1 Sec
	//      #2 theDido: 2 Month...
	//
	// ----------------------------------------------------
	public statCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			UltraVisionAPI api = ( ( ultravision ) this.getParent() ).getAPI();

			Player pl = null;
			UVPlayerInfo uI = null;
			String t = "all";
			String theName = "";

			ev( p );

			if( !( this.numArgs() > 0 ) )
			{
				return err( p, "Too few arguments." );
			}

			if( hasArgs( 1 ) )
			{

				if( getArg( 0 ).equalsIgnoreCase( "ban" )
					|| getArg( 0 ).equalsIgnoreCase( "kick" )
					|| getArg( 0 ).equalsIgnoreCase( "note" )
					|| getArg( 0 ).equalsIgnoreCase( "warning" )
					|| getArg( 0 ).equalsIgnoreCase( "mute" )
					|| getArg( 0 ).equalsIgnoreCase( "praise" )
					|| getArg( 0 ).equalsIgnoreCase( "time" )
					|| getArg( 0 ).equalsIgnoreCase( "friend" )
					|| getArg( 0 ).equalsIgnoreCase( "all" )
					|| getArg( 0 ).equalsIgnoreCase( "reg" ) )
				{
					t = getArg( 0 );
				}
				else
				{
					List<Player> mayStat = getParent().getServer().matchPlayer( getArg( 0 ) );

					if( mayStat == null || mayStat.isEmpty() )
					{
						return err( p, ChatColor.RED + "There's no player called '" + this.getArg( 0 ) + "'." );
					}

					if( mayStat.size() > 1 )
					{
						p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + this.getArg( 0 ) + "'" );
						String plist = "";
						for( Player toKick : mayStat )
						{
							plist += ChatColor.GRAY + toKick.getName() + ( ( mayStat.indexOf( toKick ) != ( mayStat.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
						}
						p.sendMessage( plist );
						return suc();
					}
					else
					{
						pl = mayStat.get( 0 );
						theName = pl.getName();
					}
				}


			}
			else
			{ // 2 PArams, needs order !

				// Eval param 1
				if( getArg( 0 ).equalsIgnoreCase( "ban" )
					|| getArg( 0 ).equalsIgnoreCase( "kick" )
					|| getArg( 0 ).equalsIgnoreCase( "note" )
					|| getArg( 0 ).equalsIgnoreCase( "warning" )
					|| getArg( 0 ).equalsIgnoreCase( "mute" )
					|| getArg( 0 ).equalsIgnoreCase( "praise" )
					|| getArg( 0 ).equalsIgnoreCase( "time" )
					|| getArg( 0 ).equalsIgnoreCase( "friend" )
					|| getArg( 0 ).equalsIgnoreCase( "all" )
					|| getArg( 0 ).equalsIgnoreCase( "reg" ) )
				{
					t = getArg( 0 );
				}
				else
				{
					return err( p, "Stat type not recognized: '" + getArg( 0 ) + "'" );
				}

				// Eval param 2
				theName = getArg( 1 );

				List<Player> mayStat = getParent().getServer().matchPlayer( theName );
				if( mayStat == null || mayStat.isEmpty() )
				{
					if( ( uI = api.getPlayerInfo( theName ) ) == null )
					{
						return err( p, ChatColor.RED + "There's no player called '" + theName + "'." );
					}
					mayStat = new ArrayList<>();
					mayStat.add( api.getPlayer( theName ) );
				}

				if( mayStat.size() > 1 )
				{
					p.sendMessage( ChatColor.DARK_AQUA + "There are some players matching '" + theName + "'" );
					String plist = "";
					for( Player toKick : mayStat )
					{
						plist += ChatColor.GRAY + toKick.getName() + ( ( mayStat.indexOf( toKick ) != ( mayStat.size() - 1 ) ) ? ChatColor.DARK_GRAY + ", " : "" );
					}
					p.sendMessage( plist );
					return suc();
				}
				else if( mayStat.size() == 1 )
				{
					pl = mayStat.get( 0 );
					theName = pl.getName();
				}

			}

			if( uI == null )
			{
				if( pl == null )
					return err( p, "No information about no player." );

				uI = api.getPlayerInfo( pl.getName() );

				if( uI == null )
					return err( p, "Cannot get Player information about player " + pl.getName() + "!" );
			}

			// now we have the PlayerInfo instance and we can read this out.

			if( t.equalsIgnoreCase( "time" ) )
			{
				return suc( p, ChatColor.DARK_AQUA + "Total Online time of " + ChatColor.AQUA + getArg( 1 ) + ChatColor.DARK_AQUA + ": " + ChatColor.GOLD + timeInterpreter.getText( uI.getOnlineTime() ) );
			}
			else if( t.equalsIgnoreCase( "registered" ) || t.equalsIgnoreCase( "reg" ) )
			{
				if( !api.isAuthorizerEnabled() )
				{
					return suc( p, ChatColor.YELLOW + "Authorizer is not in use." );
				}
				if( api.getAuthorizer().isRegistered( theName ) )
				{
					return suc( p, ChatColor.GREEN + "Player '" + theName + "' is registered." );
				}
				else
				{
					return suc( p, ChatColor.RED + "Player '" + theName + "' is not registered." );
				}
			}
			else if( t.equalsIgnoreCase( "ban" ) )
			{
				if( api.isPlayerBanned( theName ) )
				{
					p.sendMessage( ChatColor.GREEN + "Player '" + theName + "' is banned." );
				}
				else
					p.sendMessage( ChatColor.RED + "Player '" + theName + "' is not banned." );
				if( uI.banHistory.isEmpty() )
					return suc( p, ChatColor.DARK_AQUA + "Player " + ChatColor.AQUA + theName + ChatColor.DARK_AQUA + " has no bans in history." );
				else
				{
					for( UVBan fr : uI.banHistory )
						p.sendMessage( ChatColor.DARK_AQUA + fr.getFormattedInfo() );
					return suc();
				}
			}
			else if( t.equalsIgnoreCase( "friend" ) )
			{
				String out = "";
				for( String fr : uI.friends )
				{
					out += fr + ", ";
				}
				if( out.length() >= 2 )
					out = out.substring( 0, out.length() - 2 );
				else
					out = "foreveralone";
				p.sendMessage( ChatColor.DARK_AQUA + "Player " + ChatColor.AQUA + theName + ChatColor.DARK_AQUA + " has " + ChatColor.GOLD + uI.friends.size() + ChatColor.DARK_AQUA + " friends:" );
				return suc( p, ChatColor.WHITE + out );
			}
			else
			{
				return err( p, "Stat type '" + t + "' isn't implemented yet." );
			}

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[PRAISECMD] " + ex.getMessage() );
			ex.printStackTrace( System.out );
			return err( p, "Failed to execute command." );
		}

	}
}

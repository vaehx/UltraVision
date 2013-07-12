/*
 * Extended Command Class
 * written by prosicraft
 *
 * simply extend your command class with extendedCommand and override run method
 * you can remove the Exception throwing if you don't need this.
 */
package com.prosicraft.ultravision.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author passi
 */
public class extendedCommand<PluginClass extends JavaPlugin>
{

	private PluginClass parent = null;
	private String name = "";
	private String[] args = null;

	public extendedCommand( PluginClass handle, String[] args )
	{
		try
		{
			this.parent = ( PluginClass ) handle;
			this.args = args;
		}
		catch( Exception ex )
		{
			System.out.println( "[" + handle.getClass().getName() + "::newCommand] Failed: " + ex.getMessage() );
		}
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public commandResult run( Player p ) throws Exception
	{
		throw new Exception( "me.prosicraft.extendedCommand<" + this.parent.getClass().getName() + ">::run() cannot be used. Please override this function or use specific Commandclasses." );
	}

	public commandResult consoleRun( CommandSender s ) throws Exception
	{
		throw new Exception( "me.prosicraft.extendedCommand<" + this.parent.getClass().getName() + ">::run() cannot be used. Please override this function or use specific Commandclasses." );
	}

	public boolean isParentSet()
	{
		if( this.parent != null )
			try
			{
				return ( this.parent.isEnabled() );
			}
			catch( Exception ex )
			{
				return false;
			}
		return false;
	}

	public PluginClass getParent()
	{
		return parent;
	}

	public Server getServer()
	{
		return parent.getServer();
	}

	public boolean hasArgs()
	{
		return ( this.args != null && this.args.length > 0 );
	}

	public boolean hasArgs( int num )
	{
		return ( this.args != null && this.args.length == num );
	}

	public int numArgs()
	{
		try
		{
			return args.length;
		}
		catch( Exception ex )
		{    // when args isn't set;
			return -1;
		}
	}

	public String getArg( int i )
	{
		return args[i];
	}

	public String[] getArgs()
	{
		return args;
	}

	public void sendMessage( Player p, String msg )
	{
		if( p != null )
		{
			if( this.isParentSet() )
				p.sendMessage( ChatColor.DARK_GRAY + "[" + this.getParent().getDescription().getName() + "] " + msg );
			else
				p.sendMessage( msg );
		}
	}

	/// send a player a message
	public commandResult err( Player p, String msg )
	{
		if( this.isParentSet() )
		{
			this.sendMessage( p, ChatColor.RED + msg );
		}
		return commandResult.RES_ERROR;
	}

	public commandResult suc()
	{
		return commandResult.RES_SUCCESS;
	}

	public commandResult suc( Player p, String msg )
	{
		if( this.isParentSet() )
		{
			this.sendMessage( p, ChatColor.GREEN + msg );
		}
		return commandResult.RES_SUCCESS;
	}

	public commandResult skip()
	{
		return commandResult.RES_SKIPPED;
	}

	public commandResult norm( Player p, String msg )
	{
		if( this.isParentSet() )
		{
			this.sendMessage( p, ChatColor.WHITE + msg );
		}
		return commandResult.RES_NORMAL;
	}

	public commandResult broadcast( String msg )
	{
		if( this.isParentSet() )
		{
			this.parent.getServer().broadcastMessage( ChatColor.DARK_GRAY + "[" + this.parent.getClass().getSimpleName() + "] " + msg );
		}
		return commandResult.RES_BROADCAST;
	}

	/**
	 * Evaluates errors and throws Exception if error was detected.
	 *
	 * @param p
	 * @throws wrongParentException
	 * @throws wrongPlayerException
	 */
	public void ev( Player p ) throws wrongParentException, wrongPlayerException
	{
		if( !this.isParentSet() )
			throw new wrongParentException( "Parent not set." );
		if( p == null || !( p instanceof Player ) )
			throw new wrongPlayerException( "Got no Player or Player instance is damaged." );
	}

	public void ev( Player p, String perm ) throws wrongParentException, wrongPlayerException, noPermissionException
	{
		ev( p );

		if( !p.hasPermission( perm ) )
			throw new noPermissionException( "No permissions." );
	}
}

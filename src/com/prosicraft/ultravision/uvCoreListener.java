/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class uvCoreListener implements Listener
{

	private ultravision core = null;

	public uvCoreListener( ultravision uv )
	{
		core = uv;
	}

	@EventHandler( priority = EventPriority.NORMAL )
	public void onPluginEnable( PluginEnableEvent e )
	{
		if( core.bridges.length > 0 )
		{
			for( int n = 0; n < core.bridges.length; n++ )
			{
				if( core.bridges[n] != null )
					if( !core.bridges[n].isConnected() )
						core.bridges[n].open( ( JavaPlugin ) e.getPlugin() );
			}
		}
	}
}

/*
 * UltraVision Core Listener
 * Handles Plugins of UltraBox to be hooked via UVBridge
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

	private ultravision plugin = null;

	public uvCoreListener( ultravision uv )
	{
		plugin = uv;
	}

	/**
	 * Handles Plugin enabled event and hooks Bridges
	 *
	 * @param e
	 */
	@EventHandler( priority = EventPriority.NORMAL )
	public void onPluginEnable( PluginEnableEvent e )
	{
		if( plugin.bridges.length <= 0 )
		{
			return;
		}

		for( int n = 0; n < plugin.bridges.length; n++ )
		{
			if( plugin.bridges[n] != null )
			{
				if( !plugin.bridges[n].isConnected() )
					plugin.bridges[n].open( ( JavaPlugin ) e.getPlugin() );
			}
		}
	}
}

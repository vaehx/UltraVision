/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision;

import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author prosicraft
 */
public class UVBRIDGE
{

	public UVBRIDGE( ultravision u, String req_name )
	{
		core = u;
		pluginname = req_name;
	}

	public UVBRIDGE()
	{
	}

	public static enum BRIDGE_OUTPUT
	{

		NO_OUTPUT,
		CONSOLE_OUTPUT
	}
	public final String ver = "v1.0";
	public UVBRIDGE.BRIDGE_OUTPUT __out = UVBRIDGE.BRIDGE_OUTPUT.CONSOLE_OUTPUT;
	public String pluginname = "No Plugin";
	public boolean connected = false;
	public JavaPlugin plugin = null;
	public ultravision core = null;

	public void log( String __txt )
	{
		if( __out == UVBRIDGE.BRIDGE_OUTPUT.CONSOLE_OUTPUT )
		{
			System.out.println( __txt );
		}
	}

	public boolean open( String req_name, ultravision c )
	{
		if( connected )
			return true;
		if( plugin != null && plugin.getName().equalsIgnoreCase( req_name ) )
		{
			core = c;
			connected = true;
			pluginname = req_name;
			log( "Hooked Bridge to '" + req_name + "'. Bridge version " + ver );
		}
		return connected;
	}

	public boolean open( JavaPlugin plug )
	{
		if( connected )
			return true;
		if( plugin == null && core != null && plug.getName().equalsIgnoreCase( pluginname ) )
		{
			plugin = plug;
			connected = true;
			log( "Hooked Bridge to '" + pluginname + "'. Bridge version " + ver );
		}
		return connected;
	}

	public void close()
	{
		connected = false;
	}

	public boolean isConnected()
	{
		return connected;
	}
}

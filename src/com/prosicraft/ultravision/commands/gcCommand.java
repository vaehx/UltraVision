/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.commands;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class gcCommand extends extendedCommand
{

	public gcCommand( ultravision uv, String[] args )
	{
		super( uv, args );
	}

	@Override
	public commandResult run( Player p )
	{

		try
		{

			// /gc

			this.ev( p );

			p.sendMessage( "Garbage collecting..." );
			Runtime.getRuntime().gc();
			
			p.sendMessage( ChatColor.DARK_AQUA + "Processors: " + ChatColor.AQUA + Runtime.getRuntime().availableProcessors() );
			p.sendMessage( ChatColor.DARK_AQUA + "Java VM: " + ChatColor.AQUA + System.getProperty( "java.vm.name" ) );
			p.sendMessage( ChatColor.DARK_AQUA + "Java Version: " + ChatColor.AQUA + System.getProperty( "java.runtime.version" ) );                        
                        
                        Iterator<MemoryPoolMXBean> iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
                        while (iter.hasNext())
                        {
                            MemoryPoolMXBean item = iter.next();
                            String name = item.getName();
                            MemoryType type = item.getType();
                            MemoryUsage usage = item.getUsage();
                            MemoryUsage peak = item.getPeakUsage();
                            MemoryUsage collections = item.getCollectionUsage();
                            
                            p.sendMessage(ChatColor.DARK_AQUA + name + "| ty:" + type.name() + " use:" + usage.getUsed() + " B");
                        }

			return suc();

		}
		catch( wrongParentException | wrongPlayerException ex )
		{
			MLog.e( "[GCCMD] " + ex.getMessage() );
			return err( p, "Failed to execute command." );
		}

	}
}

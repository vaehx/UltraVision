/*
 * UltraVision Chat Listener
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * UltraVision Chat Listener
 *
 * @author prosicraft
 */
public class UVChatListener implements Listener
{
	private ultravision parent = null;
	private MAuthorizer auth = null;
	private MConfiguration config = null;

	public UVChatListener( ultravision parent )
	{
		this.parent = parent;
		this.config = parent.getMConfig();
		auth = parent.getAuthorizer();
	}

	/**
	 * Examine a chat event. Adds (warned) or (Not logged in) prefix
	 *
	 * @param e
	 */
	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerChat( AsyncPlayerChatEvent e )
	{
		if( auth != null && !auth.loggedIn( e.getPlayer() ) )
		{
			e.setMessage( ChatColor.GRAY + "(Not logged in) " + ( parent.showMessagesNotLoggedIn ? e.getMessage() : "" ) );
		}

		if( parent.getAPI().isPlayerWarned( e.getPlayer().getName() ) )
		{
			e.setMessage( ChatColor.GRAY + "(warned) " + ( config.getBoolean( "ultravision.showWarnedMessages", true ) ? e.getMessage() : "" ) );
		}
	}
}

/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 *
 * (c) 2010-2014 prosicraft
 * All rights reserved.
 */
package com.prosicraft.ultravision.dummy;

import com.prosicraft.ultravision.util.MLog;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import net.minecraft.server.v1_7_R3.WorldServer;
import net.minecraft.util.org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author prosicraft
 */
public class DebugDummyPlayer extends CraftPlayer
{
	private DebugDummy dummyWrapper;

	public DebugDummyPlayer(CraftServer server, EntityPlayer entity)
	{
		super(server, entity);
	}

	public DebugDummyPlayer(CraftServer server, EntityPlayer entity, DebugDummy dummyWrapper)
	{
		super(server, entity);
		this.dummyWrapper = dummyWrapper;
	}

	@Override
	public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause)
	{
		EntityPlayer checkEntity = getHandle();

		// don't check living state and connection here

		if (checkEntity.vehicle != null || checkEntity.passenger != null)
			return false;

		Location from = this.getLocation();
		Location to = location;

		PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
		server.getPluginManager().callEvent(event);

		if (event.isCancelled())
			return false;

		from = event.getFrom();
		to = event.getTo();

		WorldServer fromWorld = ((CraftWorld) from.getWorld()).getHandle();
		WorldServer toWorld = ((CraftWorld) to.getWorld()).getHandle();

		// close foreign inventories to prevent further editing
		if (getHandle().activeContainer != getHandle().defaultContainer)
			getHandle().closeInventory();

		// move world if needed
		if (fromWorld == toWorld)
			checkEntity.playerConnection.teleport(to);
		else
			server.getHandle().moveToWorld(checkEntity, toWorld.dimension, true, to, true);

		return true;
	}

	@Override
	public void setScoreboard(Scoreboard scoreboard)
	{
		MLog.w("Some plugin wanted to change the Scoreboard of the uv dummy! This is not supported yet!");
	}

	@Override
	public void kickPlayer(String message)
	{
		if (getHandle().playerConnection == null)
			return;

		getHandle().playerConnection.disconnect(message == null ? "" : message);

		// also unset the debugdummy instance
		if (this.dummyWrapper != null)
			this.dummyWrapper.despawn();
	}
}

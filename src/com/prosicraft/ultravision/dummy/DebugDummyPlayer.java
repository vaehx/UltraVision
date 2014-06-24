/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 *
 * (c) 2010-2014 prosicraft
 * All rights reserved.
 */
package com.prosicraft.ultravision.dummy;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author prosicraft
 */
public class DebugDummyPlayer extends CraftPlayer
{
	public DebugDummyPlayer(CraftServer server, EntityPlayer entity)
	{
		super(server, entity);
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
}

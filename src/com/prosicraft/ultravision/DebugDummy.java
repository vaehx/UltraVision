/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 *
 * (c) 2010-2014 prosicraft
 * All rights reserved.
 */
package com.prosicraft.ultravision;

import java.util.UUID;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.PlayerInteractManager;
import net.minecraft.server.v1_7_R3.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class DebugDummy
{
	private Player dummyPlayer = null;
	private ultravision uvPlugin = null;
	private WorldServer worldServer = null;

	public DebugDummy(ultravision uvPlugin)
	{
		this.uvPlugin = uvPlugin;
	}

	public void spawn(String dummyPlayerUUIDStr, Location loc)
	{
		// retrieve the Minecraft server instance
		CraftServer craftServer = (CraftServer)uvPlugin.getServer();
		MinecraftServer minecraftServer = craftServer.getServer();

		// the world server
		worldServer = ((CraftWorld)loc.getWorld()).getHandle();

		// the players game profile
		UUID dummyPlayerUUID = UUID.fromString(dummyPlayerUUIDStr);
		GameProfile dummyGameProfile = new GameProfile(dummyPlayerUUID, "uvdummy");

		// and finally a player interact manager
		PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);

		// now create the dummy player
		EntityPlayer entityPlayer = new EntityPlayer(minecraftServer, worldServer, dummyGameProfile, playerInteractManager);
		dummyPlayer = new CraftPlayer((CraftServer)uvPlugin.getServer(), entityPlayer);
		dummyPlayer.teleport(loc);
	}

	public void despawn()
	{
		if (dummyPlayer == null)
			return;

		worldServer.removeEntity(((CraftPlayer)dummyPlayer).getHandle());
	}
}

/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 *
 * (c) 2010-2014 prosicraft
 * All rights reserved.
 */
package com.prosicraft.ultravision.dummy;

import com.prosicraft.ultravision.ultravision;
import com.prosicraft.ultravision.util.MLog;
import java.util.UUID;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EnumGamemode;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.PlayerInteractManager;
import net.minecraft.server.v1_7_R3.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_7_R3.NetworkManager;
import net.minecraft.server.v1_7_R3.PlayerConnection;

/**
 *
 * @author prosicraft
 */
public class DebugDummy
{
	private Player dummyPlayer = null;
	private ultravision uvPlugin = null;
	private WorldServer worldServer = null;

	public boolean isLiving = false;

	public DebugDummy(ultravision uvPlugin)
	{
		this.uvPlugin = uvPlugin;
	}

	public void spawn(String dummyPlayerUUIDStr, Location loc)
	{
		// retrieve the Minecraft server instance
		CraftServer craftServer = (CraftServer)Bukkit.getServer();
		MinecraftServer minecraftServer = craftServer.getServer();

		// the world server
		worldServer = minecraftServer.getWorldServer(0);

		// the players game profile
		UUID dummyPlayerUUID = UUID.fromString(dummyPlayerUUIDStr);
		GameProfile dummyGameProfile = new GameProfile(dummyPlayerUUID, "uvdummy");

		// and finally a player interact manager
		PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);

		// now create the dummy player
		EntityPlayer entityPlayer = new EntityPlayer(minecraftServer, worldServer, dummyGameProfile, playerInteractManager);

		NetworkManager networkManager = new DebugDummyNetworkManager(true);


		minecraftServer.getUserCache().a(dummyGameProfile);
		entityPlayer.spawnIn(worldServer);
		entityPlayer.playerInteractManager.a((WorldServer)entityPlayer.world);

		// START PlayerList.a
		entityPlayer.playerInteractManager.setGameMode(EnumGamemode.CREATIVE);
		entityPlayer.playerInteractManager.b(worldServer.getWorldData().getGameType());
		// END PlayerList.a
		entityPlayer.playerConnection = new PlayerConnection(minecraftServer, networkManager, entityPlayer);

		minecraftServer.az();

		minecraftServer.getPlayerList().c(entityPlayer);
		minecraftServer.getPlayerList().b(entityPlayer, worldServer);

		dummyPlayer = new DebugDummyPlayer(craftServer, entityPlayer);
		MLog.i("Spawned Dummy Player with entity id " + dummyPlayer.getEntityId() +
			" at location " + dummyPlayer.getLocation().getX() + ", " + dummyPlayer.getLocation().getX() + ", " + dummyPlayer.getLocation().getX() + "!");

		isLiving = true;
	}

	public void despawn()
	{
		if (dummyPlayer == null || worldServer == null || !isLiving)
			return;

		worldServer.removeEntity(((CraftPlayer)dummyPlayer).getHandle());
		dummyPlayer = null;
		worldServer = null;
		isLiving = false;
	}

	public Location getLocation()
	{
		if (!isLiving)
			return null;

		return dummyPlayer.getLocation();
	}

	public void teleportTo(Location loc)
	{
		if (isLiving)
			dummyPlayer.teleport(loc);
	}
}

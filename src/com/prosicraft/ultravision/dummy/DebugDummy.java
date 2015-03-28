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
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.MinecraftServer;
import net.minecraft.server.v1_8_R2.PlayerInteractManager;
import net.minecraft.server.v1_8_R2.WorldServer;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R2.EnumProtocolDirection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.CraftServer;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_8_R2.NetworkManager;
import net.minecraft.server.v1_8_R2.PlayerConnection;
import net.minecraft.server.v1_8_R2.WorldSettings;

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

		NetworkManager networkManager = new DebugDummyNetworkManager(EnumProtocolDirection.SERVERBOUND);


		minecraftServer.getUserCache().a(dummyGameProfile);
		entityPlayer.spawnIn(worldServer);
		entityPlayer.playerInteractManager.a((WorldServer)entityPlayer.world);

		// START PlayerList.a
		entityPlayer.playerInteractManager.setGameMode(WorldSettings.EnumGamemode.CREATIVE);
		entityPlayer.playerInteractManager.b(worldServer.getWorldData().getGameType());
		// END PlayerList.a
		entityPlayer.playerConnection = new PlayerConnection(minecraftServer, networkManager, entityPlayer);

		minecraftServer.aG();

		minecraftServer.getPlayerList().onPlayerJoin(entityPlayer, "UV Dummy player joined!");
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

		((CraftPlayer)dummyPlayer).disconnect("UV Dummy deleted!");
		//worldServer.removeEntity(((CraftPlayer)dummyPlayer).getHandle());
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

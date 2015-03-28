/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 *
 * (c) 2010-2014 prosicraft
 * All rights reserved.
 */
package com.prosicraft.ultravision.dummy;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.minecraft.server.v1_8_R2.EnumProtocolDirection;
import net.minecraft.server.v1_8_R2.NetworkManager;

/**
 *
 * @author prosicraft
 */
public class DebugDummyNetworkManager extends NetworkManager
{
	private SocketAddress socketAddress = null;

	public DebugDummyNetworkManager(EnumProtocolDirection protocolDirection)
	{		
		super(protocolDirection);
		socketAddress = new InetSocketAddress(25565);
	}

	@Override
	public SocketAddress getSocketAddress()
	{
		return socketAddress;
	}

	@Override
	public void k()
	{
		// we simply do nothing here		
	}
}

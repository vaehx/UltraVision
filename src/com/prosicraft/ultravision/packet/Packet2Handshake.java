/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.packet;

import java.io.PrintWriter;

/**
 *
 * @author passi
 */
public class Packet2Handshake
{
	private boolean flag = false;

	public Packet2Handshake( boolean val )
	{
		flag = val;
	}

	public void send( PrintWriter out )
	{
		out.write( 2 );
		out.flush();

		out.write( ( ( flag ) ? 1 : 0 ) );
		out.flush();
	}
}

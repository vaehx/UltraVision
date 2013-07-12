/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.packet;

import com.prosicraft.ultravision.util.MLog;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author prosicraft
 */
public class Packet1Login
{
	private static int ID = 1;
	private int myip = 0;
	private String username = "Unknown";
	private String password = "";
	//private boolean accepted = false;

	public Packet1Login()
	{
	}

	public Packet1Login( int ip, String uname, String pword )
	{
		myip = ip;
		username = uname;
		password = pword;
	}

	public boolean eval( int sID, BufferedReader s )
	{
		MLog.d( "Packet1Login got id: " + sID + " ('" + String.valueOf( new char[]
		{
			( char ) sID
		} ) );

		if( sID != ID )
			return false;

		MLog.d( "Packet1Login now checking..." );

		/*if ( (myip = s.getInt()) == 0 )
		 MLog.e("Received damaged packet with id " + String.valueOf(sID) + ": No IP!");

		 MLog.d("Got ip: " + myip); */

		char[] uname = new char[ 16 ];
		try
		{
			s.read( uname );
		}
		catch( IOException ex )
		{
			MLog.e( "IOException reading username on Packet1Login: " + ex.getMessage() );
			ex.printStackTrace( System.out );
		}
		username = String.valueOf( uname );
		if( username.trim().equals( "" ) )
			MLog.e( "Received damaged packet with id " + String.valueOf( sID ) + ": No Username!" );

		username = username.trim();

		MLog.d( "Got username: '" + username + "'" );

		char[] pword = new char[ 16 ];
		try
		{
			s.read( pword );
		}
		catch( IOException ex )
		{
			MLog.e( "IOException reading password on Packet1Login: " + ex.getMessage() );
			ex.printStackTrace( System.out );
		}
		if( ( password = String.valueOf( pword ) ).trim().equals( "" ) )
			MLog.e( "Received damaged packet with id " + String.valueOf( sID ) + ": No Password!" );

		MLog.d( "Got password: '" + password + "'" );

		return true;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}
}

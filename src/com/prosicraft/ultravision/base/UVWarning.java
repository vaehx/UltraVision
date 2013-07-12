/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.base;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class UVWarning
{

	private String reason = "Not provided";
	private String warner = null;
	private Time warnTime = null;   // Time decremented by thread
	private Time mWarnTime = null;
	private String ServerName = "Not provided";
	private boolean global = false;

	public UVWarning()
	{
	}

	public UVWarning( String reason, Player warner, boolean global, Time warnTime )
	{
		this.reason = reason;
		this.warner = warner.getName();
		this.warnTime = warnTime;
		this.mWarnTime = warnTime;
		this.ServerName = warner.getServer().getName();
		this.global = global;
	}

	public boolean isGlobal()
	{
		return this.global;
	}

	public Time getRemainingWarnTime()
	{
		if( warnTime == null )
			return null;
		return new Time( warnTime.getTime()
			- ( Calendar.getInstance().getTimeInMillis() - this.mWarnTime.getTime() ) );
	}

	public String getReason()
	{
		return reason;
	}

	public String getWarner()
	{
		return warner;
	}

	public String getFormattedInfo()
	{
		return ( ( global ) ? "globally " : "" ) + "warned by " + warner + ( ( mWarnTime != null ) ? " for " + mWarnTime.toString() : "" ) + ". Reason: " + reason;
	}

	public boolean read( DataInputStream in, UVFileInformation fi ) throws IOException
	{

		if( ( this.warner = MStream.readString( in, 16 ) ).trim().equalsIgnoreCase( "" ) )
			return false;
		this.reason = MStream.readString( in, 60 );
		if( fi.getVersion() >= 2 )
		{
			long v = in.readLong();
			this.warnTime = ( v == -1 ) ? null : new Time( v );
			this.mWarnTime = new Time( in.readLong() );
		}
		else
		{
			long v = in.read();
			this.warnTime = ( v == -1 ) ? null : new Time( v );
			this.mWarnTime = new Time( ( long ) in.read() );
		}
		this.ServerName = MStream.readString( in, 16 );
		this.global = MStream.readBool( in );

		return true;

	}

	public void write( DataOutputStream out ) throws IOException
	{

		out.write( MAuthorizer.getCharArrayB( warner, 16 ) );
		out.write( MAuthorizer.getCharArrayB( reason, 60 ) );
		out.writeLong( ( warnTime != null ) ? warnTime.getTime() : -1 );
		out.writeLong( ( mWarnTime != null ) ? mWarnTime.getTime() : 0 );
		out.write( MAuthorizer.getCharArrayB( ServerName, 16 ) );
		out.write( global ? 1 : 0 );

		out.flush();

	}

	public static void writeNull( DataOutputStream out ) throws IOException
	{

		out.write( MAuthorizer.getCharArrayB( "", 16 ) );
		out.flush();

	}
}

/*
 * UVLocalPlayer.java
 *
 * Local Player class for ultravision
 */
package com.prosicraft.ultravision.local;

import com.prosicraft.ultravision.base.PlayerIdent;
import com.prosicraft.ultravision.base.UVPlayerInfo;
import com.prosicraft.ultravision.base.UltraVisionAPI;
import com.prosicraft.ultravision.util.MConst;
import com.prosicraft.ultravision.util.MLog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Describes a local Player for ultravision Local Engine
 *
 * @author prosicraft
 */
public class UVLocalPlayer
{

	public CraftPlayer craftPlayer = null;
        public PlayerIdent playerIdent = null;
	public UVPlayerInfo i = null;
	public File logFile = null;
	private String nl = System.getProperty( "line.separator" );
	private String logpath = "";

	/**
	 * Creates a new LocalPlayer from player entity
	 *
	 * @param server the Server
	 * @param ep The player entitiy
	 * @param logp Path to logfile
	 * @param pi Ultravision Player Information
	 */
	public UVLocalPlayer( CraftServer server, EntityPlayer ep, String logp, UVPlayerInfo pi )
	{
		craftPlayer = new CraftPlayer( server, ep );
                playerIdent = new PlayerIdent();
                playerIdent.Set(craftPlayer.getUniqueId());
		logpath = logp;
		logFile = new File( logpath + UltraVisionAPI.userLogDir, playerIdent.toString() + ".log" );
		i = pi;                

		if( !logFile.exists() )
		{
			try
			{
				File theFolder = new File( logpath + UltraVisionAPI.userLogDir );
				if( !theFolder.exists() )
					theFolder.mkdirs();
				logFile.createNewFile();

				i.logOut = new PrintWriter( logFile );
			}
			catch( IOException ioex )
			{
				MLog.e( "Can't create new User file at: " + logFile.getAbsolutePath() );
				ioex.printStackTrace( System.out );
			}
		}

		try
		{
			i.logOut = new PrintWriter( new FileOutputStream( logFile, true ) );
		}
		catch( IOException ioex )
		{
			MLog.e( "Can't open User file of user '" + ep.displayName + "'" );
                        ioex.printStackTrace(System.out);
		}
	}

	/**
	 * Creates a UVlocalPlayer from Bukkit Player
	 *
	 * @param p
	 * @param logp
	 * @param pi
	 */
	public UVLocalPlayer( Player p, String logp, UVPlayerInfo pi )
	{
		if( pi == null )
		{
			MLog.w( "Got null as PlayerInfo in UVLocalPlayer::UVLocalPlayer( Player, String, UVPlayerInfo ). Instancing empty now..." );
			pi = new UVPlayerInfo();
		}
		
		craftPlayer = new CraftPlayer( ( CraftServer ) p.getServer(), ( ( CraftPlayer ) p ).getHandle() );
		playerIdent = new PlayerIdent();
                playerIdent.Set(craftPlayer.getUniqueId());
                logpath = logp;
		logFile = new File( logpath + UltraVisionAPI.userLogDir, playerIdent.toString() + ".log" );
		i = pi;                
                
		if( !logFile.exists() )
		{
			try
			{
				File theFolder = new File( logpath + UltraVisionAPI.userLogDir );
				if( !theFolder.exists() )
					theFolder.mkdirs();
				logFile.createNewFile();
			}
			catch( IOException ioex )
			{
				MLog.e( "Can't create new User file at: " + logFile.getAbsolutePath() );
				ioex.printStackTrace( System.out );
			}
		}
		try
		{
			i.logOut = new PrintWriter( new FileOutputStream( logFile, true ) );
		}
		catch( IOException ioex )
		{
			MLog.e( "Can't open User file of user '" + p.getName() + "' (" + playerIdent.toString() + ")" );
		}
	}

	/**
	 * Reopens logfile if not found, opened or given (e.g. Player re-login)
	 */
	public void reopenLog()
	{
		if( i.logOut == null )
		{
			MLog.d( "LogOut == null" );
			if( logFile != null )
			{
				MLog.d( "LogFile != null" );
				try
				{
					i.logOut = new PrintWriter( new FileOutputStream( logFile, true ) );
				}
				catch( IOException ioex )
				{
					MLog.e( "Can't open User file." );
				}
			}
		}
	}

	/**
	 * Close logfile stream
	 */
	public void quitlog()
	{
		if( i.logOut != null )
		{
			i.logOut.close();
			i.logOut = null;
		}
	}

	/**
	 * Put something into the player logfile
	 *
	 * @param txt The message
	 */
	public void log( String txt )
	{
		if( i.logOut != null )
		{
			DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
			Date date = new Date();

			if( logFile.length() > MConst._LIMIT_A * 1024 )
			{
				MLog.d( "User file reached limit" );
				logFile.delete();
				i.logOut.close();
				try
				{
					logFile.createNewFile();
				}
				catch( IOException ioex )
				{
					MLog.e( "Can't clear userlog, reached Limit though." );
				}
				try
				{
					i.logOut = new PrintWriter( logFile );
				}
				catch( IOException ioex )
				{
					MLog.e( "Can't open User file of user '" + craftPlayer.getName() + "' (" + playerIdent.toString() + ") AFTER CLEAR." );
				}
			}
			i.logOut.append( dateFormat.format( date ) + ": " + txt + nl );
			i.logOut.flush();
		}
		else
		{
			reopenLog();
			log( txt );
		}
	}

	/**
	 * Get the Craft Player Instance
	 * @return
	 */
	public CraftPlayer getCraftPlayer()
	{
		return craftPlayer;
	}
                
        public String GetName()
        {
            return craftPlayer.getName();            
        }
                
        public PlayerIdent GetIdent()
        {
            return playerIdent;
        }
}

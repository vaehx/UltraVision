/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.chat;

import com.prosicraft.ultravision.util.MAuthorizer;
import com.prosicraft.ultravision.util.MLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author prosicraft
 */
public class UVServer extends Thread
{

	//private String serverip = "wkserver.dyndns.org";
	private int port = 5100;
	private UVChatStat state = UVChatStat.STAT_BOOT;
	private ServerSocket hostSocket = null;
	private Socket s = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private boolean isRunning = false;
	private UVClientHandler[] clientHandlers = null;
	public Stack sendBuffer = null;
	private List<MCChatListener> listeners = null;

	public UVServer( String serverip, MAuthorizer auth, int numSlots )
	{
		this.sendBuffer = new Stack<>();
		listeners = new ArrayList<>();
		clientHandlers = new UVClientHandler[ numSlots ];
	}

	public synchronized void shutdown()
	{
		disconnect();
		isRunning = false;
	}

	public void disconnect()
	{
		state = UVChatStat.STAT_STOP;
	}

	public void registerListener( MCChatListener uvcl )
	{
		if( uvcl != null )
		{
			listeners.add( uvcl );
		}
	}

	public void sendMessage( String s )
	{
		// broadcast messages
		for( int i = 0; i < clientHandlers.length; i++ )
		{
			if( clientHandlers[i] != null )
			{
				MLog.d( "Checking client " + i + ": " + String.valueOf( clientHandlers[i] ) );
				clientHandlers[i].sendMessage( s );
			}
		}
	}

	public int getNextPacket()
	{
		int res = 0;
		if( ( res = getInt() ) == -1 )
		{
			MLog.d( "There is no more packet" );
		}
		return res;
	}

	public int getInt()
	{
		if( state != UVChatStat.STAT_IDLE )
			return -1;

		try
		{

			if( in.ready() )
			{

				int res = in.read();
				return res;

			}
			return -1;

		}
		catch( IOException ioex )
		{
			return -1;
		}
	}

	public String getString( int length )
	{
		if( state != UVChatStat.STAT_IDLE )
			return " [SYS] Connection to server not established.";

		try
		{

			if( in.ready() )
			{

				char[] cbuf = new char[ length ];
				in.read( cbuf );

				return new String( cbuf );

			}
			return "";

		}
		catch( IOException ioex )
		{
			return " [SYS] Error while reading String.";
		}
	}

	@Override
	public void run()
	{
		isRunning = true;
		state = UVChatStat.STAT_BOOT;
		while( isRunning )
		{
			try
			{
				UVServer.sleep( 25 );
			}
			catch( InterruptedException iex )
			{
				MLog.d( "Server interrupted." );
			}
			catch( Exception ex )
			{
				MLog.d( "Caught exception," );
				ex.printStackTrace( System.out );
			}

			switch( state )
			{
				case STAT_BOOT:
					try
					{
						hostSocket = new ServerSocket( port );
						//hostSocket.setSoTimeout(30000);

						//in = new BufferedReader ( new InputStreamReader ( s.getInputStream() ) );
						//out = new PrintWriter ( s.getOutputStream(), true );
						state = UVChatStat.STAT_IDLE;
						MLog.d( "Connected. State=" + state.toString() );
					}
					catch( IOException ioex )
					{
						System.out.println( "Can't start server at port " + String.valueOf( port ) + ": " + ioex.getMessage() );
						ioex.printStackTrace( System.out );
						state = UVChatStat.STAT_STOP;
					}
					break;
				case STAT_IDLE:
					//write
					if( !sendBuffer.empty() )
					{
						String cur = sendBuffer.pop().toString();
						MLog.d( "(UVChat) Send Data: " + cur );
						out.print( cur );
						out.flush();
					}

					//read
					try
					{

						s = hostSocket.accept();

						for( int i = 0; i < clientHandlers.length; i++ )
						{
							if( clientHandlers[i] == null )
							{
								( clientHandlers[i] = new UVClientHandler( s, clientHandlers, this ) ).start();
								MLog.d( " (UVChat) Put new client (" + s.getInetAddress().getHostAddress() + ") at #" + i );
								break;
							}
						}

					}
					catch( Exception ex )
					{
						MLog.e( "(UVChat) IOException: " + ex.getMessage() );
						ex.printStackTrace( System.out );
					}

					break;
				case STAT_STOP:
					try
					{

						if( in != null )
							in.close();
						if( out != null )
							out.close();

						if( s != null )
							s.close();

						MLog.d( "(UVChat) Stopped UVChat server." );
						isRunning = false;
					}
					catch( IOException ioex )
					{
						MLog.e( "(UVChat) Failed to disconnect: " + ioex.getMessage() );
						ioex.printStackTrace( System.out );
					}
					break;
				default:
					break;
			}

		}

		MLog.d( "Exited Server loop" );

	}

	public void raiseOnMessageEvent( String msg )
	{
		if( listeners == null || listeners.isEmpty() )
			return;
		for( MCChatListener l : listeners )
		{
			l.onMessage( msg );
		}
	}

	public void raiseOnLoginEvent( String username )
	{
		MLog.d( "(UVServer) Raised OnLogin" );
		if( listeners == null || listeners.isEmpty() )
			return;
		for( MCChatListener l : listeners )
		{
			l.onLogin( username );
		}
	}

	public void raiseOnLeaveEvent( String username )
	{
		if( listeners == null || listeners.isEmpty() )
			return;
		for( MCChatListener l : listeners )
		{
			l.onLeave( username );
		}
	}
}

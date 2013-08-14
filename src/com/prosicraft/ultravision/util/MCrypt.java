/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 *
 * @author passi
 */
public class MCrypt
{

	private static String convertToHex( byte[] data )
	{
		StringBuilder buf = new StringBuilder();

		for( int i = 0; i < data.length; i++ )
		{
			int halfbyte = ( data[i] >>> 4 ) & 0x0F;
			int two_halfs = 0;
			do
			{
				if( ( 0 <= halfbyte ) && ( halfbyte <= 9 ) )
					buf.append( ( char ) ( '0' + halfbyte ) );
				else
					buf.append( ( char ) ( 'a' + ( halfbyte - 10 ) ) );
				halfbyte = data[i] & 0x0F;
			}
			while( two_halfs++ < 1 );
		}
		return buf.toString();
	}

	public static String getHash( int iterationNb, String password, String salt )
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
			digest.reset();
			digest.update( salt.getBytes() );
			byte[] input;
			try
			{
				input = digest.digest( password.getBytes( "UTF-8" ) );
				for( int i = 0; i < iterationNb; i++ )
				{
					digest.reset();
					input = digest.digest( input );
				}
				return new String( input );
			}
			catch( UnsupportedEncodingException ex )
			{
				MLog.e( "Failure while doing crypt algorithm." );
				return "";
			}
		}
		catch( NoSuchAlgorithmException ex )
		{
			MLog.e( "Failure while doing crypt algorithm." );
			return "";
		}
	}

	public static HashMap<String, String> loadHashes( String path )
	{
		if( path.equalsIgnoreCase( "" ) )
		{
			MLog.e( "Can't load Hases from not given file" );
			return null;
		}
		File iFile = new File( path );
		FileInputStream fis;
		ObjectInputStream ois;
		if( !iFile.exists() )
		{
			MLog.e( "Can't load Hashes from not existing file: " + path );
			return null;
		}
		try
		{
			fis = new FileInputStream( iFile );
			ois = new ObjectInputStream( fis );
		}
		catch( FileNotFoundException fnfex )
		{
			MLog.e( "Can't load Hashes from not existing file: " + path );
			return null;
		}
		catch( EOFException eofex )
		{
			HashMap<String, String> tres = new HashMap<>();
			saveHashes( path, tres );
			return tres;
		}
		catch( IOException ioex )
		{
			MLog.e( "Can't create ObjectInputStream while loading Hashes from " + path );
			ioex.printStackTrace( System.out );
			return null;
		}
		Object o;
		try
		{
			o = ois.readObject();
		}
		catch( IOException ioex )
		{
			MLog.e( "Error reading Stream in file: " + path );
			return null;
		}
		catch( ClassNotFoundException cnfex )
		{
			MLog.e( "Error reading Stream in file (ClassNotFoundException): " + path );
			return null;
		}
		if( !( o instanceof HashMap ) )
		{
			MLog.e( "Invalid hash file at: " + path );
			return null;
		}
		MLog.d( "Loaded Hashmap successfully" );
		return ( HashMap<String, String> ) o;
	}

	public static void saveHashes( String path, HashMap<String, String> hashes )
	{
		if( path.equalsIgnoreCase( "" ) )
		{
			MLog.e( "Can't save Hashes to not given file." );
			return;
		}
		File iFile = new File( path );
		FileOutputStream fos;
		ObjectOutputStream oos;
		if( !iFile.exists() )
		{
			MLog.e( "Can't save Hashes to not existing file: " + path );
			return;
		}
		if( hashes == null )
			MLog.w( "Writing 'null' to hash database means clearing." );
		try
		{
			fos = new FileOutputStream( iFile );
			oos = new ObjectOutputStream( fos );
		}
		catch( FileNotFoundException fnfex )
		{
			MLog.e( "Can't save Hashes to not existing file: " + path );
			return;
		}
		catch( IOException ioex )
		{
			MLog.e( "Can't create ObjectOutputStream while saving Hashes from " + path );
			return;
		}
		try
		{
			oos.writeObject( hashes );
		}
		catch( IOException ioex )
		{
			MLog.e( "Error writing Stream in file: " + path );
			return;
		} //catch (ClassNotFoundException cnfex) { MLog.e("Error writing Stream in file (ClassNotFoundException): " + path); return; }
		MLog.d( "Saved Hashmap successfully" );
	}

	// =============== UTILITY FUNCTIONS ===============
	public static String prependZeros( String number )
	{
		String s = "000000000000" + number;
		return s.substring( s.length() - 4 );
	}

	public static byte[] intToByteArray( int value )
	{
		return new byte[]
		{
			( byte ) ( value >>> 24 ),
			( byte ) ( value >>> 16 ),
			( byte ) ( value >>> 8 ),
			( byte ) value
		};
	}

	public static int byteArrayToInt( byte[] b )
	{
		return ( b[0] << 24 )
			+ ( ( b[1] & 0xFF ) << 16 )
			+ ( ( b[2] & 0xFF ) << 8 )
			+ ( b[3] & 0xFF );
	}
}

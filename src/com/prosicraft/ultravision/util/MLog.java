package com.prosicraft.ultravision.util;

import org.bukkit.ChatColor;

public class MLog
{

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";	
	public static final String ANSI_LIGHTBLACK = "\u001B[1;30m";
	public static final String ANSI_LIGHTRED = "\u001B[1;31m";
	public static final String ANSI_LIGHTGREEN = "\u001B[1;32m";
	public static final String ANSI_LIGHTYELLOW = "\u001B[1;33m";
	public static final String ANSI_LIGHTBLUE = "\u001B[1;34m";
	public static final String ANSI_LIGHTPURPLE = "\u001B[1;35m";
	public static final String ANSI_LIGHTCYAN = "\u001B[1;36m";
	public static final String ANSI_LIGHTWHITE = "\u001B[1;37m";
	
	public static void i( String txt, ChatColor col )
	{				
		System.out.println( realAnsi( getAnsi(col) + MConst._OUT_PREFIX + "::INFO] " + txt ) + ANSI_RESET );
	}

	public static void i( String txt )
	{
		i( txt, ChatColor.WHITE );
	}

	public static void s( String txt, ChatColor col )
	{
		System.out.println( realAnsi( getAnsi(col) + MConst._OUT_PREFIX + "::SUCCESS] " + txt ) + ANSI_RESET );
	}

	public static void s( String txt )
	{
		i( txt, ChatColor.GREEN );
	}

	public static void e( String txt, ChatColor col )
	{
		System.out.println( realAnsi( getAnsi(col) + MConst._OUT_PREFIX + "::ERROR] " + txt ) + ANSI_RESET );
	}

	public static void e( String txt )
	{
		e( txt, ChatColor.RED );
	}

	public static void w( String txt, ChatColor col )
	{
		System.out.println( realAnsi( getAnsi(col) + MConst._OUT_PREFIX + "::WARNING] " + txt ) + ANSI_RESET );
	}

	public static void w( String txt )
	{
		w( txt, ChatColor.GOLD );
	}

	public static void d( String txt, ChatColor col )
	{
		if( MConst._DEBUG_ENABLED )
			System.out.println( realAnsi( getAnsi(col) + MConst._OUT_PREFIX + "::DEBUG] " + txt ) + ANSI_RESET );
	}

	public static void d( String txt )
	{
		d( txt, ChatColor.DARK_GRAY );
	}

	public static String getAnsi( ChatColor col )
	{
		if( col == ChatColor.BLACK ) return ANSI_BLACK;
		if( col == ChatColor.DARK_BLUE ) return ANSI_BLUE;
		if( col == ChatColor.DARK_GREEN ) return ANSI_GREEN;
		if( col == ChatColor.DARK_AQUA ) return ANSI_CYAN;
		if( col == ChatColor.DARK_RED ) return ANSI_RED;
		if( col == ChatColor.DARK_PURPLE ) return ANSI_PURPLE;
		if( col == ChatColor.GOLD ) return ANSI_YELLOW;
		if( col == ChatColor.GRAY ) return ANSI_WHITE;
		if( col == ChatColor.DARK_GRAY ) return ANSI_LIGHTBLACK;
		if( col == ChatColor.AQUA ) return ANSI_LIGHTCYAN;
		if( col == ChatColor.GREEN ) return ANSI_LIGHTGREEN;
		if( col == ChatColor.BLUE ) return ANSI_LIGHTBLUE;
		if( col == ChatColor.RED ) return ANSI_LIGHTRED;
		if( col == ChatColor.LIGHT_PURPLE ) return ANSI_LIGHTPURPLE;
		if( col == ChatColor.YELLOW ) return ANSI_LIGHTYELLOW;
		
		return ANSI_LIGHTWHITE;
	}
	
	public static String realAnsi( String src )
	{
		if( src == null )
			return null;
		
		String res;
		
		res = src.replaceAll( "&0", ANSI_BLACK );
		res = res.replaceAll( "&1", ANSI_BLUE );		
		res = res.replaceAll( "&2", ANSI_GREEN );
		res = res.replaceAll( "&3", ANSI_CYAN );
		res = res.replaceAll( "&4", ANSI_RED );
		res = res.replaceAll( "&5", ANSI_PURPLE );
		res = res.replaceAll( "&6", ANSI_YELLOW );		
		res = res.replaceAll( "&7", ANSI_WHITE );
		res = res.replaceAll( "&8", ANSI_LIGHTBLACK );		
		res = res.replaceAll( "&9", ANSI_LIGHTBLUE );
		res = res.replaceAll( "&a", ANSI_LIGHTGREEN );
		res = res.replaceAll( "&b", ANSI_LIGHTCYAN );
		res = res.replaceAll( "&c", ANSI_LIGHTRED );
		res = res.replaceAll( "&d", ANSI_LIGHTPURPLE );
		res = res.replaceAll( "&e", ANSI_LIGHTYELLOW );
		res = res.replaceAll( "&f", ANSI_LIGHTWHITE );
		
		res = res.replaceAll( ChatColor.BLACK + "", ANSI_BLACK );
		res = res.replaceAll( ChatColor.DARK_BLUE + "", ANSI_BLUE );		
		res = res.replaceAll( ChatColor.DARK_GREEN + "", ANSI_GREEN );
		res = res.replaceAll( ChatColor.DARK_AQUA + "", ANSI_CYAN );
		res = res.replaceAll( ChatColor.DARK_RED + "", ANSI_RED );
		res = res.replaceAll( ChatColor.DARK_PURPLE + "", ANSI_PURPLE );
		res = res.replaceAll( ChatColor.GOLD + "", ANSI_YELLOW );		
		res = res.replaceAll( ChatColor.GRAY + "", ANSI_WHITE );
		res = res.replaceAll( ChatColor.DARK_GRAY + "", ANSI_LIGHTBLACK );		
		res = res.replaceAll( ChatColor.AQUA + "", ANSI_LIGHTCYAN );
		res = res.replaceAll( ChatColor.GREEN + "", ANSI_LIGHTGREEN );
		res = res.replaceAll( ChatColor.BLUE + "", ANSI_LIGHTBLUE );
		res = res.replaceAll( ChatColor.RED + "", ANSI_LIGHTRED );
		res = res.replaceAll( ChatColor.LIGHT_PURPLE + "", ANSI_LIGHTPURPLE );
		res = res.replaceAll( ChatColor.YELLOW + "", ANSI_LIGHTYELLOW );
		res = res.replaceAll( ChatColor.WHITE + "", ANSI_LIGHTWHITE );
		
		return res;
	}
	
	public static String real( String src )
	{
		if( src == null )
			return null;
		String target = src.replaceAll( "&([0-9a-f])", "\u00A7$1" );
		target = target.replaceAll( "&m", ChatColor.MAGIC + "" );
		target = target.replaceAll( "&l", ChatColor.BOLD + "" );
		target = target.replaceAll( "&u", ChatColor.UNDERLINE + "" );
		target = target.replaceAll( "&i", ChatColor.ITALIC + "" );
		target = target.replaceAll( "&s", ChatColor.STRIKETHROUGH + "" );
		return target;
	}
}

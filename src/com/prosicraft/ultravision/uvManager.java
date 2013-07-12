/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.ultravision;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author passi
 */
public class uvManager extends uvPlayer
{

	// SPECIAL VARIABLES FOR MANAGER
	private boolean enabled = false;                        // player is a manager and stalks poor users
	private uvPlayer playerInstance = null;                 // The ultravision.java instance of the visionizers arraylist
	private List<String> targetUsers = new ArrayList<>(); // The names of the poorly stalked users

	public uvManager( Player base )
	{
		super( base );
		enabled = false;
		if( this.playerInstance != null )
			playerInstance.isManager = true;
	}

	public uvManager( Player base, List<String> targetUsers )
	{
		super( base );

		if( targetUsers.size() > 0 )
		{
			enabled = true;
			if( this.playerInstance != null )
				playerInstance.isManager = true;
			this.targetUsers = targetUsers;
		}
	}

	// FOR MANAGER
	public void setPlayerInstance( uvPlayer uP )
	{
		this.playerInstance = uP;
		if( this.playerInstance != null )
			this.playerInstance.isManager = true;
	}

	public uvPlayer getPlayerInstance()
	{
		return this.playerInstance;
	}

	public void setAsManager( List<String> targetUsers )
	{
		this.enabled = true;
		isManager = true;
		if( this.playerInstance != null )
			playerInstance.isManager = true;
		this.targetUsers = targetUsers;
	}

	public void resetAsManager()
	{
		if( enabled )
		{
			targetUsers.clear();
			enabled = false;
			if( this.playerInstance != null )
				playerInstance.isManager = false;
			playerInstance = null;
		}
	}

	public boolean addVisionTarget( String targetUser )
	{
		if( this.enabled )
		{
			if( targetUsers == null )
				targetUsers = new ArrayList<String>();
			if( !targetUsers.contains( targetUser ) )
			{
				//System.out.println("Append Target: " + targetUser);
				this.targetUsers.add( targetUser );
				return true;
			}
			else
				return false;
		}
		return false;
	}

	public List<String> getVisionTargets()
	{
		if( this.enabled )
			return targetUsers;
		else
			return null;
	}

	public String getVisionTarget( int num )
	{
		if( this.enabled && num < numVisionTargets() )
			return targetUsers.get( num );
		else
			return "";
	}

	public boolean removeVisionTarget( String name )
	{
		if( enabled && targetUsers.contains( name ) )
		{
			targetUsers.remove( name );
			if( targetUsers.size() <= 0 )
				resetAsManager();
			return true;
		}
		return false;
	}

	public int numVisionTargets()
	{
		if( enabled && targetUsers != null )
			return targetUsers.size();
		else
			return 0;
	}

	public boolean hasAsTarget( String name )
	{
		return targetUsers.contains( name );
	}

	public boolean isManager()
	{
		return enabled;
	}
}

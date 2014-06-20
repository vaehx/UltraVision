/*
 * This file is part of the UltraVision Craftbukkit Plugin by prosicraft.
 * 
 * (c) 2010-2014 prosicraft
 * All rights reserved.
 */
package com.prosicraft.ultravision.base;

import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class PlayerIdent
{
    private UUID uid;
    
    
    
    
    public PlayerIdent()
    {	    
    }
    
    public PlayerIdent(UUID initialUUID)
    {
	    uid = initialUUID;        
    }
    
    public PlayerIdent(Player p)
    {
	    uid = p.getUniqueId();
    }
    
    
    
    
    
    public boolean Equals(PlayerIdent other)
    {
        return this.uid == other.Get();
    }
    
    public boolean Equals(UUID other)
    {
        return this.uid == other;
    }
    
    public void Set(UUID to)
    {
        this.uid = to;
    }
    
    public UUID Get()
    {
        return uid;
    }
    
    @Override
    public String toString()
    {
        return uid.toString();
    }
}

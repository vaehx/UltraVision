/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prosicraft.ultravision;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author prosicraft
 */
public class LogSettings {
	public static Map<String, Class> events = new HashMap<>();
	
	public static void addEventClass(Class c) {
		if (c == null)
			return;
		
		events.put(c.getName(), c);
	}
	
	public static void initDefaultEventHandlerClasses() {		
		addEventClass(PlayerMoveEvent.class);
		addEventClass(BlockPlaceEvent.class);
		addEventClass(BlockBreakEvent.class);
		addEventClass(PlayerDropItemEvent.class);
		addEventClass(EntityDamageByEntityEvent.class);
	}
}

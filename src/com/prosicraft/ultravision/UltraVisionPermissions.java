/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prosicraft.ultravision;

import com.prosicraft.ultravision.util.MLog;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 *
 * @author prosicraft
 */
public class UltraVisionPermissions {
	
	public class Permission {
		public String permissionNode = "";
		public String description = "???";
		
		public Permission(String node, String desc) {
			permissionNode = node;
			description = desc;
		}
	}
	
	public enum PermissionType {
		ePERM_MAINCMD,
		ePERM_LOGEVENTHANDLERS,				
		ePERM_FIND,
		ePERM_ADMIN
	}
	
	// key: permission string, value: description
	public static Map<PermissionType, Permission> perms = new HashMap<>();
	
	public static String getPermNode(PermissionType ty) {
		Permission p = perms.get(ty);
		if (p == null) {
			MLog.w("Tried to query permission node string for " + ty.name() + " but this is not set up!");
			return "";
		}
		
		return p.permissionNode;
	}
	
	public void addPermission(PermissionType ty, String permissionNode, String desc) {				
		perms.put(ty, new Permission(permissionNode, desc));
	}
	
	public void setupPermissionsList() {
		addPermission(PermissionType.ePERM_MAINCMD, "ultravision.ultravision", "UltraVision Main Command");
		addPermission(PermissionType.ePERM_LOGEVENTHANDLERS, "ultravision.logeventhandlers", "Cmd to log event handlers");
		addPermission(PermissionType.ePERM_FIND, "ultravision.find", "Find command");
		addPermission(PermissionType.ePERM_ADMIN, "ultravision.admin", "Access to all parts of the plugin");
	}
	
	public static boolean hasPermission(Player p, PermissionType ty) {
		return p.hasPermission(getPermNode(ty)) || p.hasPermission(getPermNode(PermissionType.ePERM_ADMIN));
	}
}

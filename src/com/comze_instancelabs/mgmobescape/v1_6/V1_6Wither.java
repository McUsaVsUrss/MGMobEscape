package com.comze_instancelabs.mgmobescape.v1_6;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.v1_6_R3.Packet61WorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.comze_instancelabs.mgmobescape.AbstractWither;
import com.comze_instancelabs.mgmobescape.IArena;
import com.comze_instancelabs.mgmobescape.Main;
import com.comze_instancelabs.mgmobescape.mobtools.Tools;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;

public class V1_6Wither implements AbstractWither {

	public static HashMap<String, MEWither1_6> wither1_6 = new HashMap<String, MEWither1_6>();

	public V1_6Wither(){
	}
	
	
	public void playBlockBreakParticles(final Location loc, final Material m, final Player... players) {
		@SuppressWarnings("deprecation")
		Packet61WorldEvent packet = new Packet61WorldEvent(2001, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), m.getId(), false);
		for (final Player p : players) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
	
	public static MEWither1_6 spawnWither1_6(Main m, String arena, Location t) {
		Object w = ((CraftWorld) t.getWorld()).getHandle();
		ArrayList<Vector> temp = ((IArena)MinigamesAPI.getAPI().pinstances.get(m).getArenaByName(arena)).getDragonWayPoints(arena);
		if(temp == null){
			m.getLogger().severe("You forgot to set any FlyPoints! You need to have min. 2 and one of them has to be at finish.");
			return null;
		}
		MEWither1_6 t_ = new MEWither1_6(m, arena, t, (net.minecraft.server.v1_6_R3.World) ((CraftWorld) t.getWorld()).getHandle(), temp);
		((net.minecraft.server.v1_6_R3.World) w).addEntity(t_, CreatureSpawnEvent.SpawnReason.CUSTOM);
		t_.setCustomName(m.dragon_name);
		wither1_6.put(arena, t_);
		return t_;
	}


	
	public void removeWither(String arena){
		try {
			removeWither(wither1_6.get(arena));
			wither1_6.put(arena, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop(final Main m, BukkitTask t, final String arena) {
		if (t != null) {
			t.cancel();
		}
		removeWither(wither1_6.get(arena));
	}

	
	public void removeWither(MEWither1_6 t) {
		if (t != null) {
			t.getBukkitEntity().remove();
		}
	}
	
	public Block[] getLoc(Main m, final Location l, String arena, int i, int j, Location l2){
		Block[] b = new Block[4];
		b[0] = l.getWorld().getBlockAt(new Location(l.getWorld(), wither1_6.get(arena).locX + (m.destroy_radius / 2) - i, wither1_6.get(arena).locY + j - 1, wither1_6.get(arena).locZ + 3));
		b[1] = l.getWorld().getBlockAt(new Location(l.getWorld(), wither1_6.get(arena).locX + (m.destroy_radius / 2) - i, wither1_6.get(arena).locY + j - 1, wither1_6.get(arena).locZ - 3));
		b[2] = l.getWorld().getBlockAt(new Location(l.getWorld(), wither1_6.get(arena).locX + 3, wither1_6.get(arena).locY + j - 1, wither1_6.get(arena).locZ + (m.destroy_radius / 2) - i));
		b[3] = l.getWorld().getBlockAt(new Location(l.getWorld(), wither1_6.get(arena).locX - 3, wither1_6.get(arena).locY + j - 1, wither1_6.get(arena).locZ + (m.destroy_radius / 2) - i));

		return b;
	}
	
	public static void destroyStatic(final Main m, final Location l, final Location l2, String arena, int length2){
		Tools.destroy(m, l, l2, arena, length2, "wither", true, false);
	}
	
	public void destroy(final Main m, final Location l, final Location l2, String arena, int length2){
		Tools.destroy(m, l, l2, arena, length2, "wither", true, false);
	}

}

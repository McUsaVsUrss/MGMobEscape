package com.comze_instancelabs.mgmobescape.v1_6;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.v1_6_R3.EntityTypes;
import net.minecraft.server.v1_6_R3.Packet61WorldEvent;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.comze_instancelabs.mgmobescape.AbstractDragon;
import com.comze_instancelabs.mgmobescape.IArena;
import com.comze_instancelabs.mgmobescape.Main;
import com.comze_instancelabs.mgmobescape.mobtools.Tools;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;

public class V1_6Dragon implements AbstractDragon {

	public static HashMap<String, MEDragon1_6> dragons1_6 = new HashMap<String, MEDragon1_6>();

	public V1_6Dragon(){
	}
	
	public static boolean registerEntities() {
		/*try {
			Method a = EntityTypes.class.getDeclaredMethod("a", new Class<?>[] { Class.class, String.class, int.class });
			a.setAccessible(true);
			a.invoke(a, Slimey.class, "Slimey", 55);
		} catch (Exception ex) {
		}*/

		try {
			Method a = EntityTypes.class.getDeclaredMethod("a", new Class<?>[] { Class.class, String.class, int.class });
			a.setAccessible(true);
			a.invoke(a, MEWither1_6.class, "MEWither1_6", 64);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		try {
			Method a = EntityTypes.class.getDeclaredMethod("a", new Class<?>[] { Class.class, String.class, int.class });
			a.setAccessible(true);
			a.invoke(a, MEDragon1_6.class, "MEDragon1_6", 63);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	
	public final void playBlockBreakParticles(final Location loc, final Material m, final Player... players) {
		@SuppressWarnings("deprecation")
		Packet61WorldEvent packet = new Packet61WorldEvent(2001, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), m.getId(), false);
		for (final Player p : players) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
	
	public static MEDragon1_6 spawnEnderdragon1_6(Main m, String arena, Location t) {
		Object w = ((CraftWorld) t.getWorld()).getHandle();
		ArrayList<Vector> temp = ((IArena)MinigamesAPI.getAPI().pinstances.get(m).getArenaByName(arena)).getDragonWayPoints(arena);
		if(temp == null){
			m.getLogger().severe("You forgot to set any FlyPoints! You need to have min. 2 and one of them has to be at finish.");
			return null;
		}
		MEDragon1_6 t_ = new MEDragon1_6(m, arena, t, (net.minecraft.server.v1_6_R3.World) ((CraftWorld) t.getWorld()).getHandle(), temp);
		((net.minecraft.server.v1_6_R3.World) w).addEntity(t_, CreatureSpawnEvent.SpawnReason.CUSTOM);
		t_.setCustomName(m.dragon_name);

		return t_;
	}

	
	
	
	public void removeEnderdragon(String arena){
		try {
			removeEnderdragon(dragons1_6.get(arena));
			dragons1_6.put(arena, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop(final Main m, BukkitTask t, final String arena) {
		Tools t_ = new Tools();
		t_.stop(m, t, arena, true, false, "dragon");
	}
	
	public void removeEnderdragon(MEDragon1_6 t) {
		if (t != null) {
			t.getBukkitEntity().remove();
		}
	}
	
	public Block[] getLoc(Main m, final Location l, String arena, int i, int j, Location l2){
		Block[] b = new Block[4];
		b[0] = l.getWorld().getBlockAt(new Location(l.getWorld(), dragons1_6.get(arena).locX + (m.destroy_radius / 2) - i, dragons1_6.get(arena).locY + j - 1, dragons1_6.get(arena).locZ + 3));
		b[1] = l.getWorld().getBlockAt(new Location(l.getWorld(), dragons1_6.get(arena).locX + (m.destroy_radius / 2) - i, dragons1_6.get(arena).locY + j - 1, dragons1_6.get(arena).locZ - 3));
		b[2] = l.getWorld().getBlockAt(new Location(l.getWorld(), dragons1_6.get(arena).locX + 3, dragons1_6.get(arena).locY + j - 1, dragons1_6.get(arena).locZ + (m.destroy_radius / 2) - i));
		b[3] = l.getWorld().getBlockAt(new Location(l.getWorld(), dragons1_6.get(arena).locX - 3, dragons1_6.get(arena).locY + j - 1, dragons1_6.get(arena).locZ + (m.destroy_radius / 2) - i));

		return b;
	}
	
	public static void destroyStatic(final Main m, final Location l, final Location l2, String arena, int length2){
		Tools.destroy(m, l, l2, arena, length2, "dragon", true, false);
	}
	
	public void destroy(final Main m, final Location l, final Location l2, String arena, int length2){
		Tools.destroy(m, l, l2, arena, length2, "dragon", true, false);
	}

}

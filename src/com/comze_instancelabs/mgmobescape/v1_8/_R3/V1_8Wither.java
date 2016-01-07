package com.comze_instancelabs.mgmobescape.v1_8._R3;

import com.comze_instancelabs.mgmobescape.AbstractWither;
import com.comze_instancelabs.mgmobescape.IArena;
import com.comze_instancelabs.mgmobescape.Main;
import com.comze_instancelabs.mgmobescape.mobtools.Tools;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class V1_8Wither implements AbstractWither {

    public static HashMap<String, MEWither> wither = new HashMap<String, MEWither>();

    public static MEWither spawnWither(Main m, String arena, Location t) {
        /*
		 * if(dragons.containsKey(arena)){ return wither.get(arena); }
		 */
        m.getLogger().info("WITHER SPAWNED " + arena + " " + t.toString());
        Object w = ((CraftWorld) t.getWorld()).getHandle();
        ArrayList<Vector> temp = ((IArena) MinigamesAPI.getAPI().pinstances.get(m).getArenaByName(arena)).getDragonWayPoints(arena);
        if (temp == null || temp.isEmpty()) {
            m.getLogger().severe("You forgot to set any FlyPoints! You need to have min. 2 and one of them has to be at finish.");
            return null;
        }
        MEWither t_ = new MEWither(m, arena, t, ((CraftWorld) t.getWorld()).getHandle(), temp);
        ((net.minecraft.server.v1_8_R3.World) w).addEntity(t_, CreatureSpawnEvent.SpawnReason.CUSTOM);
        t_.setCustomName(m.dragon_name);
        wither.put(arena, t_);
        t_.move(0.1D, 0.1D, 0.1D);
        return t_;
    }

    public static void destroyStatic(final Main m, final Location l, final Location l2, String arena, int length2) {
        Tools.destroy(m, l, l2, arena, length2, "wither", false, true);
    }

    public void playBlockBreakParticles(final Location loc, final Material m, final Player... players) {
        @SuppressWarnings("deprecation")
        PacketPlayOutWorldEvent packet = new PacketPlayOutWorldEvent(2001, new BlockPosition(loc.getX(), loc.getY(), loc.getZ()), m.getId(), false);
        for (final Player p : players) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void removeWither(String arena) {
        try {
            removeWither(wither.get(arena));
            wither.put(arena, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Block[] getLoc(Main m, final Location l, String arena, int i, int j, Location l2) {
        Block[] b = new Block[4];
        b[0] = l.getWorld().getBlockAt(new Location(l.getWorld(), wither.get(arena).locX + (m.destroy_radius / 2) - i, wither.get(arena).locY + j - 1, wither.get(arena).locZ + 3));
        b[1] = l.getWorld().getBlockAt(new Location(l.getWorld(), wither.get(arena).locX + (m.destroy_radius / 2) - i, wither.get(arena).locY + j - 1, wither.get(arena).locZ - 3));
        b[2] = l.getWorld().getBlockAt(new Location(l.getWorld(), wither.get(arena).locX + 3, wither.get(arena).locY + j - 1, wither.get(arena).locZ + (m.destroy_radius / 2) - i));
        b[3] = l.getWorld().getBlockAt(new Location(l.getWorld(), wither.get(arena).locX - 3, wither.get(arena).locY + j - 1, wither.get(arena).locZ + (m.destroy_radius / 2) - i));

        return b;
    }

    public void stop(final Main m, BukkitTask t, final String arena) {
        if (t != null) {
            t.cancel();
        }
        removeWither(wither.get(arena));
    }

    public void removeWither(MEWither t) {
        if (t != null) {
            t.getBukkitEntity().remove();
        }
    }

    public void destroy(final Main m, final Location l, final Location l2, String arena, int length2) {
        Tools.destroy(m, l, l2, arena, length2, "wither", false, true);
    }

}

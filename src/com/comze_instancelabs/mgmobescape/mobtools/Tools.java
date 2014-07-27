package com.comze_instancelabs.mgmobescape.mobtools;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.comze_instancelabs.mgmobescape.AbstractDragon;
import com.comze_instancelabs.mgmobescape.AbstractWither;
import com.comze_instancelabs.mgmobescape.Main;
import com.comze_instancelabs.mgmobescape.v1_6.V1_6Dragon;
import com.comze_instancelabs.mgmobescape.v1_6.V1_6Wither;
import com.comze_instancelabs.mgmobescape.v1_7.V1_7Dragon;
import com.comze_instancelabs.mgmobescape.v1_7.V1_7Wither;
import com.comze_instancelabs.mgmobescape.v1_7._R2.V1_7_5Dragon;
import com.comze_instancelabs.mgmobescape.v1_7._R2.V1_7_5Wither;
import com.comze_instancelabs.mgmobescape.v1_7._R3.V1_7_8Dragon;
import com.comze_instancelabs.mgmobescape.v1_7._R3.V1_7_8Wither;
import com.comze_instancelabs.mgmobescape.v1_7._R4.V1_7_10Dragon;
import com.comze_instancelabs.mgmobescape.v1_7._R4.V1_7_10Wither;

public class Tools {

	// the boolean parameters in this function are not used anymore
	public void stop(final Main m, BukkitTask t, final String arena, boolean mode1_6, boolean mode1_7_5, final String type) {

		if(t != null){
			t.cancel();
		}

		Bukkit.getScheduler().runTaskLater(m, new Runnable(){
			public void run(){
				if (type.equalsIgnoreCase("dragon")) {
					if (m.mode1_6) {
						V1_6Dragon v = new V1_6Dragon();
						v.removeEnderdragon(arena);
					} else if (m.mode1_7_5) {
						V1_7_5Dragon v = new V1_7_5Dragon();
						v.removeEnderdragon(arena);
					} else if (m.mode1_7_8) {
						V1_7_8Dragon v = new V1_7_8Dragon();
						v.removeEnderdragon(arena);
					} else if (m.mode1_7_10) {
						V1_7_10Dragon v = new V1_7_10Dragon();
						v.removeEnderdragon(arena);
					} else {
						V1_7Dragon v = new V1_7Dragon();
						v.removeEnderdragon(arena);
					}
				} else if (type.equalsIgnoreCase("wither")) {
					if (m.mode1_6) {
						V1_6Wither v = new V1_6Wither();
						v.removeWither(arena);
					} else if (m.mode1_7_5) {
						V1_7_5Wither v = new V1_7_5Wither();
						v.removeWither(arena);
					} else if (m.mode1_7_8) {
						V1_7_8Wither v = new V1_7_8Wither();
						v.removeWither(arena);
					} else if (m.mode1_7_10) {
						V1_7_10Wither v = new V1_7_10Wither();
						v.removeWither(arena);
					} else {
						V1_7Wither v = new V1_7Wither();
						v.removeWither(arena);
					}
				}
			}
		}, 10L);

	}

	// the boolean parameters in this function are not used anymore
	public static void destroy(final Main m, final Location l, final Location l2, String arena, int length2, String type, boolean mode1_6, boolean mode1_7_5) {
		// south
		for (int i = 0; i < m.destroy_radius; i++) { // length1
			for (int j = 0; j < m.destroy_radius; j++) {
				if (type.equalsIgnoreCase("dragon")) {

					AbstractDragon ad_ = null;

					if (m.mode1_6) {
						final V1_6Dragon v = new V1_6Dragon();
						ad_ = v;
					} else if (m.mode1_7_5) {
						final V1_7_5Dragon v = new V1_7_5Dragon();
						ad_ = v;
					} else if (m.mode1_7_8) {
						final V1_7_8Dragon v = new V1_7_8Dragon();
						ad_ = v;
					} else if (m.mode1_7_10) {
						final V1_7_10Dragon v = new V1_7_10Dragon();
						ad_ = v;
					} else {
						final V1_7Dragon v = new V1_7Dragon();
						ad_ = v;
					}

					final AbstractDragon ad = ad_;

					for (final Block b : ad.getLoc(m, l, arena, i, j - (m.destroy_radius / 3), l2)) {
						Bukkit.getScheduler().runTask(m, new Runnable() {
							public void run() {
								if (b.getType() != Material.AIR) {
									ad.playBlockBreakParticles(b.getLocation(), b.getType());
									if (b.getType() != Material.WATER && b.getType() != Material.LAVA && m.spawn_falling_blocks) {
										FallingBlock fb = l.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
										fb.setMetadata("vortex", new FixedMetadataValue(m, "protected"));
										fb.setVelocity(new Vector(0, 0.4, 0));
									}
									b.setType(Material.AIR);
								}
							}
						});
					}
				} else if (type.equalsIgnoreCase("wither")) {

					AbstractWither aw_ = null;

					if (m.mode1_6) {
						final V1_6Wither v = new V1_6Wither();
						aw_ = v;
					} else if (m.mode1_7_5) {
						final V1_7_5Wither v = new V1_7_5Wither();
						aw_ = v;
					} else if (m.mode1_7_8) {
						final V1_7_8Wither v = new V1_7_8Wither();
						aw_ = v;
					} else if (m.mode1_7_10) {
						final V1_7_10Wither v = new V1_7_10Wither();
						aw_ = v;
					} else {
						final V1_7Wither v = new V1_7Wither();
						aw_ = v;
					}

					final AbstractWither aw = aw_;

					for (final Block b : aw.getLoc(m, l, arena, i, j - (m.destroy_radius / 3), l2)) {
						Bukkit.getScheduler().runTask(m, new Runnable() {
							public void run() {
								if (b.getType() != Material.AIR) {
									aw.playBlockBreakParticles(b.getLocation(), b.getType());
									if (b.getType() != Material.WATER && b.getType() != Material.LAVA && m.spawn_falling_blocks) {
										FallingBlock fb = l.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
										fb.setMetadata("vortex", new FixedMetadataValue(m, "protected"));
										fb.setVelocity(new Vector(0, 0.4, 0));
									}
									b.setType(Material.AIR);
								}
							}
						});
					}

				}
			}
		}

	}
}

package com.comze_instancelabs.mgmobescape.mobtools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.comze_instancelabs.mgmobescape.AbstractDragon;
import com.comze_instancelabs.mgmobescape.AbstractMEDragon;
import com.comze_instancelabs.mgmobescape.AbstractMEWither;
import com.comze_instancelabs.mgmobescape.AbstractWither;
import com.comze_instancelabs.mgmobescape.IArena;
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
import com.comze_instancelabs.mgmobescape.v1_8._R1.V1_8Dragon;
import com.comze_instancelabs.mgmobescape.v1_8._R1.V1_8Wither;

public class Tools {

	// the boolean parameters in this function are not used anymore
	public void stop(final Main m, BukkitTask t, final String arena, final String type) {

		if (t != null) {
			t.cancel();
		}

		Bukkit.getScheduler().runTaskLater(m, new Runnable() {
			public void run() {
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
					} else if (m.mode1_8) {
						V1_8Dragon v = new V1_8Dragon();
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
					} else if (m.mode1_8) {
						V1_8Wither v = new V1_8Wither();
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
		final IArena a = (IArena) m.pli.getArenaByName(arena);
		for (int i = 0; i < m.destroy_radius; i++) { // length1
			for (int j = 0; j < m.destroy_radius; j++) {
				if (type.equalsIgnoreCase("dragon")) {
					final AbstractDragon ad = a.getDragonUtil();

					for (final Block b : ad.getLoc(m, l, arena, i, j - (m.destroy_radius / 3), l2)) {
						// Bukkit.getScheduler().runTask(m, new Runnable() {
						// public void run() {
						if (b.getType() != Material.AIR) {
							if (m.spawn_falling_blocks) {
								ad.playBlockBreakParticles(b.getLocation(), b.getType());
								if (b.getType() != Material.WATER && b.getType() != Material.LAVA && m.spawn_falling_blocks) {
									FallingBlock fb = l.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
									fb.setMetadata("1337", new FixedMetadataValue(m, "true"));
									fb.setDropItem(false);
									fb.setVelocity(new Vector(Math.random() * 0.4, 0.4, Math.random() * 0.4));
								}
							}
							a.getSmartReset().addChanged(b, b.getType().equals(Material.CHEST));
							b.setType(Material.AIR);
						}
						// }
						// });
					}
				} else if (type.equalsIgnoreCase("wither")) {
					final AbstractWither aw = a.getWitherUtil();

					for (final Block b : aw.getLoc(m, l, arena, i, j - (m.destroy_radius / 3), l2)) {
						// Bukkit.getScheduler().runTask(m, new Runnable() {
						// public void run() {
						if (b.getType() != Material.AIR) {
							if (m.spawn_falling_blocks) {
								aw.playBlockBreakParticles(b.getLocation(), b.getType());
								if (b.getType() != Material.WATER && b.getType() != Material.LAVA && m.spawn_falling_blocks) {
									FallingBlock fb = l.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
									fb.setMetadata("1337", new FixedMetadataValue(m, "true"));
									fb.setVelocity(new Vector(Math.random() * 0.4, 0.4, Math.random() * 0.4));
								}
							}
							a.getSmartReset().addChanged(b, b.getType().equals(Material.CHEST));
							b.setType(Material.AIR);
						}
						// }
						// });
					}

				}
			}
		}
	}

	public static void setYawPitchDragon(AbstractMEDragon ad, Vector start, Vector l) {
		double dx = l.getX() - start.getX();
		double dy = l.getY() - start.getY();
		double dz = l.getZ() - start.getZ();

		float yaw = 0F;
		float pitch = 0F;

		if (dx != 0) {
			if (dx < 0) {
				yaw = (float) (1.5 * Math.PI);
			} else {
				yaw = (float) (0.5 * Math.PI);
			}
			yaw = (float) yaw - (float) Math.atan(dz / dx);
		} else if (dz < 0) {
			yaw = (float) Math.PI;
		}

		double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

		pitch = (float) -Math.atan(dy / dxz);

		if (ad != null) {
			ad.setYawPitch(-yaw * 180F / (float) Math.PI - 180F, pitch * 180F / (float) Math.PI - 180F);
		}
	}

	public static void setYawPitchWither(AbstractMEWither aw, Vector start, Vector l) {
		double dx = l.getX() - start.getX();
		double dy = l.getY() - start.getY();
		double dz = l.getZ() - start.getZ();

		float yaw = 0F;
		float pitch = 0F;

		if (dx != 0) {
			if (dx < 0) {
				yaw = (float) (1.5 * Math.PI);
			} else {
				yaw = (float) (0.5 * Math.PI);
			}
			yaw = (float) yaw - (float) Math.atan(dz / dx);
		} else if (dz < 0) {
			yaw = (float) Math.PI;
		}

		double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

		pitch = (float) -Math.atan(dy / dxz);

		if (aw != null) {
			aw.setYawPitch(-yaw * 180F / (float) Math.PI, pitch * 180F / (float) Math.PI);
		}
	}

}

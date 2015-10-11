package com.comze_instancelabs.mgmobescape;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import com.comze_instancelabs.mgmobescape.v1_8._R3.V1_8Dragon;
import com.comze_instancelabs.mgmobescape.v1_8._R3.V1_8Wither;
import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaType;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.util.Util;

public class IArena extends Arena {

	public static Main m;
	PluginInstance pli;
	public String mobtype = "dragon";
	BukkitTask currenttask;

	private AbstractMEDragon dragon;
	private AbstractMEWither wither;
	AbstractDragon ad;
	AbstractWither aw;

	public Location lowbounds = null;
	public Location highbounds = null;

	public IArena(Main m, String arena_id) {
		super(m, arena_id, ArenaType.REGENERATION);
		ArenasConfig config = MinigamesAPI.getAPI().pinstances.get(m).getArenasConfig();
		if (config.getConfig().isSet("arenas." + this.getName() + ".mobtype")) {
			this.mobtype = config.getConfig().getString("arenas." + this.getName() + ".mobtype");
		} else {
			this.mobtype = "dragon";
		}
		this.m = m;
		this.pli = MinigamesAPI.getAPI().pinstances.get(m);
	}

	@Override
	public void start(boolean tp) {
		this.lowbounds = Util.getComponentForArena(m, this.getName(), "bounds.low");
		this.highbounds = Util.getComponentForArena(m, this.getName(), "bounds.high");
		super.start(tp);
	}

	@Override
	public void started() {
		final String arena = this.getName();
		final IArena a = this;
		if (getDragonSpawn() == null || getDragonSpawn().getWorld() == null) {
			Util.saveComponentForArena(m, arena, "mobspawn", a.getSpawns().get(0).clone().add(0D, 3D, 0D));
		}
		if (mobtype.equalsIgnoreCase("dragon")) {
				ad = new V1_8Dragon();
				setDragon(V1_8Dragon.spawnEnderdragon(m, arena, a.getDragonSpawn()));
		} else {
				aw = new V1_8Wither();
				setWither(V1_8Wither.spawnWither(m, arena, a.getDragonSpawn()));
		}

		if (mobtype.equalsIgnoreCase("dragon")) {
			final AbstractDragon ad = this.ad;

			final Location l1 = Util.getComponentForArena(m, arena, "bounds.low");
			final Location l2 = Util.getComponentForArena(m, arena, "bounds.high");

			int length1 = l1.getBlockX() - l2.getBlockX();
			final int length2 = l1.getBlockY() - l2.getBlockY();
			int length3 = l1.getBlockZ() - l2.getBlockZ();
			boolean f = false;
			boolean f_ = false;
			if (l2.getBlockX() > l1.getBlockX()) {
				length1 = l2.getBlockX() - l1.getBlockX();
				f = true;
			}

			if (l2.getBlockZ() > l1.getBlockZ()) {
				length3 = l2.getBlockZ() - l1.getBlockZ();
				f_ = true;
			}

			// currenttask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(m, new Runnable() {
			currenttask = Bukkit.getServer().getScheduler().runTaskTimer(m, new Runnable() {
				@Override
				public void run() {
					if (getDragon() != null) {
						Vector v = getDragon().getNextPosition();
						if (v != null) {
							try {
								getDragon().setPosition(v.getX(), v.getY(), v.getZ());
							} catch (Exception e) {
								;
							}
						}

						ad.destroy(m, l1, l2, arena, length2);

						m.scoreboard.updateScoreboard(a);
					}
				}
			}, 3 + 20, 3);

		} else {
			AbstractWither aw_ = this.aw;
			/*
			 * if (m.mode1_6) { aw_ = new V1_6Wither(); } else if (m.mode1_7_5) { aw_ = new V1_7_5Wither(); } else if (m.mode1_7_8) { aw_ = new
			 * V1_7_8Wither(); } else if (m.mode1_7_10) { aw_ = new V1_7_10Wither(); } else { aw_ = new V1_7Wither(); }
			 */

			final AbstractWither aw = aw_;

			final Location l1 = Util.getComponentForArena(m, arena, "bounds.low");
			final Location l2 = Util.getComponentForArena(m, arena, "bounds.high");

			if (l1 != null && l2 != null) {
				int length1 = l1.getBlockX() - l2.getBlockX();
				final int length2 = l1.getBlockY() - l2.getBlockY();
				int length3 = l1.getBlockZ() - l2.getBlockZ();
				boolean f = false;
				boolean f_ = false;
				if (l2.getBlockX() > l1.getBlockX()) {
					length1 = l2.getBlockX() - l1.getBlockX();
					f = true;
				}

				if (l2.getBlockZ() > l1.getBlockZ()) {
					length3 = l2.getBlockZ() - l1.getBlockZ();
					f_ = true;
				}

				// currenttask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(m, new Runnable() {
				currenttask = Bukkit.getServer().getScheduler().runTaskTimer(m, new Runnable() {
					@Override
					public void run() {
						if (getWither() != null) {
							Vector v = getWither().getNextPosition();
							if (v != null) {
								getWither().setPosition(v.getX(), v.getY(), v.getZ());
							}

							aw.destroy(m, l1, l2, arena, length2);
						}
					}
				}, 3 + 20, 3);
			} else {
				System.out.println("You didn't set boundaries, thus the wither/dragon won't move nor destroy anything. Please correct your setup.");
			}

		}
	}

	@Override
	public void stop() {
		this.stop(currenttask, this.getName());
		setDragon(null);
		setWither(null);
		super.stop();
	}

	public void stop(BukkitTask t, final String arena) {
		if (t != null) {
			t.cancel();
		}
		try {
			if (mobtype.equalsIgnoreCase("dragon")) {
				ad.stop(m, t, arena);
			} else if (mobtype.equalsIgnoreCase("wither")) {
				aw.stop(m, t, arena);
			} else {
				ad.stop(m, t, arena);
			}
		} catch (Exception e) {
			;
		}

	}

	@Override
	public void leavePlayer(String playername, boolean fullLeave, boolean arg2) {
		if (!pli.global_lost.containsKey(playername)) {
			if (!m.p_used_kit.contains(playername)) {
				pli.getArenaAchievements().setAchievementDone(playername, "no_used_kit", false);
			} else {
				m.p_used_kit.remove(playername);
			}
		}
		super.leavePlayer(playername, fullLeave, arg2);
	}

	public Location getDragonSpawn() {
		return Util.getComponentForArena(m, this.getName(), "mobspawn");
	}

	public ArrayList<Vector> getDragonWayPoints(String arena) {
		ArrayList<Vector> ret = new ArrayList<Vector>();
		for (Location l : m.getAllPoints(m, arena)) {
			ret.add(new Vector(l.getX(), l.getY(), l.getZ()));
		}
		return ret;
	}

	public Location getLastDragonWaypointLoc(String arena) {
		return m.getAllPoints(m, arena).get(m.getAllPoints(m, arena).size() - 1);
	}

	public AbstractDragon getDragonUtil() {
		return ad;
	}

	public AbstractWither getWitherUtil() {
		return aw;
	}

	public AbstractMEDragon getDragon() {
		return dragon;
	}

	public void setDragon(AbstractMEDragon dragon) {
		this.dragon = dragon;
	}

	public AbstractMEWither getWither() {
		return wither;
	}

	public void setWither(AbstractMEWither wither) {
		this.wither = wither;
	}
}

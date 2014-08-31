package com.comze_instancelabs.mgmobescape;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.util.ArenaScoreboard;

public class IArenaScoreboard extends ArenaScoreboard {

	HashMap<String, Scoreboard> ascore = new HashMap<String, Scoreboard>();
	HashMap<String, Objective> aobjective = new HashMap<String, Objective>();

	HashMap<String, Integer> currentscore = new HashMap<String, Integer>();

	JavaPlugin plugin = null;
	PluginInstance pli;

	public IArenaScoreboard(JavaPlugin plugin) {
		this.plugin = plugin;
		pli = MinigamesAPI.getAPI().pinstances.get(plugin);
	}

	public void updateScoreboard(final IArena arena) {
		try {
			Location finish = arena.getLastDragonWaypointLoc(arena.getName());
			for (String p_ : arena.getAllPlayers()) {
				if (!ascore.containsKey(arena.getName())) {
					ascore.put(arena.getName(), Bukkit.getScoreboardManager().getNewScoreboard());
				}
				if (!aobjective.containsKey(arena.getName())) {
					aobjective.put(arena.getName(), ascore.get(arena.getName()).registerNewObjective(arena.getName(), "dummy"));
				}

				aobjective.get(arena.getName()).setDisplaySlot(DisplaySlot.SIDEBAR);

				aobjective.get(arena.getName()).setDisplayName("[" + arena.getName() + "]");

				for (String pl_ : arena.getAllPlayers()) {
					Player p = Bukkit.getPlayer(pl_);
					if (!pli.global_lost.containsKey(pl_)) {
						int score = -(int) p.getLocation().distance(finish);
						if (currentscore.containsKey(pl_)) {
							int oldscore = currentscore.get(pl_);
							if (score > oldscore) {
								currentscore.put(pl_, score);
							} else {
								score = oldscore;
							}
						} else {
							currentscore.put(pl_, score);
						}
						try {
							if (pl_.length() < 15) {
								aobjective.get(arena.getName()).getScore(Bukkit.getOfflinePlayer("§a" + pl_)).setScore(score);
							} else {
								aobjective.get(arena.getName()).getScore(Bukkit.getOfflinePlayer("§a" + pl_.substring(0, pl_.length() - 3))).setScore(score);
							}
						} catch (Exception e) {
						}
					} else if (pli.global_lost.containsKey(pl_)) {
						if (currentscore.containsKey(pl_)) {
							int score = currentscore.get(pl_);
							try {
								if (pl_.length() < 15) {
									ascore.get(arena.getName()).resetScores("§a" + pl_);
									aobjective.get(arena.getName()).getScore("§c" + pl_).setScore(score);
								} else {
									ascore.get(arena.getName()).resetScores(Bukkit.getOfflinePlayer("§a" + pl_.substring(0, p_.length() - 3)));
									aobjective.get(arena.getName()).getScore(Bukkit.getOfflinePlayer("§c" + pl_.substring(0, p_.length() - 3))).setScore(score);
								}
							} catch (Exception e) {
							}
						}
					}
				}

				Bukkit.getPlayer(p_).setScoreboard(ascore.get(arena.getName()));
			}
		} catch (Exception e) {
			System.out.println("Failed setting Scoreboard: " + e.getMessage());
		}
	}

	@Override
	public void updateScoreboard(JavaPlugin plugin, final Arena arena) {
		IArena a = (IArena) MinigamesAPI.getAPI().pinstances.get(plugin).getArenaByName(arena.getName());
		this.updateScoreboard(a);
	}

	@Override
	public void removeScoreboard(String arena, Player p) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard sc = manager.getNewScoreboard();
		sc.clearSlot(DisplaySlot.SIDEBAR);
		p.setScoreboard(sc);
		if (currentscore.containsKey(p.getName())) {
			currentscore.remove(p.getName());
		}
		if (ascore.containsKey(arena)) {
			ascore.remove(arena);
		}
		if (aobjective.containsKey(arena)) {
			aobjective.remove(arena);
		}
	}
}

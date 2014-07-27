package com.comze_instancelabs.mgmobescape;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comze_instancelabs.mgmobescape.v1_6.V1_6Dragon;
import com.comze_instancelabs.mgmobescape.v1_7.V1_7Dragon;
import com.comze_instancelabs.mgmobescape.v1_7._R2.V1_7_5Dragon;
import com.comze_instancelabs.mgmobescape.v1_7._R3.V1_7_8Dragon;
import com.comze_instancelabs.mgmobescape.v1_7._R4.V1_7_10Dragon;
import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaSetup;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.config.DefaultConfig;
import com.comze_instancelabs.minigamesapi.config.MessagesConfig;
import com.comze_instancelabs.minigamesapi.config.StatsConfig;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class Main extends JavaPlugin implements Listener {

	// allow selecting team
	// colorbomb
	// map voting?

	MinigamesAPI api = null;
	PluginInstance pli = null;
	static Main m = null;
	ICommandHandler cmdhandler = new ICommandHandler();

	public int destroy_radius = 10;
	public String dragon_name = "Dragon";
	public boolean spawn_falling_blocks = true;

	public double mob_speed = 1.0;
	public static boolean mode1_6 = false;
	public static boolean mode1_7_5 = false;
	public static boolean mode1_7_8 = false;
	public static boolean mode1_7_10 = false;

	public static HashMap<String, String> pteam = new HashMap<String, String>();

	public void onEnable() {
		m = this;
		getServer().getPluginManager().registerEvents(this, this);
		String version = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);

		if (version.contains("1_6_R3")) {
			mode1_6 = true;
			getLogger().info("Turned on 1.6.4 mode.");
		} else if (version.contains("1_7_R1")) {
			// default
			getLogger().info("Turned on 1.7.2 mode.");
		} else if (version.contains("1_7_R2")) {
			mode1_7_5 = true;
			getLogger().info("Turned on 1.7.5 mode.");
		} else if (version.contains("1_7_R3")) {
			mode1_7_8 = true;
			getLogger().info("Turned on 1.7.8 mode.");
		} else if (version.contains("1_7_R4")) {
			mode1_7_10 = true;
			getLogger().info("Turned on 1.7.10 mode.");
		}
		registerEntities();

		api = MinigamesAPI.getAPI().setupAPI(this, "bowbash", IArena.class, new ArenasConfig(this), new MessagesConfig(this), new IClassesConfig(this), new StatsConfig(this, false), new DefaultConfig(this, false), false);
		PluginInstance pinstance = api.pinstances.get(this);
		pinstance.addLoadedArenas(loadArenas(this, pinstance.getArenasConfig()));
		Bukkit.getPluginManager().registerEvents(this, this);
		pinstance.arenaSetup = new IArenaSetup();
		pli = pinstance;
	}

	private boolean registerEntities() {
		if (mode1_6) {
			return V1_6Dragon.registerEntities();
		} else if (mode1_7_5) {
			return V1_7_5Dragon.registerEntities();
		} else if (mode1_7_8) {
			return V1_7_8Dragon.registerEntities();
		} else if (mode1_7_10) {
			return V1_7_10Dragon.registerEntities();
		}
		return V1_7Dragon.registerEntities();
	}

	public static ArrayList<Arena> loadArenas(JavaPlugin plugin, ArenasConfig cf) {
		ArrayList<Arena> ret = new ArrayList<Arena>();
		FileConfiguration config = cf.getConfig();
		if (!config.isSet("arenas")) {
			return ret;
		}
		for (String arena : config.getConfigurationSection("arenas.").getKeys(false)) {
			if (Validator.isArenaValid(plugin, arena, cf.getConfig())) {
				ret.add(initArena(arena));
			}
		}
		return ret;
	}

	public static IArena initArena(String arena) {
		IArena a = new IArena(m, arena, "dragon"); //TODO allow wither too
		ArenaSetup s = MinigamesAPI.getAPI().pinstances.get(m).arenaSetup;
		a.init(Util.getSignLocationFromArena(m, arena), Util.getAllSpawns(m, arena), Util.getMainLobby(m), Util.getComponentForArena(m, arena, "lobby"), s.getPlayerCount(m, arena, true), s.getPlayerCount(m, arena, false), s.getArenaVIP(m, arena));
		return a;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cmdhandler.handleArgs(this, "mobescape", "/" + cmd.getName(), sender, args);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (pli.global_players.containsKey(p.getName())) {
			IArena a = (IArena) pli.global_players.get(p.getName());
			if (a.getArenaState() == ArenaState.INGAME) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		final Player p = event.getPlayer();
		if (pli.global_players.containsKey(p.getName())) {
			IArena a = (IArena) pli.global_players.get(p.getName());
			if (a.getArenaState() == ArenaState.INGAME) {
				if (event.getBlock().getType() == Material.STAINED_GLASS) {
					byte data = event.getBlock().getData();
					p.getInventory().addItem(new ItemStack(Material.STAINED_GLASS, 1, data));
					p.updateInventory();
					event.getBlock().setType(Material.AIR);
					event.setCancelled(true);
				}
			}
		}
	}

}

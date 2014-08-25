package com.comze_instancelabs.mgmobescape;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
import com.comze_instancelabs.minigamesapi.util.Cuboid;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class Main extends JavaPlugin implements Listener {

	MinigamesAPI api = null;
	public PluginInstance pli = null;
	static Main m = null;
	ICommandHandler cmdhandler;

	// TODO add into default config

	public int destroy_radius = 10;
	public String dragon_name = "Dragon";
	public boolean spawn_falling_blocks = true;

	public double mob_speed = 1.0;
	public static boolean mode1_6 = false;
	public static boolean mode1_7_5 = false;
	public static boolean mode1_7_8 = false;
	public static boolean mode1_7_10 = false;

	public HashMap<String, Integer> ppoint = new HashMap<String, Integer>();

	public void onEnable() {
		cmdhandler = new ICommandHandler();
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

		api = MinigamesAPI.getAPI().setupAPI(this, "mobescape", IArena.class, new ArenasConfig(this), new MessagesConfig(this), new IClassesConfig(this), new StatsConfig(this, false), new DefaultConfig(this, false), false);
		PluginInstance pinstance = api.pinstances.get(this);
		pinstance.addLoadedArenas(loadArenas(this, pinstance.getArenasConfig()));
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
		IArena a = new IArena(m, arena);
		ArenaSetup s = MinigamesAPI.getAPI().pinstances.get(m).arenaSetup;
		a.init(Util.getSignLocationFromArena(m, arena), Util.getAllSpawns(m, arena), Util.getMainLobby(m), Util.getComponentForArena(m, arena, "lobby"), s.getPlayerCount(m, arena, true), s.getPlayerCount(m, arena, false), s.getArenaVIP(m, arena));
		return a;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		cmdhandler.handleArgs(this, "mobescape", "/" + cmd.getName(), sender, args);
		if (args.length > 0) {
			String action = args[0];
			if (action.equalsIgnoreCase("setmobspawn")) {
				if (args.length > 1) {
					String arena = args[1];

					if (!sender.hasPermission("mobescape.setup")) {
						sender.sendMessage(pli.getMessagesConfig().no_perm);
						return true;
					}
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (args.length > 1) {
							Util.saveComponentForArena(m, arena, "mobspawn", p.getLocation());
							sender.sendMessage(pli.getMessagesConfig().successfully_set.replaceAll("<component>", "mobspawn"));
						} else {
							sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd.getName() + " " + action + " <arena>");
						}
					}
				}
			} else if (action.equalsIgnoreCase("setflypoint")) {
				if (args.length > 1) {
					String arena = args[1];

					if (!sender.hasPermission("mobescape.setup")) {
						sender.sendMessage(pli.getMessagesConfig().no_perm);
						return true;
					}
					if (sender instanceof Player) {
						Player p = (Player) sender;
						int count = getAllPoints(m, arena).size();
						Util.saveComponentForArena(m, arena, "flypoint.f" + Integer.toString(count), p.getLocation());
						sender.sendMessage(pli.getMessagesConfig().successfully_set.replaceAll("<component>", "flypoint " + Integer.toString(count)));
					}
				} else {
					sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: " + cmd.getName() + " " + action + " <arena>");
				}
			} else if (action.equalsIgnoreCase("setmobtype")) {
				if (args.length > 2) {
					String arena = args[1];

					if (!sender.hasPermission("mobescape.setup")) {
						sender.sendMessage(pli.getMessagesConfig().no_perm);
						return true;
					}
					if (sender instanceof Player) {
						if (args[2].equalsIgnoreCase("dragon") || args[2].equalsIgnoreCase("wither")) {
							Player p = (Player) sender;
							ArenasConfig config = pli.getArenasConfig();
							config.getConfig().set("arenas." + arena + ".mobtype", args[2]);
							config.saveConfig();
							IArena a = (IArena) pli.getArenaByName(arena);
							if (a != null) {
								a.mobtype = args[2];
							}
							sender.sendMessage(pli.getMessagesConfig().successfully_set.replaceAll("<component>", "mobtype"));
						} else {
							sender.sendMessage(ChatColor.AQUA + "Mobtypes: wither, dragon");
						}
					}
				} else {
					sender.sendMessage(ChatColor.AQUA + "Mobtypes: wither, dragon");
					sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "-" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Usage: /" + cmd.getName() + " " + action + " <arena> <mobtype>");
				}
			}
		}
		return true;
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

	@EventHandler
	public void EntityChangeBlockEvent(org.bukkit.event.entity.EntityChangeBlockEvent event) {
		if (event.getEntityType() == EntityType.FALLING_BLOCK) {
			for (Arena a : MinigamesAPI.getAPI().pinstances.get(m).getArenas()) {
				if (Validator.isArenaValid(m, a)) {
					//Cuboid c = new Cuboid(Util.getComponentForArena(m, a.getName(), "bounds.low"), Util.getComponentForArena(m, a.getName(), "bounds.high"));
					//if (c.containsLocWithoutY(event.getBlock().getLocation())) {
					if(event.getEntity().hasMetadata("1337")){
						event.setCancelled(true);
					}
					//}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent event) {
		try {
			final Player p = event.getPlayer();
			if (pli.global_players.containsKey(p.getName())) {
				final IArena a = (IArena) pli.global_players.get(p.getName());
				if (!pli.global_lost.containsKey(p.getName())) {
					if (a.getArenaState() == ArenaState.INGAME) {
						if (p.getLocation().getBlockY() + 4 < a.lowbounds.getBlockY()) {
							a.spectate(p.getName());
							return;
						}

						int index = getAllPoints(m, a.getName()).size() - 1;
						if (Math.abs(p.getLocation().getBlockX() - getAllPoints(m, a.getName()).get(index).getBlockX()) < 3 && Math.abs(p.getLocation().getBlockZ() - getAllPoints(m, a.getName()).get(index).getBlockZ()) < 3 && Math.abs(p.getLocation().getBlockY() - getAllPoints(m, a.getName()).get(index).getBlockY()) < 3) {
							a.stop();
							return;
						}

						// TODO Die behind mob (experimental)
						if (!ppoint.containsKey(p.getName())) {
							ppoint.put(p.getName(), -1);
						}
						int i = ppoint.get(p.getName());

						int size = getAllPoints(m, a.getName()).size();
						if (i < size) {
							if (i > -1) {
								int defaultdelta = 5;

								if (i + 1 < size) {
									Location temp = getAllPoints(m, a.getName()).get(i + 1);

									if (Math.abs(p.getLocation().getBlockX() - temp.getBlockX()) < defaultdelta && Math.abs(p.getLocation().getBlockZ() - temp.getBlockZ()) < defaultdelta && Math.abs(p.getLocation().getBlockY() - temp.getBlockY()) < defaultdelta * 2) {
										i++;
										ppoint.put(p.getName(), i);
									}
								}
							} else {
								int defaultdelta = 5;
								Location temp = getAllPoints(m, a.getName()).get(0);
								if (Math.abs(p.getLocation().getBlockX() - temp.getBlockX()) < defaultdelta && Math.abs(p.getLocation().getBlockZ() - temp.getBlockZ()) < defaultdelta) {
									i++;
									ppoint.put(p.getName(), i);
								}
							}
						}

					}
				}
			}
		} catch (Exception e) {
			for (StackTraceElement et : e.getStackTrace()) {
				System.out.println(et);
			}
		}

	}

	public static ArrayList<Location> getAllPoints(JavaPlugin plugin, String arena) {
		FileConfiguration config = MinigamesAPI.getAPI().pinstances.get(plugin).getArenasConfig().getConfig();
		ArrayList<Location> ret = new ArrayList<Location>();
		if (!config.isSet("arenas." + arena + ".flypoint")) {
			return ret;
		}
		for (String spawn : config.getConfigurationSection("arenas." + arena + ".flypoint.").getKeys(false)) {
			ret.add(Util.getComponentForArena(plugin, arena, "flypoint." + spawn));
		}
		return ret;
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getType() == EntityType.ENDER_PEARL) {
			if (event.getEntity().getShooter() instanceof Player) {
				final Player p = (Player) event.getEntity().getShooter();
				if(pli.global_players.containsKey(p.getName())){
					p.getInventory().removeItem(new ItemStack(Material.ENDER_PEARL, 2));
					p.updateInventory();
					for (final Entity t : p.getNearbyEntities(40, 40, 40)) {
						if (t instanceof Player) {
							if (t != p && pli.global_players.containsKey(((Player) t).getName())) {
								Bukkit.getScheduler().runTaskLater(this, new Runnable() {
									public void run() {
										p.teleport(t);
									}
								}, 5L);
							}
						}
					}
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (pli.global_players.containsKey(p.getName())) {
			if (!event.hasItem()) {
				return;
			}
			if (event.getItem().getTypeId() == 258) {
				p.getInventory().removeItem(new ItemStack(Material.IRON_AXE, 2));
				p.updateInventory();
				Vector direction = p.getLocation().getDirection().multiply(1.3D);
				direction.setY(direction.getY() + 1.5);
				p.setVelocity(direction);
				// p.setVelocity(p.getVelocity().multiply(2D));
				event.setCancelled(true);
				p.getInventory().removeItem(new ItemStack(Material.IRON_AXE, 2));
				p.updateInventory();
				return;
			} else if (event.getItem().getTypeId() == 368) {
				p.getInventory().removeItem(new ItemStack(Material.ENDER_PEARL, 2));
				p.updateInventory();
				return;
			} else if (event.getItem().getTypeId() == 46) {
				p.getInventory().removeItem(new ItemStack(Material.TNT, 2));
				p.updateInventory();
				p.getLocation().getWorld().dropItemNaturally(p.getLocation().add(1, 3, 1), new ItemStack(Material.TNT));
				event.setCancelled(true);
				p.getInventory().removeItem(new ItemStack(Material.TNT, 2));
				p.updateInventory();
				return;
			}
		}

	}

	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		if (pli.global_players.containsKey(event.getPlayer().getName())) {
			if (event.getItem().getItemStack().getType() == Material.TNT) {
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 1));
				event.getItem().remove();
				event.setCancelled(true);
			}
		}
	}
}

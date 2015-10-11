package com.comze_instancelabs.mgmobescape;

import com.comze_instancelabs.minigamesapi.config.ClassesConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class IClassesConfig extends ClassesConfig {

    public IClassesConfig(JavaPlugin plugin) {
        super(plugin, true);
        this.getConfig().options().header("Used for saving classes. Default class:");
        this.getConfig().addDefault("config.kits.default.name", "Jumper");
        this.getConfig().addDefault("config.kits.default.items", "258*1");
        this.getConfig().addDefault("config.kits.default.lore", "The Jumper class.");
        this.getConfig().addDefault("config.kits.default.requires_money", false);
        this.getConfig().addDefault("config.kits.default.requires_permission", false);
        this.getConfig().addDefault("config.kits.default.money_amount", 100);
        this.getConfig().addDefault("config.kits.default.permission_node", "minigames.kits.default");

        this.getConfig().addDefault("config.kits.tnt.name", "TnT");
        this.getConfig().addDefault("config.kits.tnt.items", "46*1;258*1");
        this.getConfig().addDefault("config.kits.tnt.lore", "The TnT class.");
        this.getConfig().addDefault("config.kits.tnt.requires_money", false);
        this.getConfig().addDefault("config.kits.tnt.requires_permission", false);
        this.getConfig().addDefault("config.kits.tnt.money_amount", 100);
        this.getConfig().addDefault("config.kits.tnt.permission_node", "minigames.kits.tnt");

        this.getConfig().addDefault("config.kits.enderpearl.name", "Enderpearl");
        this.getConfig().addDefault("config.kits.enderpearl.items", "368*1;258*1");
        this.getConfig().addDefault("config.kits.enderpearl.lore", "The Enderpearl class.");
        this.getConfig().addDefault("config.kits.enderpearl.requires_money", false);
        this.getConfig().addDefault("config.kits.enderpearl.requires_permission", false);
        this.getConfig().addDefault("config.kits.enderpearl.money_amount", 100);
        this.getConfig().addDefault("config.kits.enderpearl.permission_node", "minigames.kits.enderpearl");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

}

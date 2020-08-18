package com.joiubas.prisongangs.utility;

import com.joiubas.prisongangs.PrisonGangs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/*
%prisongangs_xpneeded%
%prisongangs_gangname%
%prisongangs_level%

 */

public class ConfigManager {
    public static PrisonGangs plugin;

    public static Configuration config;

    public static File languageConfiguration;
    public static FileConfiguration langConfig;

    public static void createGang(String name, Player p) {
        List<UUID> members = new ArrayList<>();
        getConfig().set("gangs." + name + ".owner", p.getUniqueId().toString());
        members.add(p.getUniqueId());

        List<String> stringmembers = new ArrayList<>();

        for (UUID player : members) {
            stringmembers.add(player.toString());
        }

        getConfig().set("gangs." + name + ".members", stringmembers);
        ConfigManager.getConfig().set("gangs." + name + ".level", Integer.valueOf(1));
        ConfigManager.getConfig().set("gangs." + name + ".xp", Integer.valueOf(0));
        saveConfig();
        PrisonGang.initializeGang(name);
    }

    public static void loadConfig() {
        plugin = PrisonGangs.instance;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        prepareLanguageFile();
    }

    public static void prepareLanguageFile() {
        languageConfiguration = new File(plugin.getDataFolder(), "language.yml");

        if (!languageConfiguration.exists()) {
            plugin.saveResource("language.yml", false);
        }

        langConfig = new YamlConfiguration();

        try {
            langConfig.load(languageConfiguration);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        plugin.saveConfig();
    }

    public static Configuration getConfig() { return config; }

    public static FileConfiguration getLangConfig() {
        return langConfig;
    }

    public static String getStringLocation(Location loc) {
        if (loc == null) {
            return "";
        }
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() ;
    }

    public static Location getLocation(String s) {
        if (s == null || s.trim() == "") {
            return null;
        }
        final String[] parts = s.split(":");
        if (parts.length == 4) {
            World w = Bukkit.getServer().getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            return new Location(w, x, y, z);
        }
        return null;
    }
}

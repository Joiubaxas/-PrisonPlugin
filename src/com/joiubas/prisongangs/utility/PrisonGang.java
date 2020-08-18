package com.joiubas.prisongangs.utility;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.util.Platform;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PrisonGang {

    public static HashMap<String, PrisonGang> prisonGang = new HashMap<>();
    public static HashMap<UUID, PrisonGang> playerGang = new HashMap<>();
    public static HashMap<UUID, PrisonGang> invite = new HashMap<>();

    public List<UUID> gangPlayers = new ArrayList<>();

    public String name = null;
    public Location location = null;

    public UUID owner = null;

    public Integer level = null;
    public Integer earnedxp = 0;

    public void addXP (int xp) {
        if (level < ConfigManager.getConfig().getInt("settings.maxlevel")) {
            earnedxp += xp;
            int nextlevel = level + 1;
            if (earnedxp >= ConfigManager.getConfig().getInt("levels.level" + String.valueOf(nextlevel))) {
                earnedxp = 0;
                level = nextlevel;
                ConfigManager.getConfig().set("gangs." + name + ".level", String.valueOf(level));
                ConfigManager.getConfig().set("gangs." + name + ".xp", String.valueOf(earnedxp));
                ConfigManager.saveConfig();
            } else {
                ConfigManager.getConfig().set("gangs." + name + ".xp", String.valueOf(earnedxp));
                ConfigManager.saveConfig();
            }
        }
    }

    public static PrisonGang getGang(Player p) {
        return playerGang.get(p.getUniqueId());
    }

    public static Boolean isOwner(Player p) {
        if (inGang(p)) {
            if (getGang(p).owner.equals(p.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public static Boolean inGang(Player p) {
        return playerGang.containsKey(p.getUniqueId());
    }

    public static void initializeGang(String name) {
        PrisonGang pGang = new PrisonGang();

        pGang.name = name;

        pGang.level = ConfigManager.getConfig().getInt("gangs." + name + ".level");
        pGang.earnedxp = ConfigManager.getConfig().getInt("gangs." + name + ".xp");

        if (ConfigManager.getConfig().contains("gangs." + name + ".home")) {
            pGang.location = ConfigManager.getLocation(ConfigManager.getConfig().getString("gangs." + name + ".home"));
        }

        pGang.owner = UUID.fromString(ConfigManager.getConfig().getString("gangs." + name + ".owner"));

        List<?> list = ConfigManager.getConfig().getList("gangs." + name + ".members");

        List<String> lists = (List<String>) list;

        pGang.gangPlayers = new ArrayList<>();

        for (String  player : lists) {
            pGang.gangPlayers.add(UUID.fromString(player));
        }

        for (UUID player : pGang.gangPlayers) {
            playerGang.put(player, pGang);
        }

        prisonGang.put(name, pGang);
    }
}

package com.joiubas.prisongangs.utility;

import com.joiubas.prisongangs.PrisonGangs;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;


/*
%prisongangs_xpneeded%
%prisongangs_gangname%
%prisongangs_level%

 */

public class PlaceHolders extends EZPlaceholderHook {

    public PlaceHolders(PrisonGangs plugin) {
        super(plugin, "prisongangs");
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p == null) {
            return "Unable to find player. Global placeholder?";
        }

        if (identifier.equalsIgnoreCase("gangname")) {
            if (PrisonGang.playerGang.containsKey(p.getUniqueId())) {
                return PrisonGang.playerGang.get(p.getUniqueId()).name;
            } else {
                return "";
            }
        }

        if (identifier.equalsIgnoreCase("xpneeded")) {

            if (PrisonGang.inGang(p)) {

                PrisonGang pGang = PrisonGang.getGang(p);

                if (pGang.level < ConfigManager.getConfig().getInt("settings.maxlevel")) {
                    int nextlevel = pGang.level + 1;
                    int nextlevelxp = ConfigManager.getConfig().getInt("levels.level" + nextlevel);
                    return String.valueOf(nextlevelxp - pGang.earnedxp);
                } else {
                    return "";
                }
            } else {
                return "";
            }
        }

        if (identifier.equalsIgnoreCase("level")) {
            if (PrisonGang.inGang(p)) {

                return String.valueOf(PrisonGang.getGang(p).level);
            }
        }

        return null;
    }

}

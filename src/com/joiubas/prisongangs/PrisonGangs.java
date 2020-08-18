package com.joiubas.prisongangs;

import com.joiubas.prisongangs.utility.ConfigManager;
import com.joiubas.prisongangs.utility.PlaceHolders;
import com.joiubas.prisongangs.utility.PrisonGang;
import com.joiubas.prisongangs.utility.interaction.Events;
import com.joiubas.prisongangs.utility.interaction.MainCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonGangs extends JavaPlugin {

    public static PrisonGangs instance = null;

    public static Economy economy;

    @Override
    public void onEnable() {

        instance = this;

        ConfigManager.loadConfig();

        getCommand("gang").setExecutor(new MainCommand());
        getServer().getPluginManager().registerEvents(new Events(), instance);

        if (!setupEconomy()) {
            System.out.print("****************************************");
            System.out.print("PrisonGangs can't function without vault");
            System.out.print("****************************************");
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            System.out.print("****************************************");
            System.out.print("PrisonGangs placeholders were not enabled because PlaceholderAPI plugin was not found.");
            System.out.print("****************************************");
        } else {
            new PlaceHolders(this).hook();
        }

        if (ConfigManager.getConfig().contains("gangs")) {
            ConfigurationSection boards = ConfigManager.getConfig().getConfigurationSection("gangs");
            for (String board : boards.getKeys(false)) {
                PrisonGang.initializeGang(board);
            }
        }
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}

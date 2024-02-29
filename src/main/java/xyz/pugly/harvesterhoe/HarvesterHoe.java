package xyz.pugly.harvesterhoe;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class HarvesterHoe extends JavaPlugin {

    private static Economy econ = null;
    private static final boolean debug = false;
    private static HarvesterHoe instance;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Plugin startup logic
        FileHandler.init(this);
        new CaneListener(this);
        getCommand("harvesterhoe").setExecutor(new CommandHandler(this));

        this.getLogger().info("HarvesterHoe has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static HarvesterHoe get() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static boolean getDebug() {
        return debug;
    }

}

package xyz.pugly.harvesterhoe;

import com.samjakob.spigui.SpiGUI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.pugly.harvesterhoe.listeners.HoeListener;
import xyz.pugly.harvesterhoe.utils.ConfigHandler;
import xyz.pugly.harvesterhoe.utils.PlayerData;

public final class HarvesterHoe extends JavaPlugin {

    private static Economy econ = null;
    private static final boolean debug = false;
    public static SpiGUI spigui;
    private static HarvesterHoe instance;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!PlayerData.init()) {
            getLogger().severe("PlayerData failed to initialize.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        spigui = new SpiGUI(this);
        getLogger().info("SpiGUI has been enabled!");

        // Plugin startup logic
        ConfigHandler.init(this);
        new HoeListener(this);
        getCommand("harvesterhoe").setExecutor(new CommandHandler(this));

        this.getLogger().info("HarvesterHoe has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PlayerData.save();
        getLogger().info("HarvesterHoe has been disabled!");
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

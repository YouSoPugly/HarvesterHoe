package xyz.pugly.harvesterhoe;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileHandler {

    private static HarvesterHoe plugin;
    private static FileConfiguration config;
    private static int sellPrice;
    private static ConfigurationSection itemConfig;
    private static ConfigurationSection dropsConfig;
    private static ConfigurationSection upgradesConfig;
    private static Set<Drop> drops = new HashSet<>();

    public static void init(HarvesterHoe p) {
        plugin = p;
        p.saveDefaultConfig();
        reloadConfig();

        if (HarvesterHoe.getDebug()) {
            plugin.getLogger().info("sellPrice: " + sellPrice);
            plugin.getLogger().info("itemConfig: " + itemConfig);
            plugin.getLogger().info("dropsConfig: " + dropsConfig);
            plugin.getLogger().info("upgradesConfig: " + upgradesConfig);
        }
    }

    public static int getSellPrice() {
        return sellPrice;
    }

    public static ConfigurationSection getItemConfig() {
        return itemConfig;
    }

    public static ConfigurationSection getDropsConfig() {
        return dropsConfig;
    }

    public static void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        sellPrice = config.getInt("sell-price");
        itemConfig = config.getConfigurationSection("item");
        dropsConfig = config.getConfigurationSection("drops");
        upgradesConfig = config.getConfigurationSection("upgrades");

        updateDrops();
        updateUpgrades();
    }

    public static Set<Drop> getDrops() {
        return drops;
    }

    public static void updateDrops() {
        drops.clear();
        for (String id : dropsConfig.getKeys(false)) {
            ConfigurationSection dropConfig = dropsConfig.getConfigurationSection(id);
            double chance = dropConfig.getDouble("chance");
            if (dropConfig.isSet("material")) {
                String material = dropConfig.getString("material");
                drops.add(new Drop(id, chance, Material.getMaterial(material)));
                continue;
            }
            String command = dropConfig.getString("command");
            drops.add(new Drop(id, chance, command));
        }
    }

    public static void updateUpgrades() {
        Upgrade.clearUpgrades();
        for (String id : upgradesConfig.getKeys(false)) {
            ConfigurationSection upgradeConfig = upgradesConfig.getConfigurationSection(id);

            String name = upgradeConfig.getString("name");
            String description = upgradeConfig.getString("description");
            int maxLevel = upgradeConfig.getInt("max-level");
            int cost = upgradeConfig.getInt("cost");

            double chance = 1.0;
            if (upgradeConfig.isSet("chance"))
                chance = upgradeConfig.getDouble("chance");

            Upgrade up = new Upgrade(id, name, description, maxLevel, cost, chance);

            if (upgradeConfig.contains("actions")) {

                ConfigurationSection actions = upgradeConfig.getConfigurationSection("actions");

                if (actions.isSet("message"))
                    up.setMessage(actions.getString("message"));

                if (actions.isSet("eco"))
                    up.setEco(actions.getString("eco"));

                List<String> effects = actions.getStringList("effects");
                List<String> commands = actions.getStringList("commands");

                for (String effect : effects) {
                    if (HarvesterHoe.getDebug())
                        plugin.getLogger().info("Adding effect: " + effect + " to " + up.getName());
                    up.addEffect(effect);
                }

                for (String command : commands) {
                    up.addCommand(command);
                }
            }

            Upgrade.addUpgrade(up);
        }
    }

}

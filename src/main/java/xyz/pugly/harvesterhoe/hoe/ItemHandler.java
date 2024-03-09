package xyz.pugly.harvesterhoe.hoe;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.pugly.harvesterhoe.HarvesterHoe;
import xyz.pugly.harvesterhoe.utils.ConfigHandler;
import xyz.pugly.harvesterhoe.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemHandler {

    public static ItemStack getHarvesterHoe() {
        ItemStack item = new ItemStack(org.bukkit.Material.DIAMOND_HOE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Utils.colorize(ConfigHandler.getItemConfig().getString("name")));
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.setLore(getLore());
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);

        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("item: " + item);

        return item;
    }

    public static ItemStack getHarvesterHoe(Hoe hoe) {
        return getHarvesterHoe(hoe.getUpgrades());
    }

    public static ItemStack getHarvesterHoe(HashMap<Upgrade, Integer> upgrades) {
        ItemStack item = new ItemStack(org.bukkit.Material.DIAMOND_HOE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Utils.colorize(ConfigHandler.getItemConfig().getString("name")));
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.LUCK, 1, true);
        List<String> lore = getLore();

        for (Upgrade upgrade : upgrades.keySet()) {
            lore.add(Utils.colorize("&7" + upgrade.getName() + ": &a" + upgrades.get(upgrade)));
        }

        meta.setLore(lore);

        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);

        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("item: " + item);

        return item;
    }

    public static List<String> getLore() {
        ArrayList<String> lore;
        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("lore: " + ConfigHandler.getItemConfig().getStringList("lore"));
        lore = (ArrayList<String>) ConfigHandler.getItemConfig().getStringList("lore");
        lore.replaceAll(Utils::colorize);
        return lore;
    }
}

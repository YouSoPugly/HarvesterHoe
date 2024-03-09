package xyz.pugly.harvesterhoe.hoe;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.pugly.harvesterhoe.HarvesterHoe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hoe {

    HashMap<Upgrade, Integer> upgrades = new HashMap<>();

    public Hoe() {
    }

    public Hoe(HashMap<Upgrade, Integer> upgrades) {
        this.upgrades = upgrades;
    }

    public HashMap<Upgrade, Integer> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(HashMap<Upgrade, Integer> upgrades) {
        this.upgrades = upgrades;
    }

    public boolean addUpgrade(Upgrade upgrade, int level) {
        if (level < 1 || level > upgrade.getMaxLevel())
            return false;

        if (!upgrades.containsKey(upgrade)) {
            upgrades.put(upgrade, level);
            return true;
        }

        if (upgrades.get(upgrade) + level > upgrade.getMaxLevel())
            return false;

        upgrades.put(upgrade, upgrades.get(upgrade) + level);
        return true;
    }

    public boolean addUpgrade(Upgrade upgrade) {
        return addUpgrade(upgrade, 1);
    }

    public static Hoe get(ItemStack item) {
        if (item == null || item.getItemMeta() == null || !item.getItemMeta().hasLore() || !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) || !item.getItemMeta().hasEnchant(Enchantment.LUCK))
            return null;

        HashMap<Upgrade, Integer> upgrades = new HashMap<>();

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        lore.removeAll(ItemHandler.getLore());

        for (String s : lore) {
            String[] split = s.split(" ");
            if (HarvesterHoe.getDebug())
                HarvesterHoe.get().getLogger().info("Upgrades Lore: " + Arrays.toString(split));
            if (split.length <= 1) continue;
            Upgrade up = Upgrade.getUpgrade(split[0]);

            if (up == null) continue;

            int level = Integer.parseInt(split[1].replaceAll("[^0-9]", ""));
            upgrades.put(up, level);
        }

        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("Upgrades: " + upgrades);

        return new Hoe(upgrades);
    }

}

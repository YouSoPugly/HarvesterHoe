package xyz.pugly.harvesterhoe.listeners;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.pugly.harvesterhoe.HarvesterHoe;
import xyz.pugly.harvesterhoe.hoe.Drop;
import xyz.pugly.harvesterhoe.hoe.Hoe;
import xyz.pugly.harvesterhoe.hoe.ItemHandler;
import xyz.pugly.harvesterhoe.hoe.Upgrade;
import xyz.pugly.harvesterhoe.hoe.UpgradeGui;
import xyz.pugly.harvesterhoe.utils.ConfigHandler;
import xyz.pugly.harvesterhoe.utils.PlayerData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HoeListener implements org.bukkit.event.Listener {

    private final Economy econ;

    public HoeListener(HarvesterHoe plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        econ = HarvesterHoe.getEconomy();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (HarvesterHoe.getDebug())
            e.getPlayer().sendMessage("\u00a7aYou have broken: " + e.getBlock().getType());

        if (!e.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK))
            return;

        ItemStack item = e.getPlayer().getItemInHand();
        if (HarvesterHoe.getDebug()) {
            e.getPlayer().sendMessage("\u00a7aItem: " + item);
        }

        if (item == null) return;
        if (!item.getType().equals(Material.DIAMOND_HOE)) return;
        if (!item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) return;
        if (!item.getItemMeta().hasEnchant(Enchantment.LUCK)) return;

        if (HarvesterHoe.getDebug())
            e.getPlayer().sendMessage("\u00a7aYou have broken a sugar cane w/ a harvester hoe.");

        if (!e.getPlayer().hasPermission("harvesterhoe.use")) {
            e.getPlayer().sendMessage("\u00a7cYou do not have permission to use this item.");
            return;
        }

        if (!e.getBlock().getLocation().add(0, -1, 0).getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
            e.getPlayer().sendMessage("\u00a7cYou cannot break the bottom of the sugar cane.");
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);

        Map<Upgrade, Integer> upgrades = Hoe.get(item).getUpgrades();
        Set<String> upgradeIDS = upgrades.keySet().stream().map(Upgrade::getId).collect(Collectors.toSet());

        int count = breakCane(e.getBlock().getLocation());

        if (upgradeIDS.contains("radius")) {
            int radius = upgrades.get(Upgrade.getUpgrade("radius"));
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    Location l = e.getBlock().getLocation().add(x, 0, z);
                    if (l.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK) && l.clone().add(0, -1, 0).getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
                        count += breakCane(l);
                    }
                }
            }
        }

        int sellPrice = ConfigHandler.getSellPrice();

        if (HarvesterHoe.getDebug())
            e.getPlayer().sendMessage("\u00a7aYou have broken " + count + " sugar cane, sell price is: " + sellPrice);

        if (upgradeIDS.contains("multiplier")) {
            sellPrice *= upgrades.get(Upgrade.getUpgrade("multiplier")) + 1;
        }

        econ.depositPlayer(e.getPlayer(), count * sellPrice);
        PlayerData.addBalance(e.getPlayer().getUniqueId(), count);
        PlayerData.addCane(e.getPlayer().getUniqueId(), count);
        giveDrops(e.getPlayer(), count);
        applyUpgrades(e.getPlayer(), upgrades, count);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        if (e.getItem() == null) return;
        if (e.getItem().getType() != Material.DIAMOND_HOE) return;
        if (!e.getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) return;
        if (!e.getItem().getItemMeta().hasEnchant(Enchantment.LUCK)) return;

        e.setCancelled(true);

        if (!e.getPlayer().isSneaking()) return;

        UpgradeGui.open(e.getPlayer());
    }

    private int breakCane(Location l) {
        int count = 0;
        while (l.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
            l.getBlock().setType(Material.AIR, false);
            l = l.add(0, 1, 0);
            count++;
        }
        return count;
    }

    private void giveDrops(Player p, int count) {
        Set<Drop> drops = ConfigHandler.getDrops();
        for (Drop drop : drops) {
            drop.apply(p, count);
        }
    }

    private void applyUpgrades(Player p, Map<Upgrade, Integer> upgrades, int count) {
        for (Map.Entry<Upgrade, Integer> entry : upgrades.entrySet()) {
            Upgrade up = entry.getKey();
            int level = entry.getValue();
            up.apply(p, level, count);
        }
    }
}

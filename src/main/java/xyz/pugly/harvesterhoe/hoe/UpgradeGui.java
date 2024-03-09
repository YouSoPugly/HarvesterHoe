package xyz.pugly.harvesterhoe.hoe;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import xyz.pugly.harvesterhoe.HarvesterHoe;
import xyz.pugly.harvesterhoe.utils.ConfigHandler;
import xyz.pugly.harvesterhoe.utils.PlayerData;
import xyz.pugly.harvesterhoe.utils.Utils;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class UpgradeGui {

    private final static char[] menuDefault = {
      'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G', 'G',
      'G', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'G',
      'S', 'G', 'G', 'G', 'G', 'G', 'G', 'P', 'N',
    };

    private final static int upgradesPerPage = 7;

    public static void open(Player p, int page) {
        int pages = 1;
        if (Upgrade.getUpgrades().size() > upgradesPerPage)
            pages = (int) Math.ceil((double) Upgrade.getUpgrades().size() / upgradesPerPage);

        SGMenu menu = HarvesterHoe.spigui.create("Upgrades", 3);

        menu.setAutomaticPaginationEnabled(false);
        menu.setBlockDefaultInteractions(true);

        SGButton bg = new SGButton(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .data((short) 15)
                .amount(1)
                .name("&7")
                .build()
        );

        SGButton stats = new SGButton(new ItemBuilder(Material.DIAMOND_HOE)
                .name("&a&lPlayer Stats")
                .lore("&7Cane Broken: &a" + Utils.prettify(PlayerData.getCane(p.getUniqueId())),
                        "&7Shards: &b" + Utils.prettify(PlayerData.getBalance(p.getUniqueId())))
                .flag(ItemFlag.HIDE_ENCHANTS)
                .enchant(Enchantment.LUCK, 1)
                .build()
        );

        SGButton next = new SGButton(new ItemBuilder(Material.ARROW)
                .name("&aNext Page")
                .lore("&7Click to go to the next page")
                .build()
        ).withListener(event -> {open(p, page + 1);});

        SGButton prev = new SGButton(new ItemBuilder(Material.ARROW)
                .name("&aPrevious Page")
                .lore("&7Click to go to the previous page")
                .build()
        ).withListener(event -> {open(p, page - 1);});

        Queue<SGButton> upgradeBTNS = new LinkedList<>();

        for (Upgrade upgrade : Upgrade.getUpgrades()) {
            Hoe hoe = Hoe.get(p.getInventory().getItemInHand());
            Map<Upgrade, Integer> upgrades = hoe.getUpgrades();
            SGButton button = new SGButton(new ItemBuilder((upgrades.getOrDefault(upgrade, 0) == upgrade.getMaxLevel()) ? Material.BARRIER : Material.EMERALD)
                    .name("&a&l" + upgrade.getName())
                    .lore((upgrade.getDescription().equals("null") ? "&4&lNo Description" : upgrade.getDescription()),
                            "&7Level: &a" + upgrades.getOrDefault(upgrade, 0) + " / " + upgrade.getMaxLevel(),
                            "&7Cost: &b" + upgrade.getCost())
                    .build()
            ).withListener(event -> {upgradeHoe((Player) event.getWhoClicked(), upgrade);});
            upgradeBTNS.add(button);
        }

        //for (int i = 0; i < (page - 1) * upgradesPerPage; i++) upgradeBTNS.poll();

        int i = 0;
        for (char c : menuDefault) {
            if (c == 'G' || c == 'N' || c == 'P') menu.setButton(i, bg);
            if (c == 'S') menu.setButton(i, stats);
            if (c == 'U' && !upgradeBTNS.isEmpty()) menu.setButton(i, upgradeBTNS.poll());

            if (c == 'N' && page < pages) menu.setButton(i, next);
            if (c == 'P' && page > 1) menu.setButton(i, prev);

            if (c != 'U') menu.stickSlot(i);
            i++;
        }

        p.openInventory(menu.getInventory());
    }

    public static void open(Player p) {
        open(p, 1);
    }

    public static void upgradeHoe(Player p, Upgrade u) {
        if (PlayerData.getBalance(p.getUniqueId()) < u.getCost()) {
            p.sendMessage(ConfigHandler.getPrefix() + "You do not have enough shards to purchase this upgrade!");
            return;
        }

        Hoe hoe = Hoe.get(p.getInventory().getItemInHand());
        if (hoe == null) {
            p.sendMessage(ConfigHandler.getPrefix() + "You must be holding a harvester hoe to upgrade it!");
            return;
        }

        if (hoe.addUpgrade(u)) {
            PlayerData.removeBalance(p.getUniqueId(), u.getCost());
            p.sendMessage(ConfigHandler.getPrefix() + "You have successfully upgraded your harvester hoe with " + u.getName() + "!");
            p.getInventory().setItemInHand(ItemHandler.getHarvesterHoe(hoe));
            open(p);
        } else {
            p.sendMessage(ConfigHandler.getPrefix() + "You cannot get this upgrade!");
        }
    }

}

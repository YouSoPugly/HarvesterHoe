package xyz.pugly.harvesterhoe;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.pugly.harvesterhoe.hoe.ItemHandler;
import xyz.pugly.harvesterhoe.hoe.Upgrade;
import xyz.pugly.harvesterhoe.hoe.UpgradeGui;
import xyz.pugly.harvesterhoe.utils.ConfigHandler;
import xyz.pugly.harvesterhoe.utils.PlayerData;

import java.util.HashMap;

public class CommandHandler implements CommandExecutor {

    private final HarvesterHoe plugin;

    public CommandHandler(HarvesterHoe plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        switch (args[0]) {
            case "reload":
                reload(commandSender, command, label, args);
                return true;
            case "get":
            case "give":
                give(commandSender, command, label, args);
                return true;
            case "test":
                if (!commandSender.hasPermission("harvesterhoe.admin"))
                    return false;
                if (commandSender instanceof Player) {
                    Player p = (Player) commandSender;
                    HashMap<Upgrade, Integer> upgrades = new HashMap<>();
                    Upgrade.getUpgrades().forEach(upgrade -> upgrades.put(upgrade, upgrade.getMaxLevel()));
                    p.getInventory().addItem(ItemHandler.getHarvesterHoe(upgrades));
                }
                return true;
            case "save":
                if (!commandSender.hasPermission("harvesterhoe.save"))
                    return false;
                PlayerData.save();
                commandSender.sendMessage("\u00a7aThe configuration has been saved.");
                return true;
            case "debug":
                if (!commandSender.hasPermission("harvesterhoe.debug"))
                    return false;
                UpgradeGui.open((Player) commandSender, 3);
                return true;
            default:
                commandSender.sendMessage("\u00a7cUsage: /" + label + " <reload|give|save> [args]");
        }
        return true;
    }

    public void reload(CommandSender cs, Command c, String label, String[] args) {
        if (!cs.hasPermission("harvesterhoe.reload")) {
            cs.sendMessage("\u00a7cYou do not have permission to use this command.");
            return;
        }

        ConfigHandler.reloadConfig();
        cs.sendMessage("\u00a7aThe configuration has been reloaded.");
    }

    public void give(CommandSender cs, Command c, String label, String[] args) {
        if (!cs.hasPermission("harvesterhoe.give")) {
            cs.sendMessage("\u00a7cYou do not have permission to use this command.");
            return;
        }

        Player target = (Player) cs;

        if (args.length == 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                cs.sendMessage("\u00a7cThe player \u00a7e" + args[1] + " \u00a7ccould not be found.");
                return;
            }
        }

        target.getInventory().addItem(ItemHandler.getHarvesterHoe());
        cs.sendMessage("\u00a7aYou have given \u00a7e" + target.getName() + " \u00a7aa Harvester Hoe.");
        return;
    }
}

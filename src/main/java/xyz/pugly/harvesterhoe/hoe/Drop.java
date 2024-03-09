package xyz.pugly.harvesterhoe.hoe;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.pugly.harvesterhoe.HarvesterHoe;

import java.util.Random;

public class Drop {
    private String id;
    private double chance;
    private String command;
    private Material material;

    public Drop(String id, double chance, String command) {
        this.id = id;
        this.chance = chance;
        this.command = command;

        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("Added drop: " + id + " | Chance: " + chance + " | Command: " + command);
    }

    public Drop(String id, double chance, Material material) {
        this.id = id;
        this.chance = chance;
        this.material = material;

        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("Added drop: " + id + " | Chance: " + chance + " | Material: " + material);
    }

    public String getId() {
        return id;
    }

    public double getChance() {
        return chance;
    }

    public String getCommand() {
        return command;
    }

    public void apply(Player p, int count) {
        double chance = getChance() * count;
        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("Applying drop: " + id + " | Chance: " + chance);

        while (chance > 1) {
            give(p);
            chance--;
        }

        if (new Random().nextDouble() < chance) {
            give(p);
        }
    }

    private void give(Player p) {
        if (command != null) {
            sendCommand(p);
        } else {
            if (material != null)
                p.getInventory().addItem(new ItemStack(material));
        }
    }

    public void sendCommand(Player p) {
        p.getServer().dispatchCommand(p.getServer().getConsoleSender(), command.replace("%player%", p.getName()));
        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("Dispatched command: " + command.replace("%player%", p.getName()));
    }

}

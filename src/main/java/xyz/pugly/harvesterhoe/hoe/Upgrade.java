package xyz.pugly.harvesterhoe.hoe;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.pugly.harvesterhoe.HarvesterHoe;
import xyz.pugly.harvesterhoe.utils.Utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Upgrade {

    private String id;
    private String name;
    private String description;
    private int maxLevel = 1;
    private int cost;
    private double chance = 1.0;
    private Set<String> effects = new HashSet<>();
    private Set<String> commands = new HashSet<>();
    private String message;
    private String eco;

    public Upgrade(String id, String name, String description, int maxLevel, int cost) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
        this.cost = cost;
    }

    public Upgrade(String id, String name, String description, int maxLevel, int cost, double chance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
        this.cost = cost;
        this.chance = chance;

        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("Added upgrade: " + name + " | Chance: " + chance + " | Max Level: " + maxLevel + " | Cost: " + cost);
    }

    public void addEffect(String effect) {
        effects.add(effect);
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public String getId() {
        return id;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCost() {
        return cost;
    }

    public String toString() {
        return "Upgrade: " + name + " | Description: " + description + " | Max Level: " + maxLevel + " | Cost: " + cost;
    }

    public void apply(Player p, int level, int count) {
        if (HarvesterHoe.getDebug())
            HarvesterHoe.get().getLogger().info("Applying upgrade: " + name + " | Level: " + level + " | Count: " + count);

        if (new Random().nextDouble() > chance*count) {
            return;
        }

        double random = Math.random();

        if (message != null) {
            String m = fixString(count, random, level, p.getName(), message);
            p.sendMessage(Utils.colorize(m));
        }

        if (eco != null) {
            String e = fixString(count, random, level, eco);

            HarvesterHoe.getEconomy().depositPlayer(p, Double.parseDouble(e));
        }

        for (String effect : effects) {
            p.removePotionEffect(PotionEffectType.getByName(effect));
            p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect), 40, level - 1));
        }

        for (String command : commands) {
            command = fixString(count, random, level, p.getName(), command);

            if (HarvesterHoe.getDebug())
                HarvesterHoe.get().getLogger().info("Sending command: " + command);
            p.getServer().dispatchCommand(p.getServer().getConsoleSender(), command);
        }
    }

    private String fixString(int count, double random, int level, String s) {
        s = s.replace("%count%", String.valueOf(count));
        s = s.replace("%random%", String.valueOf(random));
        s = s.replace("%level%", String.valueOf(level));

        int i = 0;
        int start = -1,end;
        while (i <= s.length()-1) {

            if (s.charAt(i) == '{') {
                start = i;
            }
            if (s.charAt(i) == '}' && start != -1) {
                end = i;
                String sub = s.substring(start+1, end);
                Object d = Utils.eval(sub);
                s = s.replace(s.substring(start, end + 1), String.valueOf(d));
                i-=end-start;
            }

            i++;
        }

        i = 0;
        start = -1;
        while (i <= s.length()-1) {

            if (s.charAt(i) == '<') {
                start = i;
            }
            if (s.charAt(i) == '>' && start != -1) {
                end = i;
                String sub = s.substring(start+1, end);
                Object d = Utils.prettify(Double.parseDouble(sub));
                s = s.replace(s.substring(start, end + 1), String.valueOf(d));
                i-=end-start;
            }

            i++;
        }
        return s;
    }

    private String fixString(int count, double random, int level, String name, String s) {
        s.replace("%player%", name);
        return fixString(count, random, level, s);
    }

    private static Set<Upgrade> upgrades = new HashSet<>();

    public static void clearUpgrades() {
        upgrades.clear();
    }

    public static void addUpgrade(Upgrade upgrade) {
        upgrades.add(upgrade);
    }

    public static Set<Upgrade> getUpgrades() {
        return upgrades;
    }

    public static Upgrade getUpgrade(String s) {
        for (Upgrade upgrade : upgrades) {
            if (s.contains(Utils.colorize(upgrade.getName()))) {
                return upgrade;
            }
            if (upgrade.getId().equals(s)) {
                return upgrade;
            }
        }
        return null;
    }


}

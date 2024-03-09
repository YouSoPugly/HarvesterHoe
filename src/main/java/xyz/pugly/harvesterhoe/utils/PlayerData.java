package xyz.pugly.harvesterhoe.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import xyz.pugly.harvesterhoe.HarvesterHoe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

public class PlayerData {

    private static File ecoFile;
    private static JSONObject json;
    private static JSONParser parser = new JSONParser();
    private static HashMap<UUID, data> players = new HashMap<>();
    public static boolean init() {
        try {
            File pluginFolder = HarvesterHoe.get().getDataFolder();
            ecoFile = new File(pluginFolder, "playerdata.json");
            if (!ecoFile.exists()) {
                try {
                    ecoFile.createNewFile();
                    PrintWriter pw = new PrintWriter(ecoFile, "UTF-8");
                    pw.print("{");
                    pw.print("}");
                    pw.flush();
                    pw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(ecoFile), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskTimer(HarvesterHoe.get(), 20 * 60 * 5, 20L * 60L * ConfigHandler.getSaveTime()); // Save every 5 minutes

        return true;
    }

    public static void addBalance(UUID uuid, double amount) {
        load(uuid);
        players.get(uuid).setBalance(players.get(uuid).getBalance() + (int) amount);
    }

    public static void removeBalance(UUID uuid, double amount) {
        load(uuid);
        players.get(uuid).setBalance(players.get(uuid).getBalance() - (int) amount);
    }

    public static int getBalance(UUID uuid) {
        load(uuid);
        return players.get(uuid).getBalance();
    }

    public static void setBalance(UUID uuid, int amount) {
        load(uuid);
        players.get(uuid).setBalance(amount);
    }

    public static void setCane(UUID uuid, int amount) {
        load(uuid);
        players.get(uuid).setCane(amount);
    }

    public static void addCane(UUID uuid, int amount) {
        load(uuid);
        players.get(uuid).setCane(players.get(uuid).getCane() + amount);
    }

    public static int getCane(UUID uuid) {
        load(uuid);
        return players.get(uuid).getCane();
    }

    public static void load(UUID uuid) {
        if (players.containsKey(uuid)) {
            return;
        }

        if (json.containsKey(uuid.toString())) {
            JSONObject o = (JSONObject) json.get(uuid.toString());
            players.put(uuid, new data(uuid, ((Long) o.get("balance")).intValue(), ((Long) o.get("cane")).intValue()));
            return;
        }

        players.put(uuid, new data(uuid, 0, 0));
    }

    public static void save(UUID uuid) {
        if (players.containsKey(uuid)) {
            players.get(uuid).save();
        }
    }

    /***********************************************************/
    /***************** JSON HANDLING METHODS *******************/
    /***********************************************************/

    public static boolean save() {
        players.forEach((uuid, data) -> data.save());

        try {
            JSONObject toSave = new JSONObject();

            for (Object k : json.keySet()) {
                if (String.valueOf(k) == "null") continue;

                Object o = json.get(k);
                String key = String.valueOf(k);

                if (o instanceof String) {
                    toSave.put(k, getString(key));
                } else if (o instanceof Double) {
                    toSave.put(k, getDouble(key));
                } else if (o instanceof Integer) {
                    toSave.put(k, getInteger(key));
                } else if (o instanceof JSONObject) {
                    toSave.put(k, getObject(key));
                } else if (o instanceof JSONArray) {
                    toSave.put(k, getArray(key));
                }
            }

            TreeMap<String, Object> treeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
            treeMap.putAll(toSave);

            Gson g = new GsonBuilder().setPrettyPrinting().create();
            String prettyJsonString = g.toJson(treeMap);

            FileWriter fw = new FileWriter(ecoFile);
            fw.write(prettyJsonString);
            fw.flush();
            fw.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String getRawData(String key) {
        return json.containsKey(key) ? json.get(key).toString() : key;
    }

    public static String getString(String key) {
        return ChatColor.translateAlternateColorCodes('&', getRawData(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.valueOf(getRawData(key));
    }

    public static double getDouble(String key) {
        try {
            return Double.parseDouble(getRawData(key));
        } catch (Exception ex) { }
        return -1;
    }

    public static double getInteger(String key) {
        try {
            return Integer.parseInt(getRawData(key));
        } catch (Exception ex) { }
        return -1;
    }

    public static JSONObject getObject(String key) {
        return json.containsKey(key) ? (JSONObject) json.get(key) : new JSONObject();
    }

    public static JSONArray getArray(String key) {
        return json.containsKey(key) ? (JSONArray) json.get(key) : new JSONArray();
    }

    /***********************************************************/
    /********************** DATA CLASS *************************/
    /***********************************************************/

    private static class data {
        private UUID uuid;
        private int balance;
        private int cane;

        public data(UUID uuid, int balance, int cane) {
            this.uuid = uuid;
            this.balance = balance;
            this.cane = cane;
        }

        public UUID getUuid() {
            return uuid;
        }

        public int getBalance() {
            return balance;
        }

        public int getCane() {
            return cane;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public void setCane(int cane) {
            this.cane = cane;
        }

        public void save() {
            json.remove(uuid);

            JSONObject toSave = new JSONObject();
            toSave.put("balance", balance);
            toSave.put("cane", cane);
            json.put(uuid.toString(), toSave);
        }
    }

}

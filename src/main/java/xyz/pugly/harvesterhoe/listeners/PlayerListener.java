package xyz.pugly.harvesterhoe.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.pugly.harvesterhoe.utils.PlayerData;

public class PlayerListener implements org.bukkit.event.Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerData.load(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        PlayerData.save(e.getPlayer().getUniqueId());
    }

}

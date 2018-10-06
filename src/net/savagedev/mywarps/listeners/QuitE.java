package net.savagedev.mywarps.listeners;

import net.savagedev.mywarps.MyWarps;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitE implements Listener {
    private MyWarps plugin;

    public QuitE(MyWarps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuitE(PlayerQuitEvent e) {
        Player user = e.getPlayer();
        this.plugin.getUserManager().uncacheUser(user);
    }
}

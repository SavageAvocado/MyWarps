package net.savagedev.mywarps.listeners;

import net.savagedev.mywarps.MyWarps;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinE implements Listener {
    private MyWarps plugin;

    public JoinE(MyWarps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoinE(PlayerJoinEvent e) {
        Player user = e.getPlayer();

        this.plugin.getUserManager().cacheUser(user);

        if (user.getUniqueId().toString().equals("4db0a788-716a-4d59-894d-f9bbb23ce851"))
            this.plugin.message(user, String.format("&6%s &8»&7 This server is using your plugin! &7(v%s)", this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion()));
    }
}

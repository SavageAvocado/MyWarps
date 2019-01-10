package net.savagedev.mywarps.listeners;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.utils.CaseInsensitiveString;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandE implements Listener {
    private MyWarps plugin;

    public CommandE(MyWarps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getMessage().contains(" "))
            return;

        if (e.getMessage().split(" ")[0].length() == 0)
            return;

        String command = e.getMessage().split(" ")[0].replace("/", "");
        String warpname = e.getMessage().split(" ")[1];

        if (!command.equalsIgnoreCase("delwarp"))
            return;

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            for (String uuid : this.plugin.getConfigUtil().getConfig().getConfigurationSection("").getKeys(false)) {
                if (this.plugin.getConfigUtil().getConfig().getStringList(uuid).stream().noneMatch(warpname::equalsIgnoreCase))
                    continue;

                OfflinePlayer offlineUser = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                if (offlineUser.isOnline()) {
                    List<CaseInsensitiveString> ownedWarps = this.plugin.getUserManager().getUser(offlineUser).getOwnedWarps();
                    ownedWarps.remove(new CaseInsensitiveString(warpname));

                    this.plugin.getUserManager().saveWarps(offlineUser);
                    return;
                }

                List<CaseInsensitiveString> caseInsensitiveOwnedWarps = new ArrayList<>();
                List<String> ownedWarps = this.plugin.getConfigUtil().getConfig().getStringList(uuid);

                for (String ownedWarp : ownedWarps)
                    caseInsensitiveOwnedWarps.add(new CaseInsensitiveString(ownedWarp));

                caseInsensitiveOwnedWarps.remove(new CaseInsensitiveString(warpname));
                ownedWarps.clear();

                for (CaseInsensitiveString ownedWarp : caseInsensitiveOwnedWarps)
                    ownedWarps.add(ownedWarp.toString());

                this.plugin.getConfigUtil().getConfig().set(uuid, ownedWarps);
                this.plugin.getConfigUtil().save();
                this.plugin.getConfigUtil().reload();
                break;
            }
        });
    }
}

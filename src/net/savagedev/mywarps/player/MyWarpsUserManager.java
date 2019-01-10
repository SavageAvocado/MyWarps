package net.savagedev.mywarps.player;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.commands.state.ConfirmationManager;
import net.savagedev.mywarps.utils.CaseInsensitiveString;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class MyWarpsUserManager {
    private Map<UUID, MyWarpsUser> users;
    private MyWarps plugin;

    public MyWarpsUserManager(MyWarps plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        this.users = new HashMap<>();

        if (!this.plugin.getServer().getOnlinePlayers().isEmpty())
            for (Player user : this.plugin.getServer().getOnlinePlayers())
                this.cacheUser(user);
    }

    public void saveAllWarps() {
        if (!this.plugin.getServer().getOnlinePlayers().isEmpty())
            for (Player user : this.plugin.getServer().getOnlinePlayers())
                this.saveWarps(user);
    }

    public void saveWarps(OfflinePlayer user) {
        List<String> ownedWarps = new ArrayList<>();
        for (CaseInsensitiveString warp : this.users.get(user.getUniqueId()).getOwnedWarps())
            ownedWarps.add(warp.toString());

        this.plugin.getConfigUtil().getConfig().set(user.getUniqueId().toString(), ownedWarps);

        this.plugin.getConfigUtil().save();
        this.plugin.getConfigUtil().reload();
    }

    public void saveWarpsAsync(OfflinePlayer user) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.saveWarps(user));
    }

    public void cacheUser(Player user) {
        List<String> ownedWarps = this.plugin.getConfigUtil().getConfig().getStringList(user.getUniqueId().toString()) == null ? new ArrayList<>() : this.plugin.getConfigUtil().getConfig().getStringList(user.getUniqueId().toString());

        List<CaseInsensitiveString> newOwnedWarps = new ArrayList<>();
        for (String warp : ownedWarps)
            newOwnedWarps.add(new CaseInsensitiveString(warp));

        this.users.put(user.getUniqueId(), new MyWarpsUser(newOwnedWarps));
    }

    public void uncacheUser(Player user) {
        this.users.remove(user.getUniqueId());
    }

    public boolean isFirstWarp(Player user) {
        return !this.plugin.getConfigUtil().getConfig().getConfigurationSection("").getKeys(false).contains(user.getUniqueId().toString());
    }

    public MyWarpsUser getUser(OfflinePlayer user) {
        return this.users.get(user.getUniqueId());
    }

    public ConfirmationManager getConfirmationManager(Player user) {
        return this.users.get(user.getUniqueId()).getConfirmationManager();
    }
}

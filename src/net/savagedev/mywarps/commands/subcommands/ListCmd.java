package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.utils.CaseInsensitiveString;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class ListCmd extends SubCommand {
    public ListCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
        if (!this.validatePermissions(user)) return;

        if (args.length != 2) {
            List<CaseInsensitiveString> ownedWarps = this.getPlugin().getUserManager().getUser(user).getOwnedWarps();

            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.list.header").replace("%owner%", user.getName()));
            if (!ownedWarps.isEmpty()) {
                ArrayList<String> newOwnedWarps = new ArrayList<>();
                for (CaseInsensitiveString warp : ownedWarps)
                    newOwnedWarps.add(this.getPlugin().getConfig().getString("messages.list.format")
                            .replace("%warpname%", warp.toString())
                    );

                this.getPlugin().message(user, StringUtils.join(newOwnedWarps, ", "));
            } else
                this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.list.no-warps").replace("%owner%", user.getName()));
            return;
        }

        OfflinePlayer owner;
        if ((owner = Bukkit.getOfflinePlayer(args[1])) == null || !owner.hasPlayedBefore()) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.player-not-found").replace("%player%", args[1]));
            return;
        }

        if (owner.isOnline()) {
            List<CaseInsensitiveString> ownedWarps = this.getPlugin().getUserManager().getUser(owner).getOwnedWarps();

            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.list.header").replace("%owner%", owner.getName()));
            if (!ownedWarps.isEmpty()) {
                ArrayList<String> newOwnedWarps = new ArrayList<>();
                for (CaseInsensitiveString warp : ownedWarps)
                    newOwnedWarps.add(this.getPlugin().getConfig().getString("messages.list.format")
                            .replace("%warpname%", warp.toString())
                    );

                this.getPlugin().message(user, StringUtils.join(newOwnedWarps, ", "));
            } else
                this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.list.no-warps").replace("%owner%", owner.getName()));
            return;
        }

        List<String> ownedWarps = this.getPlugin().getConfigUtil().getConfig().getStringList(owner.getUniqueId().toString()) == null ? new ArrayList<>() : this.getPlugin().getConfigUtil().getConfig().getStringList(owner.getUniqueId().toString());

        this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.list.header").replace("%owner%", owner.getName()));
        if (!ownedWarps.isEmpty()) {
            ArrayList<String> newOwnedWarps = new ArrayList<>();
            for (String warp : ownedWarps)
                newOwnedWarps.add(this.getPlugin().getConfig().getString("messages.list.format")
                        .replace("%warpname%", warp)
                );

            this.getPlugin().message(user, StringUtils.join(newOwnedWarps, ", "));
        } else
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.list.no-warps").replace("%owner%", owner.getName()));
    }
}

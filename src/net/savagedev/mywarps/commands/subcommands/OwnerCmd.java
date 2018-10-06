package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public class OwnerCmd extends SubCommand {
    public OwnerCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
        if (!this.validatePermissions(user)) return;

        if (args.length != 2) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments").replace("%command%", "owner <warpName>"));
            return;
        }

        String owner = this.getPlugin().getConfig().getString("messages.no-owner");
        for (String uuid : this.getPlugin().getConfigUtil().getConfig().getConfigurationSection("").getKeys(false)) {
            if (this.getPlugin().getConfigUtil().getConfig().getStringList(uuid).stream().anyMatch(args[1]::equalsIgnoreCase)) {
                owner = this.getPlugin().getConfig().getString("messages.owner").replace("%owner%", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                break;
            }
        }

        this.getPlugin().message(user, owner);
    }
}

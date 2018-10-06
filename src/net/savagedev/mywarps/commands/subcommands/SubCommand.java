package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public abstract class SubCommand implements ISubCommand {
    private Permission permission;
    private MyWarps plugin;

    public SubCommand(MyWarps plugin, Permission permission) {
        this.permission = permission;
        this.plugin = plugin;
    }

    boolean validatePermissions(Player user) {
        if (!user.hasPermission(this.getPermission())) {
            this.plugin.message(user, this.plugin.getConfig().getString("messages.no-permission"));
            return false;
        }

        return true;
    }

    private Permission getPermission() {
        return this.permission;
    }

    public MyWarps getPlugin() {
        return this.plugin;
    }
}

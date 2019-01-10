package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class RenewCmd extends SubCommand implements Confirmable {
    public RenewCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
    }

    @Override
    public void onConfirm(Player user, String... args) {
    }
}

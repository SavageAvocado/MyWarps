package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class HelpCmd extends SubCommand {
    public HelpCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
        if (!this.validatePermissions(user)) return;

        for (String line : this.getPlugin().getConfig().getStringList("messages.help"))
            this.getPlugin().message(user, line);
    }
}

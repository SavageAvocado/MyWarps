package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.commands.state.CommandState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

public class MoveCmd extends SubCommand implements Confirmable {
    public MoveCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
        if (!this.validatePermissions(user)) return;

        if (args.length != 2) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments").replace("%command%", "move <warpName>"));
            return;
        }

        String name = args[1];

        if (this.getPlugin().getEssentialsDependency().getDependency().getWarps().getList().stream().noneMatch(args[1]::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.move.does-not-exist").replace("%warpname%", name));
            return;
        }

        List<String> ownedWarps = this.getPlugin().getConfigUtil().getConfig().getStringList(user.getUniqueId().toString()) == null ? new ArrayList<>() : this.getPlugin().getConfigUtil().getConfig().getStringList(user.getUniqueId().toString());

        if (ownedWarps.stream().noneMatch(name::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.move.not-owner"));
            return;
        }

        if (this.getPlugin().getVaultDependency().getEconomy().getBalance(user) < this.getPlugin().getConfig().getDouble("costs.move")) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.move.insufficient-funds")
                    .replace("%movecost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.move")))
            );
            return;
        }

        this.getPlugin().getUserManager().getConfirmationManager(user).setState(CommandState.AWAITING_CONFIRMATION);
        this.getPlugin().getUserManager().getConfirmationManager(user).setLocation(user.getLocation());
        this.getPlugin().getUserManager().getConfirmationManager(user).setArguments(args);
        this.getPlugin().getUserManager().getConfirmationManager(user).setCommand(this);
        this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.move.confirm")
                .replace("%warpname%", args[1])
                .replace("%movecost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.move")))
        );
    }

    @Override
    public void onConfirm(Player user, String... args) {
        Location location = this.getPlugin().getUserManager().getConfirmationManager(user).getLocation();
        String name = this.getPlugin().getUserManager().getConfirmationManager(user).getArguments()[1];

        try {
            this.getPlugin().getEssentialsDependency().getDependency().getWarps().removeWarp(name);
            this.getPlugin().getEssentialsDependency().getDependency().getWarps().setWarp(name, location);

            double cost = this.getPlugin().getConfig().getDouble("costs.move");
            this.getPlugin().getVaultDependency().performTransaction(user, cost);

            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.move.success"));
        } catch (Exception e) {
            this.getPlugin().message(user, "&cAn error occurred while moving warp... Please notify an admin.");
            e.printStackTrace();
        }
    }
}

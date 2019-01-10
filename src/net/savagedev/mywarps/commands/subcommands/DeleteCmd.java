package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.commands.state.CommandState;
import net.savagedev.mywarps.utils.CaseInsensitiveString;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

public class DeleteCmd extends SubCommand implements Confirmable {
    public DeleteCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
        if (!this.validatePermissions(user)) return;

        if (args.length != 2) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments").replace("%command%", "delete <warpName>"));
            return;
        }

        String name = args[1];

        if (this.getPlugin().getEssentialsDependency().getDependency().getWarps().getList().stream().noneMatch(args[1]::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.delete.does-not-exist").replace("%warpname%", name));
            return;
        }

        List<String> ownedWarps = this.getPlugin().getConfigUtil().getConfig().getStringList(user.getUniqueId().toString()) == null ? new ArrayList<>() : this.getPlugin().getConfigUtil().getConfig().getStringList(user.getUniqueId().toString());

        if (ownedWarps.stream().noneMatch(name::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.delete.not-owner"));
            return;
        }

        if (this.getPlugin().getVaultDependency().getEconomy().getBalance(user) < this.getPlugin().getConfig().getDouble("costs.delete")) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.delete.insufficient-funds")
                    .replace("%deletecost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.delete")))
            );
            return;
        }

        this.getPlugin().getUserManager().getConfirmationManager(user).setState(CommandState.AWAITING_CONFIRMATION);
        this.getPlugin().getUserManager().getConfirmationManager(user).setLocation(user.getLocation());
        this.getPlugin().getUserManager().getConfirmationManager(user).setArguments(args);
        this.getPlugin().getUserManager().getConfirmationManager(user).setCommand(this);
        this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.delete.confirm")
                .replace("%warpname%", args[1])
                .replace("%deletecost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.delete")))
        );
    }

    @Override
    public void onConfirm(Player user, String... args) {
        String name = this.getPlugin().getUserManager().getConfirmationManager(user).getArguments()[1];

        try {
            this.getPlugin().getEssentialsDependency().getDependency().getWarps().removeWarp(name);

            List<CaseInsensitiveString> ownedWarps = this.getPlugin().getUserManager().getUser(user).getOwnedWarps();
            ownedWarps.remove(new CaseInsensitiveString(name));

            this.getPlugin().getUserManager().saveWarpsAsync(user);

            double cost = this.getPlugin().getConfig().getDouble("costs.delete");
            this.getPlugin().getVaultDependency().performTransaction(user, cost);

            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.delete.success"));
        } catch (Exception e) {
            this.getPlugin().message(user, "&cAn error occurred while deleting warp... Please notify an admin.");
            e.printStackTrace();
        }
    }
}

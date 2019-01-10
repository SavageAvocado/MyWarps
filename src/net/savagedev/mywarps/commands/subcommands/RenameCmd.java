package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.commands.state.CommandState;
import net.savagedev.mywarps.utils.CaseInsensitiveString;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

public class RenameCmd extends SubCommand implements Confirmable {
    public RenameCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
        if (!this.validatePermissions(user)) return;

        if (args.length != 3) {
            if (args.length == 2)
                this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments").replace("%command%", "rename " + args[1] + " <newName>"));
            else
                this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments").replace("%command%", "rename <oldName> <newName>"));
            return;
        }

        String name = args[1];

        if (args[2].equalsIgnoreCase(name)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.rename.same-name").replace("%warpname%", name).replace("%newname%", args[2]));
            return;
        }

        if (this.getPlugin().getEssentialsDependency().getDependency().getWarps().getList().stream().noneMatch(name::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.rename.does-not-exist").replace("%warpname%", name));
            return;
        }

        if (this.getPlugin().getEssentialsDependency().getDependency().getWarps().getList().stream().anyMatch(args[2]::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.rename.name-unavailable").replace("%warpname%", name));
            return;
        }

        List<String> ownedWarps = this.getPlugin().getConfigUtil().getConfig().getStringList(user.getUniqueId().toString()) == null ? new ArrayList<>() : this.getPlugin().getConfigUtil().getConfig().getStringList(user.getUniqueId().toString());

        if (ownedWarps.stream().noneMatch(name::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.rename.not-owner"));
            return;
        }

        if (this.getPlugin().getVaultDependency().getEconomy().getBalance(user) < this.getPlugin().getConfig().getDouble("costs.rename")) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.rename.insufficient-funds")
                    .replace("%renamecost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.rename")))
            );
            return;
        }

        this.getPlugin().getUserManager().getConfirmationManager(user).setState(CommandState.AWAITING_CONFIRMATION);
        this.getPlugin().getUserManager().getConfirmationManager(user).setLocation(user.getLocation());
        this.getPlugin().getUserManager().getConfirmationManager(user).setArguments(args);
        this.getPlugin().getUserManager().getConfirmationManager(user).setCommand(this);
        this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.rename.confirm")
                .replace("%oldname%", args[1])
                .replace("%newname%", args[2])
                .replace("%renamecost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.rename")))
        );
    }

    @Override
    public void onConfirm(Player user, String... args) {
        String name = this.getPlugin().getUserManager().getConfirmationManager(user).getArguments()[1];
        String newName = this.getPlugin().getUserManager().getConfirmationManager(user).getArguments()[2];

        try {
            this.getPlugin().getEssentialsDependency().getDependency().getWarps().setWarp(newName, this.getPlugin().getEssentialsDependency().getDependency().getWarps().getWarp(name));
            this.getPlugin().getEssentialsDependency().getDependency().getWarps().removeWarp(name);

            List<CaseInsensitiveString> ownedWarps = this.getPlugin().getUserManager().getUser(user).getOwnedWarps();
            ownedWarps.remove(new CaseInsensitiveString(name));
            ownedWarps.add(new CaseInsensitiveString(newName));

            this.getPlugin().getUserManager().saveWarpsAsync(user);

            double cost = this.getPlugin().getConfig().getDouble("costs.rename");
            this.getPlugin().getVaultDependency().performTransaction(user, cost);

            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.rename.success"));
        } catch (Exception e) {
            this.getPlugin().message(user, "&cAn error occurred while renaming warp... Please notify an admin.");
            e.printStackTrace();
        }
    }
}

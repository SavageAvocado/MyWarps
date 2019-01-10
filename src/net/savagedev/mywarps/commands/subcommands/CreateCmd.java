package net.savagedev.mywarps.commands.subcommands;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.commands.state.CommandState;
import net.savagedev.mywarps.utils.CaseInsensitiveString;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.List;

public class CreateCmd extends SubCommand implements Confirmable {
    public CreateCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
        if (!this.validatePermissions(user)) return;

        if (args.length != 2) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments").replace("%command%", "create <warpName>"));
            return;
        }

        if (this.getPlugin().getVaultDependency().getEconomy().getBalance(user) < this.getPlugin().getConfig().getDouble("costs.create")) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.create.insufficient-funds")
                    .replace("%createcost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.create")))
            );
            return;
        }

        if (this.getPlugin().getEssentialsDependency().getDependency().getWarps().getList().stream().anyMatch(args[1]::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.create.already-exists"));
            return;
        }

        this.getPlugin().getUserManager().getConfirmationManager(user).setState(CommandState.AWAITING_CONFIRMATION);
        this.getPlugin().getUserManager().getConfirmationManager(user).setLocation(user.getLocation());
        this.getPlugin().getUserManager().getConfirmationManager(user).setArguments(args);
        this.getPlugin().getUserManager().getConfirmationManager(user).setCommand(this);
        this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.create.confirm")
                .replace("%warpname%", args[1])
                .replace("%createcost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.create")))
        );
    }

    @Override
    public void onConfirm(Player user, String... args) {
        Location location = this.getPlugin().getUserManager().getConfirmationManager(user).getLocation();
        String name = this.getPlugin().getUserManager().getConfirmationManager(user).getArguments()[1];

        try {
            this.getPlugin().getEssentialsDependency().getDependency().getWarps().setWarp(name, location);

            List<CaseInsensitiveString> ownedWarps = this.getPlugin().getUserManager().getUser(user).getOwnedWarps();
            ownedWarps.add(new CaseInsensitiveString(name));

            if (this.getPlugin().getUserManager().isFirstWarp(user))
                for (String command : this.getPlugin().getConfig().getStringList("first-warp-commands"))
                    this.getPlugin().getServer().dispatchCommand(this.getPlugin().getServer().getConsoleSender(), command);

            this.getPlugin().getUserManager().saveWarpsAsync(user);

            double cost = this.getPlugin().getConfig().getDouble("costs.create");
            this.getPlugin().message(this.getPlugin().getServer().getConsoleSender(), "" + cost);
            this.getPlugin().getVaultDependency().performTransaction(user, cost);

            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.create.success"));
        } catch (Exception e) {
            this.getPlugin().message(user, "&cAn error occurred while creating warp... Please notify an admin.");
            e.printStackTrace();
        }
    }
}

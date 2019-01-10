package net.savagedev.mywarps.commands.subcommands;

import com.earth2me.essentials.Essentials;
import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.commands.state.CommandState;
import net.savagedev.mywarps.utils.CaseInsensitiveString;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

public class TransferCmd extends SubCommand implements Confirmable {
    public TransferCmd(MyWarps plugin, Permission permission) {
        super(plugin, permission);
    }

    @Override
    public void execute(Player user, String... args) {
        if (!this.validatePermissions(user)) return;

        if (args.length != 3) {
            if (args.length == 2)
                this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments").replace("%command%", "transfer " + args[1] + " <player>"));
            else
                this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.invalid-arguments").replace("%command%", "transfer <warpName> <player>"));
            return;
        }

        String name = args[1];

        if (Essentials.getPlugin(Essentials.class).getWarps().getList().stream().noneMatch(args[1]::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.transfer.does-not-exist").replace("%warpname%", name));
            return;
        }

        OfflinePlayer newOwner;
        if ((newOwner = Bukkit.getOfflinePlayer(args[2])) == null || !newOwner.hasPlayedBefore()) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.player-not-found").replace("%player%", args[2]));
            return;
        }

        List<String> ownedWarps = this.getPlugin().getConfigUtil().getConfig().getStringList(user.getUniqueId().toString()) == null ? new ArrayList<>() : this.getPlugin().getConfigUtil().getConfig().getStringList(user.getUniqueId().toString());

        if (ownedWarps.stream().noneMatch(name::equalsIgnoreCase)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.transfer.not-owner"));
            return;
        }

        if (newOwner.getPlayer().equals(user)) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.transfer.to-self"));
            return;
        }

        if (this.getPlugin().getVaultDependency().getEconomy().getBalance(user) < this.getPlugin().getConfig().getDouble("costs.transfer")) {
            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.transfer.insufficient-funds")
                    .replace("%deletecost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.transfer")))
            );
            return;
        }

        this.getPlugin().getUserManager().getConfirmationManager(user).setState(CommandState.AWAITING_CONFIRMATION);
        this.getPlugin().getUserManager().getConfirmationManager(user).setArguments(args);
        this.getPlugin().getUserManager().getConfirmationManager(user).setCommand(this);
        this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.transfer.confirm")
                .replace("%warpname%", args[1])
                .replace("%playername%", args[2])
                .replace("%transfercost%", String.valueOf(this.getPlugin().getConfig().getDouble("costs.transfer")))
        );
    }

    @Override
    public void onConfirm(Player user, String... args) {
        String name = this.getPlugin().getUserManager().getConfirmationManager(user).getArguments()[1];
        String newOwner = this.getPlugin().getUserManager().getConfirmationManager(user).getArguments()[2];

        try {
            List<CaseInsensitiveString> ownedWarpsOwner = this.getPlugin().getUserManager().getUser(user).getOwnedWarps();
            ownedWarpsOwner.remove(new CaseInsensitiveString(name));

            OfflinePlayer newOwnerU;

            if ((newOwnerU = Bukkit.getOfflinePlayer(newOwner)) == null || !newOwnerU.hasPlayedBefore()) {
                this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.player-not-found").replace("%player%", newOwner));
                return;
            }

            if (newOwnerU.isOnline())
                this.getPlugin().getUserManager().getUser(newOwnerU).getOwnedWarps().add(new CaseInsensitiveString(name));

            List<String> ownedWarpsNewOwner = this.getPlugin().getConfigUtil().getConfig().getStringList(newOwnerU.getUniqueId().toString()) == null ? new ArrayList<>() : this.getPlugin().getConfigUtil().getConfig().getStringList(newOwnerU.getUniqueId().toString());
            ownedWarpsNewOwner.add(name);

            this.getPlugin().getUserManager().saveWarpsAsync(user);

            this.getPlugin().getConfigUtil().getConfig().set(newOwnerU.getUniqueId().toString(), ownedWarpsNewOwner);
            this.getPlugin().getConfigUtil().save();
            this.getPlugin().getConfigUtil().reload();

            double cost = this.getPlugin().getConfig().getDouble("costs.transfer");
            this.getPlugin().getVaultDependency().performTransaction(user, cost);

            this.getPlugin().message(user, this.getPlugin().getConfig().getString("messages.transfer.success"));
        } catch (Exception e) {
            this.getPlugin().message(user, "&cAn error occurred while transferring warp... Please notify an admin.");
            e.printStackTrace();
        }
    }
}

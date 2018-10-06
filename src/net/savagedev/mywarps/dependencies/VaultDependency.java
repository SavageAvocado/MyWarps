package net.savagedev.mywarps.dependencies;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.savagedev.mywarps.MyWarps;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultDependency extends Dependency<Vault> {
    private Economy economy;

    public VaultDependency(MyWarps plugin, String dependency) {
        super(plugin, dependency);
    }

    @Override
    public void onHook() {
        RegisteredServiceProvider<Economy> rsp;
        if ((rsp = this.getHandlingPlugin().getServer().getServicesManager().getRegistration(Economy.class)) == null) {
            this.getHandlingPlugin().getPluginManager().disablePlugin(this.getHandlingPlugin());
        }

        if ((this.economy = rsp.getProvider()) == null) {
            this.getHandlingPlugin().getPluginManager().disablePlugin(this.getHandlingPlugin());
        }
    }

    public void performTransaction(Player user, double amount) {
        if (amount < 0.0) {
            this.getEconomy().depositPlayer(user, amount);
            Bukkit.getServer().getLogger().info( amount + " less than 0.0. Paying player " + Math.abs(amount));
        } else {
            this.getEconomy().withdrawPlayer(user, amount);
            Bukkit.getServer().getLogger().info(amount + " more than 0.0. Withdrawing " + amount + " from player.");
        }
    }

    public Economy getEconomy() {
        return this.economy;
    }
}

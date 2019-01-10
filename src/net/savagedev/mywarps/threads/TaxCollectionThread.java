package net.savagedev.mywarps.threads;

import net.savagedev.mywarps.MyWarps;
import net.savagedev.mywarps.player.MyWarpsUser;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaxCollectionThread extends BukkitRunnable {
    private long lastTaxCollection;
    private MyWarps plugin;
    private double tax;

    public TaxCollectionThread(MyWarps plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        if (!this.plugin.getConfigUtil().getConfig().getConfigurationSection("").contains("last-tax-collection")) {
            this.plugin.getConfigUtil().getConfig().set("last-tax-collection", System.currentTimeMillis());
            this.plugin.getConfigUtil().save();
            this.plugin.getConfigUtil().reload();
        }

        this.lastTaxCollection = this.plugin.getConfigUtil().getConfig().getLong("last-tax-collection");
        this.tax = this.plugin.getConfig().getDouble("costs.tax");
    }

    @Override
    public void run() {
        long days = (System.currentTimeMillis() - this.lastTaxCollection) / (1000 * 60 * 60 * 24) % 30;
        if (days < 1) return;

        for (int i = 0; i < days; i++)
            this.collectTaxes();

        this.lastTaxCollection = System.currentTimeMillis();

        this.plugin.getConfigUtil().getConfig().set("last-tax-collection", this.lastTaxCollection);
        this.plugin.getConfigUtil().save();
        this.plugin.getConfigUtil().reload();
    }

    private void collectTaxes() {
        System.out.println("Starting MyWarps tax collection task.");
        long start = System.currentTimeMillis();

        for (String uuidString : this.plugin.getConfigUtil().getConfig().getConfigurationSection("").getKeys(false)) {
            if (!this.isUuid(uuidString)) continue;
            UUID uuid = UUID.fromString(uuidString);

            Queue<String> ownedWarps = this.plugin.getConfigUtil().getConfig().getStringList(uuid.toString()) == null ? new ConcurrentLinkedQueue<>() : new ConcurrentLinkedQueue<>(this.plugin.getConfigUtil().getConfig().getStringList(uuid.toString()));
            if (ownedWarps.isEmpty()) continue;

            List<String> removedWarps = new ArrayList<>();
            OfflinePlayer user = this.plugin.getServer().getOfflinePlayer(uuid);
            for (String warp : ownedWarps) {
                if (this.plugin.getVaultDependency().getEconomy().getBalance(user) < this.tax) {
                    try {
                        this.plugin.getEssentialsDependency().getDependency().getWarps().removeWarp(warp);
                        ownedWarps.remove(warp);

                        if (user.isOnline()) {
                            this.plugin.getUserManager().saveWarps(user);
                            removedWarps.add(warp);
                            continue;
                        }

                        List<String> stringOwnedWarps = new ArrayList<>(ownedWarps);

                        this.plugin.getConfigUtil().getConfig().set(user.getUniqueId().toString(), stringOwnedWarps);
                        this.plugin.getConfigUtil().save();
                        this.plugin.getConfigUtil().reload();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    this.plugin.getVaultDependency().performTransaction(user, this.tax);
            }

            MyWarpsUser myWarpsUser = this.plugin.getUserManager().getUser(user);

            if (user.isOnline()) {
                if (myWarpsUser.getOwnedWarps().size() > 0)
                    this.plugin.message(user.getPlayer(), this.plugin.getConfig().getString("messages.tax").replace("%tax%", String.valueOf(this.tax * myWarpsUser.getOwnedWarps().size())));

                if (removedWarps.size() > 0) {
                    ArrayList<String> newOwnedWarps = new ArrayList<>();
                    for (String warp : removedWarps)
                        newOwnedWarps.add(this.plugin.getConfig().getString("messages.list.format")
                                .replace("%warpname%", warp)
                        );

                    this.plugin.message(user.getPlayer(), this.plugin.getConfig().getString("messages.too-poor").replace("%warps%", StringUtils.join(newOwnedWarps, ", ")));
                }
            }
        }

        System.out.println("MyWarps tax collection complete. Operation took " + String.valueOf(System.currentTimeMillis() - start) + "ms.");
    }

    private boolean isUuid(String potentialUuid) {
        try {
            UUID.fromString(potentialUuid);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}

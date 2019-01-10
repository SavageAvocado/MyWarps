package net.savagedev.mywarps;

import net.savagedev.mywarps.commands.MyWarpsCmd;
import net.savagedev.mywarps.dependencies.EssentialsDependency;
import net.savagedev.mywarps.dependencies.VaultDependency;
import net.savagedev.mywarps.listeners.CommandE;
import net.savagedev.mywarps.listeners.JoinE;
import net.savagedev.mywarps.listeners.QuitE;
import net.savagedev.mywarps.player.MyWarpsUserManager;
import net.savagedev.mywarps.threads.TaxCollectionThread;
import net.savagedev.mywarps.utils.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyWarps extends JavaPlugin {
    private EssentialsDependency essentialsDependency;
    private VaultDependency vaultDependency;
    private MyWarpsUserManager userManager;
    private PluginManager pluginManager;
    private ConfigUtil configUtil;

    @Override
    public void onEnable() {
        this.loadDependencies();
        this.loadFiles();
        this.loadUtils();
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void onDisable() {
        this.userManager.saveAllWarps();
    }

    private void loadUtils() {
        this.configUtil = new ConfigUtil(this);
        this.userManager = new MyWarpsUserManager(this);
        new TaxCollectionThread(this).runTaskTimerAsynchronously(this, 0L, 1200L);
    }

    private void loadDependencies() {
        this.pluginManager = this.getServer().getPluginManager();
        this.essentialsDependency = new EssentialsDependency(this, "Essentials");
        this.vaultDependency = new VaultDependency(this, "Vault");
    }

    private void loadFiles() {
        this.saveDefaultConfig();
    }

    private void loadCommands() {
        this.getCommand("mywarps").setExecutor(new MyWarpsCmd(this));
    }

    private void loadListeners() {
        this.pluginManager.registerEvents(new CommandE(this), this);
        this.pluginManager.registerEvents(new JoinE(this), this);
        this.pluginManager.registerEvents(new QuitE(this), this);
    }

    public void message(CommandSender user, String message) {
        user.sendMessage(this.color(message));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public EssentialsDependency getEssentialsDependency() {
        return this.essentialsDependency;
    }

    public VaultDependency getVaultDependency() {
        return this.vaultDependency;
    }

    public MyWarpsUserManager getUserManager() {
        return this.userManager;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public ConfigUtil getConfigUtil() {
        return this.configUtil;
    }
}

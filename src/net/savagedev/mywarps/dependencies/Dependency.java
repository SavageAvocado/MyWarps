package net.savagedev.mywarps.dependencies;

import net.savagedev.mywarps.MyWarps;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public abstract class Dependency<T extends Plugin> implements IDependency {
    private MyWarps plugin;
    private T dependency;

    Dependency(MyWarps plugin, String dependency) {
        this.plugin = plugin;
        this.hook(dependency);
        this.onHook();
    }

    private void hook(String dependencyName) {
        if (this.getHandlingPlugin().getPluginManager().getPlugin("Vault") == null) {
            this.getHandlingPlugin().getServer().getLogger().log(Level.WARNING, "MyWarps requires " + dependencyName + "! Disabling plugin...");
            this.getHandlingPlugin().getPluginManager().disablePlugin(this.getHandlingPlugin());
        }

        this.dependency = (T) this.plugin.getServer().getPluginManager().getPlugin(dependencyName);
    }

    MyWarps getHandlingPlugin() {
        return this.plugin;
    }

    @Override
    public T getDependency() {
        return this.dependency;
    }
}

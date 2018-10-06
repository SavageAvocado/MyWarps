package net.savagedev.mywarps.dependencies;

import org.bukkit.plugin.Plugin;

public interface IDependency<T extends Plugin> {
    void onHook();

    T getDependency();
}

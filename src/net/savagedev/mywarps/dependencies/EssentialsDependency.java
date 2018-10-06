package net.savagedev.mywarps.dependencies;

import com.earth2me.essentials.Essentials;
import net.savagedev.mywarps.MyWarps;

public class EssentialsDependency extends Dependency<Essentials> {
    public EssentialsDependency(MyWarps plugin, String dependency) {
        super(plugin, dependency);
    }

    @Override
    public void onHook() {
    }
}

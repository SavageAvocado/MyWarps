package net.savagedev.mywarps.utils;

import net.savagedev.mywarps.MyWarps;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {
    private FileConfiguration configuration;
    private MyWarps plugin;
    private File file;

    public ConfigUtil(MyWarps plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        this.file = new File(this.plugin.getDataFolder(), "warps.yml");
        this.configuration = new YamlConfiguration();

        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.reload();
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            this.configuration.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return this.configuration;
    }
}

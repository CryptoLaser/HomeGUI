package com.jaaakee.homegui.configuration;

import java.io.File;

import com.jaaakee.homegui.HomeGUI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class HomeData {

    private File settingsFile;
    private FileConfiguration settingsConfig;

    public HomeData() {
        this.settingsFile = new File(HomeGUI.getInstance().getDataFolder(), "homeData.yml");
        this.settingsConfig = YamlConfiguration.loadConfiguration(this.settingsFile);

        try {
            this.settingsConfig.save(this.settingsFile);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public FileConfiguration getSettingsConfig() {
        return this.settingsConfig;
    }

    public void saveSettingsConfig() {
        try {
            this.settingsConfig.save(this.settingsFile);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
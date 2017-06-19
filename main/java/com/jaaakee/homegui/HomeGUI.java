package com.jaaakee.homegui;

import com.jaaakee.homegui.commands.HomeCommand;
import com.jaaakee.homegui.configuration.ConfigurationFile;
import com.jaaakee.homegui.configuration.HomeData;
import com.jaaakee.homegui.events.InventoryClickListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class HomeGUI extends JavaPlugin {

    private HomeData configuration;
    private ConfigurationFile configurationFile;
    static HomeGUI instance;

    @Override
    public void onEnable() {
        instance = this;
        this.configuration = new HomeData();

        try {
            configurationFile = new ConfigurationFile(new File(this.getDataFolder(), "config.yml"));

            if (!configurationFile.getFile().exists()) {
                saveDefaultConfig();
            }

            configurationFile.load();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            Bukkit.getServer().getConsoleSender().sendMessage("HomeGUI: Error while loading the configuration.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);

        getCommand("home").setExecutor(new HomeCommand());
    }

    @Override
    public void onDisable() {
        instance = null;
        this.configuration = null;

        try {
            configurationFile.save();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            getServer().getConsoleSender().sendMessage("HomeGUI: Error while saving the configuration!");
        }
    }

    public static HomeGUI getInstance() {
        return instance;
    }

    public HomeData getConfiguration() {
        return this.configuration;
    }

    public ConfigurationFile getConfigurationFile() {
        return this.configurationFile;
    }
}
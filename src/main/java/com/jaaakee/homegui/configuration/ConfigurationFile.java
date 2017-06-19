package com.jaaakee.homegui.configuration;

import java.io.File;
import java.util.Arrays;

public class ConfigurationFile extends ConfigurationHandler {

    @ConfigOptions(name = "HomeGUI.menuName")
    public String homeGUIMenuName;

    public ConfigurationFile(final File file) {
        super(file, Arrays.asList("HomeGUI ConfigurationFile"));
        homeGUIMenuName = "Your Home List";
    }
}
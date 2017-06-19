package com.jaaakee.homegui.inventory;

import com.jaaakee.homegui.HomeGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

public class HomeInventory {

    private static Material[] blocks;

    public static Inventory createConfirmInventory() {
        Inventory inventory = Bukkit.getServer().createInventory(null, 9, "Overwrite Existing Home?");

        final ItemStack setHomeItem2 = new ItemStack(Material.WOOL, 1, (short) 5);
        final ItemMeta setHomeItemMeta2 = setHomeItem2.getItemMeta();
        setHomeItemMeta2.setDisplayName("Yes, I would like to overwrite my existing home.");
        setHomeItem2.setItemMeta(setHomeItemMeta2);
        inventory.setItem(3, setHomeItem2);

        final ItemStack delHomeItem2 = new ItemStack(Material.WOOL, 1, (short) 14);
        final ItemMeta delHomeItemMeta2 = delHomeItem2.getItemMeta();
        delHomeItemMeta2.setDisplayName("No, I would like to cancel.");
        delHomeItem2.setItemMeta(delHomeItemMeta2);
        inventory.setItem(5, delHomeItem2);


        return inventory;
    }

    public static Inventory createHomeInventory(final String playerUUID) {
        String newMenuName = HomeGUI.getInstance().getConfigurationFile().homeGUIMenuName;

        if (HomeGUI.getInstance().getConfigurationFile().homeGUIMenuName.length() >= 18) {
            newMenuName = HomeGUI.getInstance().getConfigurationFile().homeGUIMenuName.substring(0, 18);
        }

        Inventory inventory = Bukkit.getServer().createInventory(null, 9, newMenuName + " " + ChatColor.GREEN + "[Teleport]");
        int count = 0;

        if (HomeGUI.getInstance().getConfiguration().getSettingsConfig().contains(playerUUID)) {
            final Set<String> homelist = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getConfigurationSection(playerUUID).getKeys(false);
            int rows = 1;

            if (homelist.size() > 6) {
                for (int i = 0; i < homelist.size(); ++i) {
                    if ((i + 2) % 8 == 0) {
                        ++rows;
                    }
                }
            }

            int size = rows * 9;
            if (size > 54) {
                size = 54;
            }

            inventory = Bukkit.getServer().createInventory(null, size, newMenuName + " " + ChatColor.GREEN + "[Teleport]");
            for (final String homes : homelist) {
                final String iconName = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getString(playerUUID + "." + homes + "." + "icon");
                final ItemStack homeItem = new ItemStack(Material.getMaterial(iconName));
                final ItemMeta homeItemMeta = homeItem.getItemMeta();
                homeItemMeta.setDisplayName(String.valueOf(homes));
                homeItem.setItemMeta(homeItemMeta);
                inventory.setItem(count, homeItem);
                ++count;
            }

            final ItemStack setHomeItem = new ItemStack(Material.WOOL, 1, (short) 5);
            final ItemMeta setHomeItemMeta = setHomeItem.getItemMeta();
            setHomeItemMeta.setDisplayName("Create Home");
            setHomeItem.setItemMeta(setHomeItemMeta);
            inventory.setItem(size - 2, setHomeItem);
            final ItemStack delHomeItem = new ItemStack(Material.WOOL, 1, (short) 14);
            final ItemMeta delHomeItemMeta = delHomeItem.getItemMeta();
            delHomeItemMeta.setDisplayName("Delete Home");
            delHomeItem.setItemMeta(delHomeItemMeta);
            inventory.setItem(size - 1, delHomeItem);
        } else {
            final ItemStack setHomeItem2 = new ItemStack(Material.WOOL, 1, (short) 5);
            final ItemMeta setHomeItemMeta2 = setHomeItem2.getItemMeta();
            setHomeItemMeta2.setDisplayName("Create Home");
            setHomeItem2.setItemMeta(setHomeItemMeta2);
            inventory.setItem(7, setHomeItem2);
            final ItemStack delHomeItem2 = new ItemStack(Material.WOOL, 1, (short) 14);
            final ItemMeta delHomeItemMeta2 = delHomeItem2.getItemMeta();
            delHomeItemMeta2.setDisplayName("Delete Home");
            delHomeItem2.setItemMeta(delHomeItemMeta2);
            inventory.setItem(8, delHomeItem2);
        }
        return inventory;
    }

    public static Inventory createDeleteHomeInventory(final String playerUUID) {
        String newMenuName = HomeGUI.getInstance().getConfigurationFile().homeGUIMenuName;

        if (HomeGUI.getInstance().getConfigurationFile().homeGUIMenuName.length() >= 18) {
            newMenuName = HomeGUI.getInstance().getConfigurationFile().homeGUIMenuName.substring(0, 18);
        }

        Inventory inventory = Bukkit.getServer().createInventory(null, 9, newMenuName + " " + ChatColor.RED + "[Delete]");
        int count = 0;

        if (HomeGUI.getInstance().getConfiguration().getSettingsConfig().contains(playerUUID)) {
            final Set<String> homelist = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getConfigurationSection(playerUUID).getKeys(false);
            int rows = 1;
            if (homelist.size() > 7) {
                for (int i = 0; i < homelist.size(); ++i) {
                    if ((i + 1) % 9 == 0) {
                        ++rows;
                    }
                }
            }

            int size = rows * 9;
            if (size > 54) {
                size = 54;
            }

            inventory = Bukkit.getServer().createInventory(null, size, newMenuName + " " + ChatColor.RED + "[Delete]");
            for (final String homes : homelist) {
                final String iconName = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getString(playerUUID + "." + homes + "." + "icon");
                final ItemStack homeItem = new ItemStack(Material.getMaterial(iconName));
                final ItemMeta homeItemMeta = homeItem.getItemMeta();
                homeItemMeta.setDisplayName(String.valueOf(homes));
                homeItem.setItemMeta(homeItemMeta);
                inventory.setItem(count, homeItem);
                ++count;
            }

            final ItemStack delHomeItem = new ItemStack(Material.WOOL, 1, (short) 14);
            final ItemMeta delHomeItemMeta = delHomeItem.getItemMeta();
            delHomeItemMeta.setDisplayName("Back");
            delHomeItem.setItemMeta(delHomeItemMeta);
            inventory.setItem(size - 1, delHomeItem);
        } else {
            final ItemStack delHomeItem2 = new ItemStack(Material.WOOL, 1, (short) 14);
            final ItemMeta delHomeItemMeta2 = delHomeItem2.getItemMeta();
            delHomeItemMeta2.setDisplayName("Back");
            delHomeItem2.setItemMeta(delHomeItemMeta2);
            inventory.setItem(8, delHomeItem2);
        }
        return inventory;
    }

    public static Inventory createSetIconInventory(final String homeName) {
        final Inventory inventory = Bukkit.getServer().createInventory(null, 27, "Select Icon");
        for (int i = 0; i < HomeInventory.blocks.length; ++i) {
            final ItemStack iconItem = new ItemStack(HomeInventory.blocks[i]);
            final ItemMeta iconItemMeta = iconItem.getItemMeta();
            iconItemMeta.setDisplayName(homeName);
            iconItem.setItemMeta(iconItemMeta);
            inventory.setItem(i, iconItem);
        }
        return inventory;
    }

    static {
        HomeInventory.blocks = new Material[]{Material.GRASS, Material.DIRT, Material.STONE, Material.COBBLESTONE, Material.LOG, Material.WOOD, Material.GLASS, Material.BRICK, Material.BOOKSHELF, Material.OBSIDIAN, Material.NETHER_BRICK, Material.NETHERRACK, Material.COAL_BLOCK, Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.HAY_BLOCK, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE, Material.BED, Material.WORKBENCH, Material.FURNACE, Material.CHEST};
    }
}
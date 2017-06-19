package com.jaaakee.homegui.events;

import com.jaaakee.homegui.HomeGUI;
import com.jaaakee.homegui.inventory.AnvilGUI;
import com.jaaakee.homegui.inventory.HomeInventory;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {

        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getInventory();
        String homeNameSet = "";

        final int slotNum = event.getRawSlot();
        String menuName = HomeGUI.getInstance().getConfigurationFile().homeGUIMenuName;

        int homeCount = 0;
        if (menuName.length() >= 18) {
            menuName = menuName.substring(0, 18);
        }

        if (HomeGUI.getInstance().getConfiguration().getSettingsConfig().contains(player.getUniqueId().toString())) {
            homeCount = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getConfigurationSection(player.getUniqueId().toString()).getKeys(false).size();
        }

        if (inventory.getName().equals(menuName + " " + ChatColor.GREEN + "[Teleport]")) {
            if (slotNum < inventory.getSize()) {
                if (slotNum == inventory.getSize() - 2) {

                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "Enter a Name for your Home.");

                    final AnvilGUI gui = new AnvilGUI(player, new AnvilGUI.AnvilClickEventHandler() {

                        @Override
                        public void onAnvilClick(final AnvilGUI.AnvilClickEvent event) {
                            if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
                                event.setWillClose(true);
                                event.setWillDestroy(true);
                                final String homeName = event.getName();

                                if (homeName.equals("")) {
                                    player.sendMessage(ChatColor.RED + "Your home name cannot be empty!");
                                    return;
                                }

                                final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                                scheduler.scheduleSyncDelayedTask(HomeGUI.getInstance(), new Runnable() {

                                    @Override
                                    public void run() {
                                        openSlotInventory(player, homeName);
                                    }
                                }, 1L);
                            } else {
                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            }
                        }
                    });
                    final ItemStack item = new ItemStack(Material.NAME_TAG);
                    final ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("Enter a Name");
                    item.setItemMeta(meta);
                    gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, item);
                    gui.open();

                } else if (slotNum == inventory.getSize() - 1) {
                    player.openInventory(HomeInventory.createDeleteHomeInventory(player.getUniqueId().toString()));

                } else if (slotNum >= 0 && slotNum <= homeCount && inventory.getItem(slotNum) != null) {
                    homeNameSet = inventory.getItem(slotNum).getItemMeta().getDisplayName();
                    final double x = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getInt(player.getUniqueId().toString() + "." + homeNameSet + "." + "x") + 0.5;
                    final double y = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getInt(player.getUniqueId().toString() + "." + homeNameSet + "." + "y");
                    final double z = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getInt(player.getUniqueId().toString() + "." + homeNameSet + "." + "z") + 0.5;
                    final float pitch = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getInt(player.getUniqueId().toString() + "." + homeNameSet + "." + "pitch");
                    final float yaw = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getInt(player.getUniqueId().toString() + "." + homeNameSet + "." + "yaw");
                    final String worldName = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getString(player.getUniqueId().toString() + "." + homeNameSet + "." + "world");
                    final World world = Bukkit.getWorld(worldName);
                    final Location location = new Location(world, x, y, z, yaw, pitch);
                    final Entity entity = player.getVehicle();

                    if (entity != null) {
                        entity.eject();
                    }

                    final boolean loaded = world.getChunkAt(location).load();

                    if (loaded) {
                        if (entity != null) {
                            if (entity.getType().name().equals("BOAT") | entity.getType().name().equals("MINECART")) {
                                player.getServer().getScheduler().scheduleSyncDelayedTask(HomeGUI.getInstance(), new Runnable() {

                                    @Override
                                    public void run() {
                                        player.teleport(location);
                                    }
                                }, 2L);
                            } else {
                                entity.teleport(location);
                                player.teleport(location);
                                entity.setPassenger(player);
                            }
                        } else {
                            player.teleport(location);
                        }
                        player.sendMessage(ChatColor.GREEN + "You teleported to " + ChatColor.YELLOW + homeNameSet + ChatColor.GREEN + "!");
                    } else {
                        if (entity != null) {
                            if (entity.getType().name().equals("BOAT") | entity.getType().name().equals("MINECART")) {
                                player.getServer().getScheduler().scheduleSyncDelayedTask(HomeGUI.getInstance(), new Runnable() {

                                    @Override
                                    public void run() {
                                        player.teleport(location);
                                    }
                                }, 2L);
                            } else {
                                entity.teleport(location);
                                player.teleport(location);
                            }
                        }
                        player.sendMessage(ChatColor.GREEN + "You teleported to " + ChatColor.YELLOW + homeNameSet + ChatColor.GREEN + "!");

                        while (!player.getWorld().isChunkLoaded(world.getChunkAt(location))) {
                            player.teleport(location);
                        }

                        if (entity != null && (!entity.getType().name().equals("BOAT") | !entity.getType().name().equals("MINECART"))) {
                            entity.setPassenger(player);
                        }
                    }
                }
            }
            event.setCancelled(true);

        } else if (inventory.getName().equals("Overwrite Existing Home?")) {
            if (slotNum == inventory.getSize() - 6) {
                player.openInventory(HomeInventory.createSetIconInventory(homeNameSet));
                player.sendMessage(ChatColor.GREEN + "Name set. Now select an icon.");
            } else if (slotNum == inventory.getSize() - 4) {
                player.openInventory(HomeInventory.createHomeInventory(player.getUniqueId().toString()));
            }

        } else if (inventory.getName().equals(menuName + " " + ChatColor.RED + "[Delete]")) {
            if (slotNum == inventory.getSize() - 1) {
                player.openInventory(HomeInventory.createHomeInventory(player.getUniqueId().toString()));
            } else if (slotNum >= 0 && slotNum <= homeCount) {
                if (inventory.getItem(slotNum) != null) {
                    homeNameSet = inventory.getItem(slotNum).getItemMeta().getDisplayName();

                    if (HomeGUI.getInstance().getConfiguration().getSettingsConfig().contains(player.getUniqueId().toString() + "." + homeNameSet)) {
                        HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet, null);
                        player.sendMessage(ChatColor.RED + "Home " + ChatColor.YELLOW + homeNameSet + ChatColor.RED + " Deleted!");
                    }

                    if (HomeGUI.getInstance().getConfiguration().getSettingsConfig().contains(player.getUniqueId().toString())) {
                        final Set<String> homelist = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getConfigurationSection(player.getUniqueId().toString()).getKeys(false);
                        if (homelist.size() == 0) {
                            HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString(), null);
                        }
                    }
                }
                player.openInventory(HomeInventory.createDeleteHomeInventory(player.getUniqueId().toString()));
            }
            event.setCancelled(true);

        } else if (inventory.getName().equals("Select Icon") && slotNum >= 0 && slotNum < 27) {
            homeNameSet = inventory.getItem(slotNum).getItemMeta().getDisplayName();
            if (HomeGUI.getInstance().getConfiguration().getSettingsConfig().contains(player.getUniqueId().toString())) {
                final Set<String> homelist = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getConfigurationSection(player.getUniqueId().toString()).getKeys(false);
                int homeLimit = 0;

                for (int i = 1; i <= 52; ++i) {
                    if (player.hasPermission("homegui.limit." + i)) {
                        homeLimit = i;
                    }
                }

                if (homeLimit == 0) {
                    homeLimit = 1;
                }

                if (homelist.size() < homeLimit) {
                    HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "icon", inventory.getItem(slotNum).getType().name());
                    HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "world", player.getWorld().getName());
                    HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "x", player.getLocation().getBlockX());
                    HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "y", player.getLocation().getBlockY());
                    HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "z", player.getLocation().getBlockZ());
                    HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "pitch", player.getLocation().getPitch());
                    HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "yaw", player.getLocation().getYaw());
                    player.sendMessage(ChatColor.GREEN + "Home " + ChatColor.YELLOW + homeNameSet + ChatColor.GREEN + " Created!");
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "You have reached your allowed maximum of houses!");
                }
            } else {
                HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "icon", inventory.getItem(slotNum).getType().name());
                HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "world", player.getWorld().getName());
                HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "x", player.getLocation().getBlockX());
                HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "y", player.getLocation().getBlockY());
                HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "z", player.getLocation().getBlockZ());
                HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "pitch", player.getLocation().getPitch());
                HomeGUI.getInstance().getConfiguration().getSettingsConfig().set(player.getUniqueId().toString() + "." + homeNameSet + "." + "yaw", player.getLocation().getYaw());
                player.sendMessage(ChatColor.GREEN + "Home " + ChatColor.YELLOW + homeNameSet + ChatColor.GREEN + " Created!");
            }
            player.closeInventory();
        }
        HomeGUI.getInstance().getConfiguration().saveSettingsConfig();
    }

    public void openSlotInventory(final Player player, final String homeName) {
        ConfigurationSection section = HomeGUI.getInstance().getConfiguration().getSettingsConfig().getConfigurationSection(player.getUniqueId().toString());
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (key.equals(homeName)) {
                    player.sendMessage(ChatColor.RED + "You already have a home with this name. Would you like to overwrite it?");
                    player.openInventory(HomeInventory.createConfirmInventory());
                } else {
                    player.openInventory(HomeInventory.createSetIconInventory(homeName));
                    player.sendMessage(ChatColor.GREEN + "Name set. Now select an icon.");
                }
            }
        } else {
            player.openInventory(HomeInventory.createSetIconInventory(homeName));
            player.sendMessage(ChatColor.GREEN + "Name set. Now select an icon.");
        }
        return;
    }
}
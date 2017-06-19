package com.jaaakee.homegui.inventory;

import com.jaaakee.homegui.HomeGUI;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class AnvilGUI {

    private Player player;
    private HashMap<AnvilSlot, ItemStack> items;
    private Inventory inv;
    private Listener listener;

    public AnvilGUI(final Player player, final AnvilClickEventHandler handler) {
        this.items = new HashMap();
        this.player = player;
        listener = new Listener() {

            @EventHandler
            public void onInventoryClick(final InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player && event.getInventory().equals(AnvilGUI.this.inv)) {
                    event.setCancelled(true);
                    final ItemStack item = event.getCurrentItem();
                    final int slot = event.getRawSlot();
                    String name = "";

                    if (item != null && item.hasItemMeta()) {
                        final ItemMeta meta = item.getItemMeta();
                        if (meta.hasDisplayName()) {
                            name = meta.getDisplayName();
                        }
                    }

                    final AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);
                    handler.onAnvilClick(clickEvent);

                    if (clickEvent.getWillClose()) {
                        event.getWhoClicked().closeInventory();
                    }

                    if (clickEvent.getWillDestroy()) {
                        AnvilGUI.this.destroy();
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(final InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    final Inventory inv = event.getInventory();
                    if (inv.equals(AnvilGUI.this.inv)) {
                        inv.clear();
                        AnvilGUI.this.destroy();
                    }
                }
            }

            @EventHandler
            public void onPlayerQuit(final PlayerQuitEvent event) {
                if (event.getPlayer().equals(AnvilGUI.this.getPlayer())) {
                    AnvilGUI.this.destroy();
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(this.listener, HomeGUI.getInstance());
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setSlot(final AnvilSlot slot, final ItemStack item) {
        this.items.put(slot, item);
    }

    public void open() {
        final EntityPlayer p = ((CraftPlayer) this.player).getHandle();
        final AnvilContainer container = new AnvilContainer(p);
        this.inv = container.getBukkitView().getTopInventory();

        for (final AnvilSlot slot : this.items.keySet()) {
            this.inv.setItem(slot.getSlot(), items.get(slot));
        }

        final int c = p.nextContainerCounter();
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing", new Object[0]), 0));
        p.activeContainer = container;
        p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener(p);
    }

    public void destroy() {
        this.player = null;
        this.items = null;
        HandlerList.unregisterAll(this.listener);
        this.listener = null;
    }

    private class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(final EntityHuman entity) {
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
        }

        public boolean a(final EntityHuman entityhuman) {
            return true;
        }
    }

    public enum AnvilSlot {
        INPUT_LEFT(0),
        OUTPUT(2);

        private int slot;

        private AnvilSlot(final int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return this.slot;
        }

        public static AnvilSlot bySlot(final int slot) {
            for (final AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }
            return null;
        }
    }

    public class AnvilClickEvent {
        private AnvilSlot slot;
        private String name;
        private boolean close;
        private boolean destroy;

        public AnvilClickEvent(final AnvilSlot slot, final String name) {
            this.close = true;
            this.destroy = true;
            this.slot = slot;
            this.name = name;
        }

        public AnvilSlot getSlot() {
            return this.slot;
        }

        public String getName() {
            return this.name;
        }

        public boolean getWillClose() {
            return this.close;
        }

        public void setWillClose(final boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return this.destroy;
        }

        public void setWillDestroy(final boolean destroy) {
            this.destroy = destroy;
        }
    }

    public interface AnvilClickEventHandler {
        void onAnvilClick(AnvilClickEvent p0);
    }
}
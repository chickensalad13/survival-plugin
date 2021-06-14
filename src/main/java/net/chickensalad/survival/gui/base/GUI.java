package net.chickensalad.survival.gui.base;

import com.google.inject.Inject;
import net.chickensalad.survival.Survival;
import net.chickensalad.survival.gui.WarpEditGUI;
import net.chickensalad.survival.gui.WarpListGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class GUI {

    protected static final ItemStack SPACER = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

    static {
        ItemMeta spacerMeta = SPACER.getItemMeta();
        spacerMeta.setDisplayName(" ");
        SPACER.setItemMeta(spacerMeta);
    }

    protected InventoryProxy inventory;
    private Inventory bukkitInventory;
    private boolean populated = false;
    private boolean invCheckOverride = false;
    private boolean allowDrag = false;
    private boolean allowShiftClicking = false;
    private BukkitRunnable updaterTask;

    @Inject
    private Survival plugin;

    public GUI(String title, int size) {
        this.bukkitInventory = Bukkit.createInventory(null, getInvSizeForCount(size), title);
        this.inventory = new InventoryProxy(bukkitInventory, Bukkit.createInventory(null, getInvSizeForCount(size), title));
        Bukkit.getPluginManager().registerEvents(new GUIEvents(this), plugin);
    }

    public GUI(String title, InventoryType type) {
        this.bukkitInventory = Bukkit.createInventory(null, type, title);
        this.inventory = new InventoryProxy(bukkitInventory, Bukkit.createInventory(null, type, title));
        Bukkit.getPluginManager().registerEvents(new GUIEvents(this), plugin);
    }

    public GUI(Inventory bukkitInventory) {
        this.bukkitInventory = bukkitInventory;
        this.inventory = new InventoryProxy(bukkitInventory, bukkitInventory);
        Bukkit.getPluginManager().registerEvents(new GUIEvents(this), plugin);
    }

    public final void open(Player p) {
        try {
            if (!this.populated) {
                this.populate();
                this.inventory.apply();
                this.populated = true;
            }
            this.openInventory(p);
        } catch (Throwable e) {
            this.throwError(e);
        }
    }

    public final void close() {
        new ArrayList<>(this.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
    }

    protected void openInventory(Player p) {
        p.openInventory(this.getInventory());
    }

    protected Inventory getInventory() {
        return bukkitInventory;
    }

    protected abstract void onPlayerClick(InventoryClickEvent event);

    protected void onTickUpdate() {
    }

    protected void onPlayerCloseInv() {
    }

    protected void onPlayerDrag(InventoryDragEvent event) {
    }

    protected final int getInvSizeForCount(int count) {
        int size = (count / 9) * 9;
        if (count % 9 > 0) size += 9;
        if (size < 9) return 9;
        if (size > 54) return 54;
        return size;
    }

    public void setInvCheckOverride(boolean invCheckOverride) {
        this.invCheckOverride = invCheckOverride;
    }

    protected abstract void populate();

    protected final void repopulate() {
        try {
            this.inventory.clear();
            this.populate();
            this.inventory.apply();
            this.populated = true;
        } catch (Throwable e) {
            this.throwError(e);
        }
    }

    protected final void setUpdateTicks(int ticks) {
        this.setUpdateTicks(ticks, false);
    }

    protected final void setUpdateTicks(int ticks, boolean sync) {
        if (this.updaterTask != null) {
            this.updaterTask.cancel();
            this.updaterTask = null;
        }
        this.updaterTask = new GUIUpdateTask(this);
        if (sync) {
            this.updaterTask.runTaskTimer(plugin, 0, ticks);
        } else {
            this.updaterTask.runTaskTimerAsynchronously(plugin, 0, ticks);
        }
    }

    protected final void scheduleOpen(final WarpEditGUI gui, final Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> gui.open(player));
    }

    protected final void scheduleOpen(final WarpListGUI gui, final Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> gui.open(player));
    }

    protected void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
    }

    protected boolean isAllowShiftClicking() {
        return allowShiftClicking;
    }

    protected void setAllowShiftClicking(boolean allowShiftClicking) {
        this.allowShiftClicking = allowShiftClicking;
    }

    private void throwError(Throwable e) {
        e.printStackTrace();
    }

    private class GUIEvents implements Listener {

        private GUI gui;

        public GUIEvents(GUI gui) {
            this.gui = gui;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            try {
                if (this.gui.bukkitInventory.getViewers().contains(event.getWhoClicked())) {
                    List<InventoryAction> deniedActions = new ArrayList<>(Arrays.asList(
                            InventoryAction.CLONE_STACK,
                            InventoryAction.COLLECT_TO_CURSOR,
                            InventoryAction.UNKNOWN
                    ));

                    if (!allowShiftClicking) {
                        deniedActions.add(InventoryAction.MOVE_TO_OTHER_INVENTORY);
                    }

                    if (deniedActions.contains(event.getAction())) {
                        event.setCancelled(true);
                    }

                    if (!allowShiftClicking && event.getClick().isShiftClick()) {
                        event.setCancelled(true);
                    }
                }
                if (!invCheckOverride) {
                    event.getInventory();
                    if (!event.getInventory().equals(gui.getInventory())) return;
                }
                event.setCancelled(true);
                if (!(event.getWhoClicked() instanceof Player)) return;

                gui.onPlayerClick(event);
            } catch (Throwable e) {
                this.gui.throwError(e);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (!event.getInventory().equals(gui.getInventory())) return;
            if (bukkitInventory.getViewers().size() <= 1) {
                HandlerList.unregisterAll(this);
                try {
                    gui.onPlayerCloseInv();
                } catch (Throwable e) {
                    this.gui.throwError(e);
                }
                if (gui.updaterTask != null) {
                    gui.updaterTask.cancel();
                }
            }
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent event) {
            try {
                if (!event.getInventory().equals(gui.getInventory())) return;
                if (!allowDrag) {
                    event.setCancelled(true);
                } else {

                    gui.onPlayerDrag(event);

                }
            } catch (Throwable e) {
                this.gui.throwError(e);
            }
        }
    }

    private class GUIUpdateTask extends BukkitRunnable {

        private GUI gui;

        public GUIUpdateTask(GUI gui) {
            this.gui = gui;
        }

        public void run() {
            try {
                this.gui.repopulate();
                this.gui.onTickUpdate();
            } catch (Throwable e) {
                this.gui.throwError(e);
            }
        }
    }

    public class InventoryProxy implements Inventory {
        private final Inventory mainInventory;
        private final Inventory proxyInventory;

        private InventoryProxy(Inventory mainInventory, Inventory proxyInventory) {
            this.mainInventory = mainInventory;
            this.proxyInventory = proxyInventory;
        }

        public void apply() {
            this.mainInventory.setContents(this.proxyInventory.getContents());
        }

        public int getSize() {
            return proxyInventory.getSize();
        }

        public int getMaxStackSize() {
            return proxyInventory.getMaxStackSize();
        }

        public void setMaxStackSize(int i) {
            proxyInventory.setMaxStackSize(i);
        }

        public ItemStack getItem(int i) {
            return proxyInventory.getItem(i);
        }

        public void setItem(int i, ItemStack itemStack) {
            proxyInventory.setItem(i, itemStack);
        }

        public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
            return proxyInventory.addItem(itemStacks);
        }

        public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
            return proxyInventory.removeItem(itemStacks);
        }

        public ItemStack[] getContents() {
            return proxyInventory.getContents();
        }

        public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {
            proxyInventory.setContents(itemStacks);
        }

        public ItemStack[] getStorageContents() {
            return new ItemStack[0];
        }

        public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {

        }

        public boolean contains(Material material) throws IllegalArgumentException {
            return proxyInventory.contains(material);
        }

        public boolean contains(ItemStack itemStack) {
            return proxyInventory.contains(itemStack);
        }

        public boolean contains(Material material, int i) throws IllegalArgumentException {
            return proxyInventory.contains(material, i);
        }

        public boolean contains(ItemStack itemStack, int i) {
            return proxyInventory.contains(itemStack, i);
        }

        public boolean containsAtLeast(ItemStack itemStack, int i) {
            return proxyInventory.containsAtLeast(itemStack, i);
        }

        public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
            return proxyInventory.all(material);
        }

        public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
            return proxyInventory.all(itemStack);
        }

        public int first(Material material) throws IllegalArgumentException {
            return proxyInventory.first(material);
        }

        public int first(ItemStack itemStack) {
            return proxyInventory.first(itemStack);
        }

        public int firstEmpty() {
            return proxyInventory.firstEmpty();
        }

        @Override
        public boolean isEmpty() {
            return proxyInventory.isEmpty();
        }

        public void remove(Material material) throws IllegalArgumentException {
            proxyInventory.remove(material);
        }

        public void remove(ItemStack itemStack) {
            proxyInventory.remove(itemStack);
        }

        public void clear(int i) {
            proxyInventory.clear(i);
        }

        public void clear() {
            proxyInventory.clear();
        }

        public List<HumanEntity> getViewers() {
            return mainInventory.getViewers();
        }

        public InventoryType getType() {
            return mainInventory.getType();
        }

        public InventoryHolder getHolder() {
            return mainInventory.getHolder();
        }

        public ListIterator<ItemStack> iterator() {
            return proxyInventory.iterator();
        }

        public ListIterator<ItemStack> iterator(int i) {
            return proxyInventory.iterator(i);
        }

        @Override
        public Location getLocation() {
            return new Location(plugin.getServer().getWorld("world"), 0, 0, 0);
        }

    }
}
package net.chickensalad.survival.gui;

import net.chickensalad.survival.Survival;
import net.chickensalad.survival.gui.base.GUI;
import net.chickensalad.survival.manager.WarpPadManager;
import net.chickensalad.survival.objects.WarpPad;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.chickensalad.survival.manager.PlayerManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.enginehub.squirrelid.Profile;

import java.util.Arrays;
import java.util.Collections;

public class WarpEditGUI extends GUI {

    private final Survival plugin;
    private final PlayerManager playerManager;
    private final WarpPadManager warpPadManager;
    private final MenuFactory menuFactory;

    WarpPad pad;
    Player player;

    @Inject
    public WarpEditGUI(Survival plugin, PlayerManager playerManager, WarpPadManager warpPadManager,
                       MenuFactory menuFactory, @Assisted WarpPad pad, @Assisted Player player) {
        super("Edit Warp Pad", 36);
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.warpPadManager = warpPadManager;
        this.menuFactory = menuFactory;
        this.pad = pad;
        this.player = player;
    }

    @Override
    protected void onPlayerClick(InventoryClickEvent event) {
        if (event.getRawSlot() == 11) {
            this.close();
            new AnvilGUI(plugin, player, "New Name", (player, name) -> {
                WarpPad otherPad = warpPadManager.getWarpPads().stream()
                        .filter(warpPad -> warpPad.getOwner().equals(player.getUniqueId()))
                        .filter(warpPad -> warpPad.getName().equalsIgnoreCase(name))
                        .findFirst().orElse(null);
                if (otherPad != null) {
                    return "You already have a warp pad with this name!";
                }
                pad.setName(name);
                if (pad.isVisible()) {
                    pad.despawn();
                    pad.spawn();
                }
                this.scheduleOpen(menuFactory.newEditGui(pad, player), player);
                return null;
            });
            return;
        }

        if (event.getRawSlot() == 13) {
            int id = this.pad.getVisibility().ordinal();
            if ((id + 1) == WarpPad.Visibility.values().length) {
                id = 0;
            } else {
                id++;
            }
            int finalId = id;
            this.pad.setVisibility(Arrays.stream(WarpPad.Visibility.values()).filter(vis -> vis.ordinal() == finalId).findFirst().get());
            this.repopulate();
            return;
        }

        if (event.getRawSlot() == 15) {
            this.close();
            new AnvilGUI(plugin, player, "New Owner", (player, name) -> {
                Profile profile;
                try {
                    profile = playerManager.getProfileService().findByName(name);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error finding player profile!";
                }
                if (profile == null) {
                    return "Player not found";
                }

                pad.setOwner(profile.getUniqueId());
                this.scheduleOpen(menuFactory.newListGui(pad, player), player);
                return null;
            });
            return;
        }

        if (event.getRawSlot() == 31) {
            this.close();
            this.scheduleOpen(menuFactory.newListGui(pad, player), player);
        }
    }

    @Override
    protected void populate() {
        {
            ItemStack icon = new ItemStack(Material.NAME_TAG);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(ChatColor.GOLD + "Edit name");
            iconMeta.setLore(Collections.singletonList(ChatColor.GRAY + pad.getName()));
            icon.setItemMeta(iconMeta);
            this.inventory.setItem(11, icon);
        }

        {
            ItemStack icon = new ItemStack(Material.MAP);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(ChatColor.GOLD + "Change Viability");
            iconMeta.setLore(Collections.singletonList(ChatColor.GRAY + pad.getVisibility().getName()));
            icon.setItemMeta(iconMeta);
            this.inventory.setItem(13, icon);
        }

        {
            ItemStack icon = new ItemStack(Material.WITHER_SKELETON_SKULL);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(ChatColor.GOLD + "Edit Owner");
            iconMeta.setLore(Collections.singletonList(ChatColor.GRAY + Bukkit.getOfflinePlayer(pad.getOwner()).getName()));
            icon.setItemMeta(iconMeta);
            this.inventory.setItem(15, icon);
        }

        {
            ItemStack icon = new ItemStack(Material.ARROW);
            ItemMeta arrowMeta = icon.getItemMeta();
            arrowMeta.setDisplayName(ChatColor.WHITE + "<- Back");
            icon.setItemMeta(arrowMeta);
            this.inventory.setItem(31, icon);
        }
    }
}
package net.chickensalad.survival.gui;

import net.chickensalad.survival.gui.base.PagedGUI;
import net.chickensalad.survival.manager.PlayerManager;
import net.chickensalad.survival.manager.WarpPadManager;
import net.chickensalad.survival.objects.WarpPad;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarpListGUI extends PagedGUI {

    private final PlayerManager playerManager;
    private final WarpPadManager warpPadManager;
    private final MenuFactory menuFactory;

    WarpPad pad;
    Player player;

    @Inject
    public WarpListGUI(PlayerManager playerManager, WarpPadManager warpPadManager, MenuFactory menuFactory,
                       @Assisted WarpPad pad, @Assisted Player player) {
        super("Warp Pad List", 36);
        this.playerManager = playerManager;
        this.warpPadManager = warpPadManager;
        this.menuFactory = menuFactory;

        this.pad = pad;
        this.player = player;
    }

    @Override
    protected List<ItemStack> getIcons() {
        return warpPadManager.getWarpPads().stream()
                .filter(otherPad -> otherPad != pad)
                .filter(otherPad -> otherPad.canSee(player))
                .map(otherPad -> {
                    ItemStack icon = new ItemStack(Material.ENDER_EYE);
                    ItemMeta iconMeta = icon.getItemMeta();
                    assert iconMeta != null;
                    iconMeta.setDisplayName(ChatColor.WHITE + otherPad.getName());
                    iconMeta.setLore(Collections.singletonList(ChatColor.GRAY + playerManager.getProfileCache().getIfPresent(otherPad.getOwner()).getName()));
                    icon.setItemMeta(iconMeta);
                    return icon;
                }).collect(Collectors.toList());
    }

    @Override
    protected void onPlayerClickIcon(InventoryClickEvent event) {
        if (event.getRawSlot() == 35) {
            if (pad.getOwner().equals(player.getUniqueId())) {
                this.close();
                this.scheduleOpen(menuFactory.newEditGui(pad, player), player);
            }
        }

        ItemStack stack = event.getCurrentItem();
        if (stack == null || stack.getType() != Material.ENDER_EYE) {
            return;
        }

        String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
        UUID owner;
        try {
            owner = playerManager.getProfileService().findByName(ChatColor.stripColor(stack.getItemMeta().getLore().get(0))).getUniqueId();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        warpPadManager.getWarpPads().stream()
                .filter(otherPad -> otherPad != pad)
                .filter(otherPad -> otherPad.getOwner().equals(owner) && otherPad.getName().equals(name))
                .findFirst().ifPresent(otherPad -> {
            this.close();
            player.teleport(otherPad.getLocation().add(0.5, 0.5, 0.5));
        });
    }

    @Override
    protected void populateSpecial() {
        if (pad.getOwner().equals(player.getUniqueId())) {
            ItemStack editIcon = new ItemStack(Material.ANVIL);
            ItemMeta editIconMeta = editIcon.getItemMeta();
            assert editIconMeta != null;
            editIconMeta.setDisplayName(ChatColor.GOLD + "Edit this warp pad");
            editIcon.setItemMeta(editIconMeta);

            this.inventory.setItem(35, editIcon);
        }
    }
}

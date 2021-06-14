package net.chickensalad.survival.listener.listeners;

import net.chickensalad.survival.Survival;
import net.chickensalad.survival.manager.WarpPadManager;
import net.chickensalad.survival.objects.WarpPad;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {

    @Inject
    private Survival plugin;
    @Inject
    private WarpPadManager warpPadManager;

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
        WarpPad pad = warpPadManager.getWarpPads().stream().filter(warpPad ->
                warpPad.getLocation().getWorld() == event.getBlock().getWorld() &&
                warpPad.getLocation().getBlockX() == event.getBlock().getX() &&
                warpPad.getLocation().getBlockY() == event.getBlock().getY() &&
                warpPad.getLocation().getBlockZ() == event.getBlock().getZ())
                .findFirst().orElse(null);

        if (pad != null) {
            event.setCancelled(true);

            if (pad.getOwner().equals(event.getPlayer().getUniqueId()) || event.getPlayer().isOp()) {
                pad.despawn();

                assert pad.getLocation().getWorld() != null;
                pad.getLocation().getWorld().dropItemNaturally(pad.getLocation().add(0.5, 0.5, 0.5), WarpPad.HAND_ITEM.clone());

                warpPadManager.getWarpPads().remove(pad);
            }
        }
    }
//    @EventHandler
//    void onBlockPlace(BlockPlaceEvent event) {
//        if (WarpPad.HAND_ITEM.isSimilar(event.getItemInHand())) {
//            new AnvilGUI(plugin, event.getPlayer(), "Warp Name", (player, name) -> {
//                WarpPad pad = warpPadManager.getWarpPads().stream()
//                        .filter(warpPad -> warpPad.getOwner().equals(event.getPlayer().getUniqueId()))
//                        .filter(warpPad -> warpPad.getName().equalsIgnoreCase(name))
//                        .findFirst().orElse(null);
//                if (pad != null) {
//                    return "You already have a warp pad with this name!";
//                }
//                pad = new WarpPad(event.getPlayer().getUniqueId(), name, event.getBlockPlaced().getLocation());
//                pad.spawn();
//                warpPadManager.getWarpPads().add(pad);
//                return null;
//            });
//        }
//    }
}

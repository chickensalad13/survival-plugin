package net.chickensalad.survival.listener.listeners;

import net.chickensalad.survival.manager.WarpPadManager;
import net.chickensalad.survival.objects.WarpPad;
import com.google.inject.Inject;
import net.chickensalad.survival.gui.MenuFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Objects;

public class InteractListener implements Listener {

    private final WarpPadManager warpPadManager;
    private final MenuFactory menuFactory;

    @Inject
    public InteractListener(WarpPadManager warpPadManager, MenuFactory menuFactory) {
        this.warpPadManager = warpPadManager;
        this.menuFactory = menuFactory;
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        onPlayerInteractEntityReal(event); // Why
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        onPlayerInteractEntityReal(event); // Bukkit
    }

    @EventHandler
    public void onPlayerInteractArmorStand(PlayerArmorStandManipulateEvent event) {
        onPlayerInteractEntityReal(event); // Why
    }

    private void onPlayerInteractEntityReal(PlayerInteractEntityEvent event) {
        if (warpPadManager.getWarpPads().stream()
                .map(WarpPad::getStand)
                .filter(Objects::nonNull)
                .anyMatch(stand -> stand == event.getRightClicked())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onOffHandSwap(PlayerSwapHandItemsEvent event) {
        WarpPad pad = warpPadManager.getWarpPads().stream()
                .filter(WarpPad::isVisible)
                .filter(warpPad -> warpPad.getLocation().getWorld() == event.getPlayer().getWorld())
                .filter(warpPad -> warpPad.getLocation().add(0.5, 0.5, 0.5).distance(event.getPlayer().getLocation()) < 1.2)
                .findFirst().orElse(null);
        if (pad != null) {
            event.setCancelled(true);
            menuFactory.newListGui(pad, event.getPlayer()).open(event.getPlayer());
        }
    }
}

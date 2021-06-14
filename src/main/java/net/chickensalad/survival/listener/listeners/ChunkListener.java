package net.chickensalad.survival.listener.listeners;

import net.chickensalad.survival.manager.WarpPadManager;
import net.chickensalad.survival.objects.WarpPad;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @Inject
    private WarpPadManager warpPadManager;

    @EventHandler
    void onChunkLoad(ChunkLoadEvent event) {
        warpPadManager.getWarpPads().stream().filter(warpPad ->
                event.getChunk().getX() == warpPad.getLocation().getBlockX() >> 4 &&
                        event.getChunk().getZ() == warpPad.getLocation().getBlockZ() >> 4).forEach(WarpPad::spawn);
    }

    @EventHandler
    void onChunkUnload(ChunkUnloadEvent event) {
        warpPadManager.getWarpPads().stream().filter(warpPad ->
                event.getChunk().getX() == warpPad.getLocation().getBlockX() >> 4 &&
                        event.getChunk().getZ() == warpPad.getLocation().getBlockZ() >> 4).forEach(WarpPad::despawn);
    }
}

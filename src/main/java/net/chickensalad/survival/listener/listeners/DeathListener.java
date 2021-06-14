package net.chickensalad.survival.listener.listeners;

import net.chickensalad.survival.manager.WarpPadManager;
import net.chickensalad.survival.objects.SurvivalPlayer;
import net.chickensalad.survival.objects.WarpPad;
import com.google.inject.Inject;
import net.chickensalad.survival.manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Objects;

public class DeathListener implements Listener {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private WarpPadManager warpPadManager;

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (warpPadManager.getWarpPads().stream().map(WarpPad::getStand)
                .filter(Objects::nonNull)
                .anyMatch(stand -> stand == event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        System.out.println(event.getDeathMessage());
        event.setDeathMessage(null);

        final SurvivalPlayer player = playerManager.getPlayer(event.getEntity());
        player.setDeaths(player.getDeaths() + 1);

        if (event.getEntity().getKiller() != null) {
            final SurvivalPlayer killer = playerManager.getPlayer(event.getEntity().getKiller());

            killer.setKills(killer.getKills() + 1);
        }
    }
}
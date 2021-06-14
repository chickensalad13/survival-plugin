package net.chickensalad.survival.tasks;

import com.google.inject.Inject;
import net.chickensalad.survival.util.MessageParser;
import net.chickensalad.survival.manager.WarpPadManager;
import net.chickensalad.survival.objects.WarpPad;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TooltipTask extends BukkitRunnable {
    @Inject
    private WarpPadManager warpPadManager;
    @Inject
    private MessageParser messageParser;

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            TextComponent tooltip = null;
            if (warpPadManager.getWarpPads().stream()
                    .filter(WarpPad::isVisible)
                    .filter(warpPad -> warpPad.getLocation().getWorld() == player.getWorld())
                    .anyMatch(warpPad -> warpPad.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) < 1.2)) {
                tooltip = Component.text("Press ")
                        .color(NamedTextColor.AQUA)
                        .append(
                                Component.keybind("key.swapOffhand")
                                .decorate(TextDecoration.BOLD)
                        )
                        .append(Component.text(" to interact with the warp pad!"));
            }

            if (tooltip != null) {
                Audience audience = messageParser.adventure().player(player);
                audience.sendActionBar(tooltip);
            }
        });
    }
}

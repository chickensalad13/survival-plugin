package net.chickensalad.survival.command.teleport;

import com.google.inject.Inject;
import net.chickensalad.survival.command.AbstractCommand;
import net.chickensalad.survival.manager.PlayerManager;
import net.chickensalad.survival.objects.SurvivalPlayer;
import net.chickensalad.survival.util.MessageParser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TpdenyCommand extends AbstractCommand {

    @Inject
    protected TpdenyCommand(PlayerManager playerManager, MessageParser messageParser) {
        super("tpdeny", null, playerManager, messageParser);
    }

    @Override
    protected boolean logic(Player player, SurvivalPlayer survivalPlayer, Audience audience, String label, String[] args) {
        if (playerManager.getTeleportCache().getIfPresent(player.getUniqueId()) != null) {
            audience.sendMessage(Component.text("You have denied the teleport request.", NamedTextColor.YELLOW));

            Player target = Bukkit.getPlayer(Objects.requireNonNull(playerManager.getTeleportCache().getIfPresent(player.getUniqueId())));
            if (target != null) {
                messageParser.adventure().player(target).sendMessage(
                        Component.text(String.format("%s has denied your teleport request.", player.getName()), NamedTextColor.YELLOW));
            }

            playerManager.getTeleportCache().invalidate(player.getUniqueId());
            return true;
        } else {
            audience.sendMessage(Component.text("You have no pending teleport requests." +
                    " Has it been more than 120 seconds since your last request?", NamedTextColor.RED));
        }
        return true;
    }
}

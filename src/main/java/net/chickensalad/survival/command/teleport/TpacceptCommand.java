package net.chickensalad.survival.command.teleport;

import com.google.inject.Inject;
import net.chickensalad.survival.command.AbstractCommand;
import net.chickensalad.survival.manager.PlayerManager;
import net.chickensalad.survival.objects.SurvivalPlayer;
import net.chickensalad.survival.util.MessageParser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static java.util.Objects.requireNonNull;

public class TpacceptCommand extends AbstractCommand {

    @Inject
    protected TpacceptCommand(PlayerManager playerManager, MessageParser messageParser) {
        super("tpaccept", null, playerManager, messageParser);
    }

    @Override
    protected boolean logic(Player player, SurvivalPlayer survivalPlayer, Audience audience, String label, String[] args) {
        if (playerManager.getTeleportCache().getIfPresent(player.getUniqueId()) != null) {
            Player target = Bukkit.getPlayer(requireNonNull(playerManager.getTeleportCache().getIfPresent(player.getUniqueId())));

            if (target == null) {
                final TextComponent component = Component.text("The player that requested the teleport has went offline.")
                        .color(NamedTextColor.RED);
                audience.sendMessage(component);
                return true;
            } else {
                target.teleport(player);
                messageParser.adventure().player(target)
                        .sendMessage(Component.text(String.format("You have teleported to %s.", player.getName()))
                                .color(NamedTextColor.YELLOW));
                audience.sendMessage(Component.text(String.format("%s has teleported to you.", target.getName()))
                        .color(NamedTextColor.YELLOW));
                playerManager.getTeleportCache().invalidate(player.getUniqueId());
            }
        } else {
            audience.sendMessage(Component.text("You have no pending teleport requests. Has it been more than 120 seconds since your last request?")
                    .color(NamedTextColor.RED));
        }
        return true;
    }
}

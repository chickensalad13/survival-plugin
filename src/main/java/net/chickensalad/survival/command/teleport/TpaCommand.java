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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class TpaCommand extends AbstractCommand {

    @Inject
    protected TpaCommand(PlayerManager playerManager, MessageParser messageParser) {
        super("tpa", null, playerManager, messageParser);
    }

    @Override
    protected boolean logic(Player player, SurvivalPlayer survivalPlayer, Audience audience, String label, String[] args) {
        if (args.length == 0) {
            final TextComponent textComponent = Component.text("Invalid usage. /tpa <target>")
                    .color(NamedTextColor.RED);
            audience.sendMessage(textComponent);
            return true;
        }

        Player bukkitTarget = Bukkit.getPlayerExact(args[0]);
        if (bukkitTarget == null) {
            final TextComponent textComponent = Component.text(String.format("%s is not online."))
                    .color(NamedTextColor.RED);
            audience.sendMessage(textComponent);
            return true;
        } else if (bukkitTarget == player) {
            final TextComponent textComponent = Component.text("You cannot teleport to yourself.")
                    .color(NamedTextColor.RED);
            audience.sendMessage(textComponent);
            return true;
        }

        playerManager.getTeleportCache().put(bukkitTarget.getUniqueId(), player.getUniqueId());

        final TextComponent senderComponent = Component.text(String.format("Teleport request sent to %s", bukkitTarget.getName()))
                .color(NamedTextColor.YELLOW);
        audience.sendMessage(senderComponent);

        Stream.of(
                ChatColor.YELLOW + player.getName()  + " has requested to teleport to you!",
                ChatColor.YELLOW + "Use " + ChatColor.AQUA + "/tpaccept" + ChatColor.YELLOW + " to accept the request",
                ChatColor.YELLOW + "Use " + ChatColor.AQUA + "/tpdeny" + ChatColor.YELLOW + " to accept the request",
                ChatColor.YELLOW + "This request will expire in 120 seconds!"

        ).forEach(bukkitTarget::sendMessage);
        return true;
    }
}

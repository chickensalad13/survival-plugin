package net.chickensalad.survival.command.message;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.chickensalad.survival.command.AbstractCommand;
import net.chickensalad.survival.manager.PlayerManager;
import net.chickensalad.survival.objects.SurvivalPlayer;
import net.chickensalad.survival.util.MessageParser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Singleton
public class ReplyCommand extends AbstractCommand {

    @Inject
    protected ReplyCommand(PlayerManager playerManager, MessageParser messageParser) {
        super("reply", null, playerManager, messageParser);
    }

    @Override
    protected boolean logic(Player player, SurvivalPlayer survivalPlayer, Audience audience, String label, String[] args) {
        if (args.length < 1) {
            final TextComponent textComponent = Component.text("Please specify a reply message.")
                    .color(NamedTextColor.RED);
            audience.sendMessage(textComponent);
            return true;
        }

        if (survivalPlayer.getLastMessaged() == null) {
            final TextComponent textComponent = Component.text("You have nobody to reply to.")
                    .color(NamedTextColor.RED);
            audience.sendMessage(textComponent);
            return true;
        }

        Player target = Bukkit.getPlayer(survivalPlayer.getLastMessaged());
        if (target == null) {
            final TextComponent textComponent = Component.text(String.format("The player %s is now offline!", survivalPlayer.getLastMessaged()))
                    .color(NamedTextColor.RED);
            audience.sendMessage(textComponent);
            return true;
        }

        String message = Joiner.on(" ").join(args);

        final Component senderComponent = Component.text()
                .append(Component.text("You", NamedTextColor.BLUE, TextDecoration.BOLD))
                .append(Component.text(" -> ", NamedTextColor.DARK_AQUA))
                .append(Component.text(target.getName(), NamedTextColor.BLUE, TextDecoration.BOLD))
                .append(Component.text(" " + message, NamedTextColor.GRAY))
                .clickEvent(ClickEvent.suggestCommand("/msg " + target.getName() + " "))
                .build();

        final Component targetComponent = Component.text()
                .append(Component.text(player.getName(), NamedTextColor.BLUE, TextDecoration.BOLD))
                .append(Component.text(" -> ", NamedTextColor.DARK_AQUA))
                .append(Component.text("You", NamedTextColor.BLUE, TextDecoration.BOLD))
                .append(Component.text(" " + message, NamedTextColor.GRAY))
                .clickEvent(ClickEvent.suggestCommand("/msg " + player.getName() + " "))
                .build();

        audience.sendMessage(senderComponent);
        messageParser.adventure().player(target).sendMessage(targetComponent);
        return true;
    }
}

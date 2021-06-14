package net.chickensalad.survival.command;

import com.google.inject.Inject;
import net.chickensalad.survival.manager.PlayerManager;
import net.chickensalad.survival.objects.SurvivalPlayer;
import net.chickensalad.survival.util.MessageParser;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCommand implements CommandExecutor {
    private static final String NO_PERMS_MSG = ChatColor.RED + "You do not have permission to do that.";

    @Getter
    private final String name;
    private final String permission;

    protected final PlayerManager playerManager;
    protected final MessageParser messageParser;

    @Inject
    protected AbstractCommand(String name, String permission, PlayerManager playerManager, MessageParser messageParser) {
        this.name = name;
        this.permission = permission;
        this.playerManager = playerManager;
        this.messageParser = messageParser;
    }

    @Override
    public final boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        SurvivalPlayer survivalPlayer = playerManager.getPlayer(player);
        Audience audience = messageParser.adventure().player(player);

        if (this.permission != null && !sender.hasPermission(this.permission)) {
            sender.sendMessage(NO_PERMS_MSG);
            return true;
        }

        return this.logic(player, survivalPlayer, audience, label, args);
    }

    protected abstract boolean logic(Player player, SurvivalPlayer survivalPlayer, Audience audience, String label, String[] args);
}

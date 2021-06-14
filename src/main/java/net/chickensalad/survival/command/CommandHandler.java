package net.chickensalad.survival.command;

import net.chickensalad.survival.command.teleport.TpdenyCommand;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.chickensalad.survival.Survival;
import net.chickensalad.survival.command.message.MessageCommand;
import net.chickensalad.survival.command.message.ReplyCommand;
import net.chickensalad.survival.command.teleport.TpaCommand;
import net.chickensalad.survival.command.teleport.TpacceptCommand;
import net.chickensalad.survival.util.ArrayHelper;
import org.bukkit.command.PluginCommand;

@Singleton
public class CommandHandler {
    @Inject
    protected Injector injector;
    @Inject
    protected Survival plugin;

    final Class<? extends AbstractCommand>[] commands = ArrayHelper.create(
            MessageCommand.class, ReplyCommand.class,
            TpaCommand.class, TpacceptCommand.class, TpdenyCommand.class
    );

    public void registerCommands() {
        for (final Class<? extends AbstractCommand> commandClass : commands) {
            AbstractCommand abstractCommand = injector.getInstance(commandClass);

            PluginCommand pluginCommand = plugin.getCommand(abstractCommand.getName());
            if (pluginCommand != null) {
                pluginCommand.setExecutor(abstractCommand);
            }
        }
    }
}

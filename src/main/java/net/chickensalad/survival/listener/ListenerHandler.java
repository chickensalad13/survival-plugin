package net.chickensalad.survival.listener;

import chickensalad.survival.listener.listeners.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.chickensalad.survival.Survival;
import net.chickensalad.survival.listener.listeners.*;
import net.chickensalad.survival.util.ArrayHelper;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

@Singleton
public class ListenerHandler {
    @Inject
    protected Injector injector;
    @Inject
    protected Survival plugin;

    final Class<? extends Listener>[] listeners = ArrayHelper.create(
            BlockListener.class,
            ChatListener.class,
            ChunkListener.class,
            DeathListener.class,
            InteractListener.class,
            JoinListener.class
    );

    public void registerListeners() {
        PluginManager pm = plugin.getServer().getPluginManager();

        for (final Class<? extends Listener> listenerClass : listeners) {
            Listener listener = injector.getInstance(listenerClass);
            pm.registerEvents(listener, plugin);
        }
    }
}

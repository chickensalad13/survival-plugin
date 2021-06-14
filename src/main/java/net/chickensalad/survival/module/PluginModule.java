package net.chickensalad.survival.module;

import net.chickensalad.survival.Survival;
import com.google.inject.AbstractModule;

public class PluginModule extends AbstractModule {

    private final Survival plugin;

    public PluginModule(Survival plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        this.bind(Survival.class).toInstance(this.plugin);
    }
}

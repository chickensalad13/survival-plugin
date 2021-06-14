package net.chickensalad.survival.module;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.chickensalad.survival.gui.MenuFactory;

public class MenuModule extends AbstractModule {
    @Override
    protected void configure() {
        this.install(new FactoryModuleBuilder().build(MenuFactory.class));
    }
}

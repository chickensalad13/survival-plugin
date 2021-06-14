package net.chickensalad.survival;

import net.chickensalad.survival.command.CommandHandler;
import net.chickensalad.survival.listener.ListenerHandler;
import net.chickensalad.survival.manager.WarpPadManager;
import net.chickensalad.survival.tasks.TooltipTask;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.chickensalad.survival.manager.PlayerManager;
import net.chickensalad.survival.module.MenuModule;
import net.chickensalad.survival.module.PluginModule;
import net.chickensalad.survival.objects.WarpPad;
import net.chickensalad.survival.util.MessageParser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class Survival extends JavaPlugin {
    protected Injector injector;

    @Override
    public void onLoad() {
        PluginModule pluginModule = new PluginModule(this);
        MenuModule menuModule = new MenuModule();

        this.injector = Guice.createInjector(pluginModule, menuModule);
    }

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            if (!this.getDataFolder().mkdirs()) {
                throw new RuntimeException("Failed to create data directory!");
            }
        }

        this.injector.getInstance(MessageParser.class).enable();
        this.injector.getInstance(PlayerManager.class).loadCache();
        this.injector.getInstance(WarpPadManager.class).load();

        this.injector.getInstance(TooltipTask.class).runTaskTimerAsynchronously(this, 0, 5);

        this.injector.getInstance(ListenerHandler.class).registerListeners();
        this.injector.getInstance(CommandHandler.class).registerCommands();

        NamespacedKey namespacedKey = new NamespacedKey(this, "pad");
        this.getServer().addRecipe(new ShapedRecipe(namespacedKey, WarpPad.HAND_ITEM).shape(
                "E",
                "S"
        ).setIngredient('E', Material.ENDER_EYE).setIngredient('S', Material.LEGACY_STEP));
    }

    @Override
    public void onDisable() {
        this.injector.getInstance(MessageParser.class).disable();
        this.injector.getInstance(PlayerManager.class).saveData();

        WarpPadManager warpPadManager = this.injector.getInstance(WarpPadManager.class);
        warpPadManager.save();
        warpPadManager.getWarpPads().forEach(WarpPad::despawn);
    }
}

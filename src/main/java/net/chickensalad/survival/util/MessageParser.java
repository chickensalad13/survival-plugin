package net.chickensalad.survival.util;

import net.chickensalad.survival.Survival;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

@Singleton
public class MessageParser {

    @Getter
    private BukkitAudiences adventure;
    private final Survival plugin;

    @Inject
    public MessageParser(Survival plugin) {
        this.plugin = plugin;
    }

    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled.");
        }
        return this.adventure;
    }

    public final void enable() {
        this.adventure = BukkitAudiences.create(plugin);
    }

    public final void disable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}

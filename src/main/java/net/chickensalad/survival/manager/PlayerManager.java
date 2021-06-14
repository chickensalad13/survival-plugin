package net.chickensalad.survival.manager;

import net.chickensalad.survival.Survival;
import net.chickensalad.survival.objects.SurvivalPlayer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;
import org.enginehub.squirrelid.cache.ProfileCache;
import org.enginehub.squirrelid.cache.SQLiteCache;
import org.enginehub.squirrelid.resolver.CacheForwardingService;
import org.enginehub.squirrelid.resolver.HttpRepositoryService;
import org.enginehub.squirrelid.resolver.ProfileService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
@Log
public final class PlayerManager {

    @Getter
    private final Map<UUID, SurvivalPlayer> playerMap = Maps.newConcurrentMap();
    @Getter
    private final Cache<UUID, UUID> teleportCache = CacheBuilder.newBuilder()
            .expireAfterWrite(120, TimeUnit.SECONDS)
            .maximumSize(500)
            .build();

    private final Gson gson = new Gson();

    private final Survival plugin;

    @Getter
    private ProfileService profileService;
    @Getter
    private ProfileCache profileCache;

    @Inject
    public PlayerManager(Survival plugin) {
        this.plugin = plugin;
    }

    public SurvivalPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public SurvivalPlayer getPlayer(UUID uniqueId) {
        return playerMap.get(uniqueId);
    }

    public void loadCache() {
        try {
            profileCache = new SQLiteCache(new File(plugin.getDataFolder(), "cache.sqlite"));
            profileService = new CacheForwardingService(
                    HttpRepositoryService.forMinecraft(),
                    profileCache
            );
        } catch (IOException e) {
            log.severe("Error loading player name cache!");
            e.printStackTrace();
        }
    }

    public void saveData()  {
        try {
            for (SurvivalPlayer survivalPlayer : playerMap.values()) {
                try (FileWriter writer = new FileWriter(new File(plugin.getDataFolder(), survivalPlayer.getUniqueId().toString() + ".json"))) {
                    gson.toJson(survivalPlayer, writer);
                }
            }
        } catch (IOException e) {
            log.severe("Error saving user data!");
            e.printStackTrace();
        }
    }
}

package net.chickensalad.survival.listener.listeners;

import net.chickensalad.survival.Survival;
import net.chickensalad.survival.objects.SurvivalPlayer;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.chickensalad.survival.manager.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.enginehub.squirrelid.Profile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Stream;

@Singleton
public class JoinListener implements Listener {

    @Inject
    private Survival plugin;
    @Inject
    private PlayerManager playerManager;

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        SurvivalPlayer player = null;
        try {
            File loadFile = new File(plugin.getDataFolder(), event.getUniqueId().toString() + ".json");
            if (loadFile.exists()) {
                player = new Gson().fromJson(
                        new FileReader(loadFile),
                        SurvivalPlayer.class
                );
            }
        } catch (IOException ignored) {
        }

        if (player == null) {
            player = new SurvivalPlayer(event.getUniqueId(), event.getName());
        }

        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        playerManager.getProfileCache().put(new Profile(event.getUniqueId(), event.getName()));
        playerManager.getPlayerMap().put(event.getUniqueId(), player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player bukkitPlayer = event.getPlayer();
        SurvivalPlayer player = playerManager.getPlayer(bukkitPlayer);

        if (player == null) { // Fix for auto reconnect.
            AsyncPlayerPreLoginEvent fakeEvent = new AsyncPlayerPreLoginEvent(
                    bukkitPlayer.getName(),
                    bukkitPlayer.getAddress().getAddress(),
                    bukkitPlayer.getUniqueId()
            );

            this.onPreLogin(fakeEvent);

            if (fakeEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                bukkitPlayer.kickPlayer(fakeEvent.getKickMessage());
                return;
            }
            else {
                player = playerManager.getPlayer(bukkitPlayer);
            }
        }

        player.setLastJoin(new Date());

        // TODO: send MOTD message or somethin

        if (!player.isReceivedStarterKit()) {
            player.setReceivedStarterKit(true);
            bukkitPlayer.sendMessage(ChatColor.BLUE + "Since we see this is your first time, you have received the starter kit!");
            Stream.of(
                    new ItemStack(Material.STONE_PICKAXE),
                    new ItemStack(Material.STONE_AXE),
                    new ItemStack(Material.APPLE, 5)
            ).forEach(bukkitPlayer.getInventory()::addItem);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        SurvivalPlayer player = playerManager.getPlayerMap().remove(event.getPlayer().getUniqueId());
        try (FileWriter writer = new FileWriter(new File(plugin.getDataFolder(), event.getPlayer().getUniqueId() + ".json"))) {
            new Gson().toJson(
                    player,
                    writer
            );
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data of user " + player);
            e.printStackTrace();
        }
    }
}

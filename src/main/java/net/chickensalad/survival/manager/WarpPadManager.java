package net.chickensalad.survival.manager;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.chickensalad.survival.Survival;
import lombok.Getter;
import net.chickensalad.survival.objects.WarpPad;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Singleton
@Log
public class WarpPadManager {

    private final Survival plugin;

    @Getter
    private final List<WarpPad> warpPads = Lists.newArrayList();
    private final Gson gson = new Gson();

    @Inject
    public WarpPadManager(Survival plugin) {
        this.plugin = plugin;
    }

    public void load() {
        try {
            warpPads.clear();

            File data = new File(plugin.getDataFolder(), "pads.json");
            if (data.exists()) {
                try (FileReader reader = new FileReader(data)) {
                    Arrays.stream(gson.fromJson(reader, WarpPad[].class)).forEach(warpPad -> {
                        if (!warpPad.getLocation().getChunk().isLoaded()) {
                            warpPad.getLocation().getChunk().load();
                        }
                        warpPad.spawn();
                        warpPads.add(warpPad);
                    });
                }
            }
        } catch (IOException e) {
            log.severe("Error loading manager data!");
            e.printStackTrace();
        }
    }

    public void save()  {
        try {
            File data = new File(plugin.getDataFolder(), "pads.json");

            try (FileWriter writer = new FileWriter(data)) {
                gson.toJson(warpPads.toArray(new WarpPad[0]), writer);
            }
        } catch (IOException e) {
            log.severe("Error saving manager data!");
            e.printStackTrace();
        }
    }
}

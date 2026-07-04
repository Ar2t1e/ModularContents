package modularcontents.custom.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import modularcontents.ModularcontentsMod;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CustomItemManager {

    public static final Map<String, CustomItemInfo> CUSTOM_ITEMS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().create();

    public static void loadItems(File gameDir) {
        CUSTOM_ITEMS.clear();

        File rootPacksDir = new File(gameDir, "ModularContents");
        if (!rootPacksDir.exists()) return;

        File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
        if (packDirs == null) return;

        for (File packDir : packDirs) {
            File itemsDir = new File(packDir, "items");
            if (!itemsDir.exists() || !itemsDir.isDirectory()) continue;

            File[] jsonFiles = itemsDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (jsonFiles == null) continue;

            for (File file : jsonFiles) {
                try (FileReader reader = new FileReader(file)) {
                    CustomItemInfo info = GSON.fromJson(reader, CustomItemInfo.class);
                    if (info != null && info.id != null && !info.id.isEmpty()) {
                        CUSTOM_ITEMS.put(info.id, info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("[ModularContents] Loaded " + CUSTOM_ITEMS.size() + " custom items.");
    }
}
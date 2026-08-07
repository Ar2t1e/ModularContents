package modularcontents.custom.item;

import com.google.gson.Gson;
import modularcontents.custom.pack.PackState;
import modularcontents.custom.pack.PackZipUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CustomFuelManager {

    private static final Gson GSON = new Gson();
    private static final Map<String, Integer> FUELS = new HashMap<>();

    public static void loadFuels(File gameDir) {
        FUELS.clear();

        File rootPacksDir = new File(gameDir, "ModularContents");
        if (!rootPacksDir.exists()) return;

        for (File packDir : PackState.listPacks(rootPacksDir)) {
            File fuelsDir = new File(packDir, "fuels");
            if (!fuelsDir.exists() || !fuelsDir.isDirectory()) continue;

            File[] files = fuelsDir.listFiles((d, name) -> name.endsWith(".json"));
            if (files == null) continue;

            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    register(GSON.fromJson(reader, CustomFuelInfo.class));
                } catch (Exception e) {
                    System.err.println("[ModularContents] Failed to load fuel: " + file.getName());
                }
            }
        }

        PackZipUtils.loadJsonEntries(rootPacksDir, "fuels",
                (fileName, reader, packName) -> register(GSON.fromJson(reader, CustomFuelInfo.class)));

        if (!FUELS.isEmpty()) {
            System.out.println("[ModularContents] Loaded " + FUELS.size() + " custom fuel entries.");
        }
    }

    private static void register(CustomFuelInfo info) {
        if (info == null || info.item == null || info.item.isEmpty() || info.burnTime <= 0) return;
        FUELS.put(key(info.item, info.meta), info.burnTime);
    }

    public static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Item item = stack.getItem();
        ResourceLocation registryName = item.getRegistryName();
        if (registryName == null) return 0;

        String name = registryName.toString();
        Integer exact = FUELS.get(key(name, stack.getMetadata()));
        if (exact != null) return exact;

        Integer any = FUELS.get(key(name, -1));
        return any != null ? any : 0;
    }

    private static String key(String item, int meta) {
        return item + "@" + meta;
    }
}

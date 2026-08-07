package modularcontents.custom.item;

import modularcontents.custom.pack.PackState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Universal content manager that replaces CustomItemManager.
 * It scans subdirectories to load specialized POJOs for blocks, food, and items.
 */
public class CustomContentManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final Map<String, CustomItemInfo> CUSTOM_ITEMS = new HashMap<>();
    public static final Map<String, CustomBlockInfo> CUSTOM_BLOCKS = new LinkedHashMap<>();
    public static final Map<String, CustomFoodInfo> CUSTOM_FOODS = new HashMap<>();
    public static final Map<String, CustomWeaponInfo> CUSTOM_WEAPONS = new HashMap<>();
    public static final Map<String, CustomToolInfo> CUSTOM_TOOLS = new HashMap<>();
    public static final Map<String, CustomArmorInfo> CUSTOM_ARMORS = new HashMap<>();
    public static final Map<String, Integer> BLOCK_ORDER = new HashMap<>();

    public static void loadContent(File gameDir) {
        CUSTOM_ITEMS.clear();
        CUSTOM_BLOCKS.clear();
        CUSTOM_FOODS.clear();
        CUSTOM_WEAPONS.clear();
        CUSTOM_TOOLS.clear();
        CUSTOM_ARMORS.clear();
        BLOCK_ORDER.clear();

        File rootDir = new File(gameDir, "ModularContents");
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        File[] packs = PackState.listPacks(rootDir);
        if (packs != null) {
            for (File packDir : packs) {
                if (packDir.getName().equals("generated")) continue;

                // Load basic items
                loadJsonFiles(new File(packDir, "items"), CustomItemInfo.class, CUSTOM_ITEMS);

                // Load blocks
                loadJsonFiles(new File(packDir, "blocks"), CustomBlockInfo.class, CUSTOM_BLOCKS);

                // Load food
                loadJsonFiles(new File(packDir, "food"), CustomFoodInfo.class, CUSTOM_FOODS);
                // Load weapons
                loadJsonFiles(new File(packDir, "weapons"), CustomWeaponInfo.class, CUSTOM_WEAPONS);

                // Load tools
                loadJsonFiles(new File(packDir, "tools"), CustomToolInfo.class, CUSTOM_TOOLS);

                // Load armor
                loadJsonFiles(new File(packDir, "armor"), CustomArmorInfo.class, CUSTOM_ARMORS);
            }
        }
        System.out.println("[ModularContents] Loaded " + CUSTOM_ITEMS.size() + " items, " + CUSTOM_BLOCKS.size() + " blocks, " + CUSTOM_FOODS.size() + " foods, " + CUSTOM_WEAPONS.size() + " weapons, " + CustomContentManager.CUSTOM_TOOLS.size() + " tools, " + CustomContentManager.CUSTOM_ARMORS.size() + " armors.");
        generateVariants();
        LangGenerator.generateLangFiles(gameDir);
    }

    private static <T> void loadJsonFiles(File dir, Class<T> clazz, Map<String, T> map) {
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    T info = GSON.fromJson(reader, clazz);
                    // Extract ID via reflection or assumption
                    String id = null;
                    if (info instanceof CustomItemInfo) id = ((CustomItemInfo) info).id;
                    else if (info instanceof CustomBlockInfo) id = ((CustomBlockInfo) info).id;
                    else if (info instanceof CustomFoodInfo) id = ((CustomFoodInfo) info).id;
                    else if (info instanceof CustomWeaponInfo) id = ((CustomWeaponInfo) info).id;
                    else if (info instanceof CustomToolInfo) id = ((CustomToolInfo) info).id;
                    else if (info instanceof CustomArmorInfo) id = ((CustomArmorInfo) info).id;

                    if (id != null && !id.isEmpty()) {
                        map.put(id, info);
                    }
                } catch (Exception e) {
                    System.out.println("[ModularContents] Failed to load JSON: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private static final String[][] VARIANT_TYPES = {
            {"block", ""},
            {"stair", "_stairs"},
            {"slab", "_slab"},
            {"fence", "_fence"},
            {"wall", "_wall"},
            {"trapdoor", "_trapdoor"},
            {"door", "_door"},
            {"button", "_button"},
            {"pressure_plate", "_pressure_plate"}
    };

    private static int typeRank(String blockType) {
        for (int i = 0; i < VARIANT_TYPES.length; i++) {
            if (VARIANT_TYPES[i][0].equalsIgnoreCase(blockType)) return i;
        }
        return 0;
    }

    private static String baseId(CustomBlockInfo info) {
        int rank = typeRank(info.blockType);
        String suffix = VARIANT_TYPES[rank][1];
        if (!suffix.isEmpty() && info.id.toLowerCase().endsWith(suffix)) {
            return info.id.substring(0, info.id.length() - suffix.length());
        }
        return info.id;
    }

    private static void generateVariants() {
        Map<String, CustomBlockInfo> generated = new LinkedHashMap<>();
        for (CustomBlockInfo info : CUSTOM_BLOCKS.values()) {
            if (info.hasStairs) {
                CustomBlockInfo stair = cloneBlock(info, "_stairs", "stair");
                generated.put(stair.id, stair);
            }
            if (info.hasSlab) {
                CustomBlockInfo slab = cloneBlock(info, "_slab", "slab");
                generated.put(slab.id, slab);
            }
            if (info.hasFence) {
                CustomBlockInfo fence = cloneBlock(info, "_fence", "fence");
                generated.put(fence.id, fence);
            }
            if (info.hasWall) {
                CustomBlockInfo wall = cloneBlock(info, "_wall", "wall");
                generated.put(wall.id, wall);
            }
        }
        CUSTOM_BLOCKS.putAll(generated);
        sortBlocks();
    }

    private static void sortBlocks() {
        Map<String, Integer> baseOrder = new LinkedHashMap<>();
        List<CustomBlockInfo> all = new ArrayList<>(CUSTOM_BLOCKS.values());
        for (CustomBlockInfo info : all) {
            baseOrder.putIfAbsent(baseId(info), baseOrder.size());
        }
        all.sort((a, b) -> {
            int cmp = Integer.compare(typeRank(a.blockType), typeRank(b.blockType));
            if (cmp != 0) return cmp;
            cmp = Integer.compare(baseOrder.get(baseId(a)), baseOrder.get(baseId(b)));
            return cmp != 0 ? cmp : a.id.compareToIgnoreCase(b.id);
        });
        CUSTOM_BLOCKS.clear();
        BLOCK_ORDER.clear();
        for (CustomBlockInfo info : all) {
            CUSTOM_BLOCKS.put(info.id, info);
            BLOCK_ORDER.put(info.id, BLOCK_ORDER.size());
        }
    }

    private static CustomBlockInfo cloneBlock(CustomBlockInfo original, String suffix, String newType) {
        CustomBlockInfo clone = new CustomBlockInfo();
        clone.id = original.id + suffix;
        clone.displayName = original.displayName;
        clone.creativeTab = original.creativeTab;
        clone.material = original.material;
        clone.hardness = original.hardness;
        clone.resistance = original.resistance;
        clone.lightLevel = original.lightLevel;
        clone.toolClass = original.toolClass;
        clone.harvestLevel = original.harvestLevel;
        clone.texture = (original.texture != null && !original.texture.isEmpty()) ? original.texture : original.id;
        clone.blockType = newType;
        clone.biomeTint = original.biomeTint;
        clone.rotationType = original.rotationType;
        clone.textureTop = original.textureTop;
        clone.textureBottom = original.textureBottom;
        clone.textureFront = original.textureFront;
        clone.textureSide = original.textureSide;
        clone.boundingBoxes = original.boundingBoxes;
        return clone;
    }
}

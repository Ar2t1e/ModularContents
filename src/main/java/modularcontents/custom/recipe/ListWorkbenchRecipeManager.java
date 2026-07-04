package modularcontents.custom.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import modularcontents.custom.config.ModularContentsConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ListWorkbenchRecipeManager {
    private static final Logger LOGGER = LogManager.getLogger("ModularContents");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, ListWorkbenchRecipe> RECIPES = new HashMap<>();

    public static void setupDirectories(File gameDir) {
        File rootPacksDir = new File(gameDir, "ModularContents");
        if (!rootPacksDir.exists()) {
            rootPacksDir.mkdirs();
        }

        if (ModularContentsConfig.generateExamplePack && !hasAnyPack(rootPacksDir)) {
            createExamplePack(rootPacksDir);
        }
    }

    private static boolean hasAnyPack(File rootPacksDir) {
        File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
        if (packDirs != null) {
            for (File packDir : packDirs) {
                if (dirHasJsons(new File(packDir, "recipes"))) return true;
                if (dirHasJsons(new File(new File(packDir, "loot_tables"), "airdrops"))) return true;
            }
        }

        File[] zips = rootPacksDir.listFiles((d, name) -> isZipName(name));
        if (zips != null) {
            for (File zip : zips) {
                if (zipHasContent(zip)) return true;
            }
        }
        return false;
    }

    private static boolean dirHasJsons(File dir) {
        if (!dir.isDirectory()) return false;
        File[] jsons = dir.listFiles((d, name) -> name.endsWith(".json"));
        return jsons != null && jsons.length > 0;
    }

    private static boolean isZipName(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.endsWith(".zip");
    }

    private static boolean isRecipeEntry(String entryName) {
        String normalized = entryName.replace('\\', '/');
        String lower = normalized.toLowerCase(Locale.ROOT);
        return (lower.startsWith("recipes/") || lower.contains("/recipes/")) && lower.endsWith(".json");
    }

    private static boolean zipHasContent(File zipFile) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory() && (isRecipeEntry(entry.getName()) || isAirdropLootEntry(entry.getName()))) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read pack archive: " + zipFile.getName(), e);
        }
        return false;
    }

    public static boolean isAirdropLootEntry(String entryName) {
        String normalized = entryName.replace('\\', '/');
        String lower = normalized.toLowerCase(Locale.ROOT);
        return (lower.startsWith("loot_tables/airdrops/") || lower.contains("/loot_tables/airdrops/")) && lower.endsWith(".json");
    }

    public static void loadRecipes(File gameDir) {
        RECIPES.clear();

        File rootPacksDir = new File(gameDir, "ModularContents");
        if (!rootPacksDir.exists()) {
            rootPacksDir.mkdirs();
        }

        File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
        if (packDirs != null) {
            for (File packDir : packDirs) {
                File recipeDir = new File(packDir, "recipes");
                if (recipeDir.exists() && recipeDir.isDirectory()) {
                    loadRecipesFromDir(recipeDir, packDir.getName());
                }
            }
        }

        File[] zips = rootPacksDir.listFiles((d, name) -> isZipName(name));
        if (zips != null) {
            for (File zip : zips) {
                loadRecipesFromZip(zip);
            }
        }
    }

    private static void loadRecipesFromZip(File zipFile) {
        String packName = zipFile.getName();
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !isRecipeEntry(entry.getName())) {
                    continue;
                }
                try (Reader reader = new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8)) {
                    ListWorkbenchRecipe recipe = GSON.fromJson(reader, ListWorkbenchRecipe.class);
                    if (recipe != null && recipe.id != null) {
                        RECIPES.put(recipe.id, recipe);
                        LOGGER.info("Loaded custom workbench recipe '" + recipe.id + "' from pack '" + packName + "'");
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load recipe: " + entry.getName() + " in pack: " + packName, e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to open pack archive: " + packName, e);
        }
    }

    private static void loadRecipesFromDir(File recipeDir, String packName) {
        File[] files = recipeDir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    ListWorkbenchRecipe recipe = GSON.fromJson(reader, ListWorkbenchRecipe.class);
                    if (recipe != null && recipe.id != null) {
                        RECIPES.put(recipe.id, recipe);
                        LOGGER.info("Loaded custom workbench recipe '" + recipe.id + "' from pack '" + packName + "'");
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load recipe: " + file.getName() + " in pack: " + packName, e);
                }
            }
        }
    }

    private static final String[] EXAMPLE_PACK_RECIPES = {
            "example_aa_battery.json",
            "example_arrows.json",
            "example_nbt_sword.json",
            "vanilla_torches.json",
            "vanilla_book.json",
            "vanilla_fire_charge.json",
            "vanilla_cake.json",
            "vanilla_tnt.json",
            "vanilla_sticky_piston.json",
            "vanilla_explorer_map.json",
            "vanilla_beacon.json",
            "vanilla_enchanting_table.json",
            "vanilla_defense_kit.json",
            "vanilla_ore_processing.json"
    };

    private static final String[] EXAMPLE_PACK_LOOT_TABLES = {
            "basic_loot.json",
            "medical_loot.json",
            "military_loot.json"
    };

    private static void createExamplePack(File rootPacksDir) {
        try {
            File examplePackDir = new File(rootPacksDir, "example_pack");
            int recipes = copyExampleResources(new File(examplePackDir, "recipes"),
                    "/assets/modularcontents/example_pack/recipes/", EXAMPLE_PACK_RECIPES);
            int lootTables = copyExampleResources(new File(new File(examplePackDir, "loot_tables"), "airdrops"),
                    "/assets/modularcontents/example_pack/loot_tables/airdrops/", EXAMPLE_PACK_LOOT_TABLES);

            LOGGER.info("Created example content pack (" + recipes + " recipes, " + lootTables + " loot tables) in ModularContents/example_pack");
        } catch (Exception e) {
            LOGGER.error("Failed to create example content pack", e);
        }
    }

    private static int copyExampleResources(File targetDir, String resourceBasePath, String[] fileNames) throws Exception {
        targetDir.mkdirs();
        int copied = 0;
        for (String fileName : fileNames) {
            String resourcePath = resourceBasePath + fileName;
            try (InputStream in = ListWorkbenchRecipeManager.class.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    LOGGER.warn("Example pack resource missing in jar: " + resourcePath);
                    continue;
                }
                Files.copy(in, new File(targetDir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                copied++;
            }
        }
        return copied;
    }

    public static ListWorkbenchRecipe getRecipe(String id) {
        return RECIPES.get(id);
    }

    public static Collection<ListWorkbenchRecipe> getAllRecipes() {
        return RECIPES.values();
    }

    public static List<String> getCategories() {
        return RECIPES.values().stream()
                .map(r -> r.category != null && !r.category.isEmpty() ? r.category : "general")
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<ListWorkbenchRecipe> getRecipesInCategory(String category) {
        return RECIPES.values().stream()
                .filter(r -> {
                    String cat = r.category != null && !r.category.isEmpty() ? r.category : "general";
                    return cat.equalsIgnoreCase(category);
                })
                .collect(Collectors.toList());
    }
}

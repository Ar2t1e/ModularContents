package modularcontents.custom.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.FileReader;

public class CustomSmeltingManager {
    private static final Gson GSON = new GsonBuilder().create();

    public static void loadSmeltingRecipes(File rootDir) {
        File modularDir = new File(rootDir, "ModularContents");
        if (!modularDir.exists() || !modularDir.isDirectory()) return;

        File[] packs = modularDir.listFiles();
        if (packs == null) return;

        int count = 0;
        for (File packDir : packs) {
            if (!packDir.isDirectory() || packDir.getName().equals("generated")) continue;

            File smeltingDir = new File(packDir, "recipes/smelting");
            if (!smeltingDir.exists() || !smeltingDir.isDirectory()) continue;

            File[] files = smeltingDir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try (FileReader reader = new FileReader(file)) {
                        CustomSmeltingInfo info = GSON.fromJson(reader, CustomSmeltingInfo.class);
                        if (info != null && info.input != null && info.output != null) {
                            ItemStack inStack = info.input.toItemStack();
                            ItemStack outStack = info.output.toItemStack();
                            if (!inStack.isEmpty() && !outStack.isEmpty()) {
                                GameRegistry.addSmelting(inStack, outStack, info.xp);
                                count++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (count > 0) {
            System.out.println("[ModularContents] Loaded " + count + " vanilla smelting recipes.");
        }
    }
}

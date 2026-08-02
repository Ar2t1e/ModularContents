package modularcontents.custom.client;

import com.google.common.collect.ImmutableSet;
import modularcontents.custom.item.CustomContentManager;
import modularcontents.custom.pack.PackZipUtils;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ModularResourcePack implements IResourcePack {

    private final File rootPacksDir;
    private static final Set<String> DOMAINS = ImmutableSet.of("modularcontents");

    public ModularResourcePack(File gameDir) {
        this.rootPacksDir = new File(gameDir, "ModularContents");
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        String path = location.getResourcePath();

        // Try to load any requested resource directly from content packs first
        InputStream packStream = findPackResource(location);
        if (packStream != null) {
            return packStream;
        }

        // Texture intercept check (fallback for when requested by string internally)
        if (path.startsWith("textures/") && path.endsWith(".png")) {
            InputStream texStream = findTexture(path);
            if (texStream != null) return texStream;
        }

        // 0. Serve generated lang files
        if (path.startsWith("lang/") && path.endsWith(".lang")) {
            File langFile = new File(rootPacksDir, path);
            if (langFile.exists()) {
                return new FileInputStream(langFile);
            }
        }

        // 1. Intercept Model generation (So users don't have to write .json models for items)
        if (path.startsWith("models/item/") && path.endsWith(".json")) {
            String itemId = path.substring("models/item/".length(), path.length() - 5);
            if (CustomContentManager.CUSTOM_ITEMS.containsKey(itemId) || CustomContentManager.CUSTOM_FOODS.containsKey(itemId) || CustomContentManager.CUSTOM_WEAPONS.containsKey(itemId) || CustomContentManager.CUSTOM_TOOLS.containsKey(itemId) || CustomContentManager.CUSTOM_ARMORS.containsKey(itemId) || CustomContentManager.CUSTOM_BLOCKS.containsKey(itemId) || itemId.equals("custom_workbench") || modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(itemId) != null) {
                if (!itemId.equals("custom_workbench")) {
                    String generatedJson = "{\n  \"parent\": \"item/generated\",\n  \"textures\": {\n    \"layer0\": \"modularcontents:items/" + itemId + "\"\n  }\n}";
                    if (modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(itemId) != null || CustomContentManager.CUSTOM_BLOCKS.containsKey(itemId)) {
                         generatedJson = "{\n  \"parent\": \"modularcontents:block/" + itemId + "\"\n}";
                         if (CustomContentManager.CUSTOM_BLOCKS.containsKey(itemId)) {
                             modularcontents.custom.item.CustomBlockInfo bInfo = CustomContentManager.CUSTOM_BLOCKS.get(itemId);
                             if ("fence".equalsIgnoreCase(bInfo.blockType) || "wall".equalsIgnoreCase(bInfo.blockType)) {
                                 generatedJson = "{\n  \"parent\": \"modularcontents:block/" + itemId + "_inventory\"\n}";
                             }
                         }
                    }
                    System.out.println("[ModularContents] Generated model for item: " + itemId);
                    return new ByteArrayInputStream(generatedJson.getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        // Blockstates generation
        if (path.startsWith("blockstates/") && path.endsWith(".json")) {
            String blockId = path.substring("blockstates/".length(), path.length() - 5);
            boolean isDoubleSlab = blockId.endsWith("_double");
            String searchId = isDoubleSlab ? blockId.substring(0, blockId.length() - 7) : blockId;
            if (CustomContentManager.CUSTOM_BLOCKS.containsKey(searchId)) {
                modularcontents.custom.item.CustomBlockInfo info = CustomContentManager.CUSTOM_BLOCKS.get(searchId);
                String type = info.blockType != null ? info.blockType.toLowerCase() : "block";
                String generatedJson = "";
                if (isDoubleSlab) {
                    generatedJson = "{\n  \"variants\": {\n    \"normal\": { \"model\": \"modularcontents:" + searchId + "_double\" },\n    \"variant=default\": { \"model\": \"modularcontents:" + searchId + "_double\" }\n  }\n}";
                } else if (type.equals("stair")) {
                    generatedJson = "{\n  \"variants\": {\n    \"facing=east,half=bottom,shape=straight\":  { \"model\": \"modularcontents:" + blockId + "\" },\n    \"facing=west,half=bottom,shape=straight\":  { \"model\": \"modularcontents:" + blockId + "\", \"y\": 180 },\n    \"facing=south,half=bottom,shape=straight\": { \"model\": \"modularcontents:" + blockId + "\", \"y\": 90 },\n    \"facing=north,half=bottom,shape=straight\": { \"model\": \"modularcontents:" + blockId + "\", \"y\": 270 },\n    \"facing=east,half=bottom,shape=outer_right\":  { \"model\": \"modularcontents:" + blockId + "_outer\" },\n    \"facing=west,half=bottom,shape=outer_right\":  { \"model\": \"modularcontents:" + blockId + "_outer\", \"y\": 180 },\n    \"facing=south,half=bottom,shape=outer_right\": { \"model\": \"modularcontents:" + blockId + "_outer\", \"y\": 90 },\n    \"facing=north,half=bottom,shape=outer_right\": { \"model\": \"modularcontents:" + blockId + "_outer\", \"y\": 270 },\n    \"facing=east,half=bottom,shape=outer_left\":  { \"model\": \"modularcontents:" + blockId + "_outer\", \"y\": 270 },\n    \"facing=west,half=bottom,shape=outer_left\":  { \"model\": \"modularcontents:" + blockId + "_outer\", \"y\": 90 },\n    \"facing=south,half=bottom,shape=outer_left\": { \"model\": \"modularcontents:" + blockId + "_outer\" },\n    \"facing=north,half=bottom,shape=outer_left\": { \"model\": \"modularcontents:" + blockId + "_outer\", \"y\": 180 },\n    \"facing=east,half=bottom,shape=inner_right\":  { \"model\": \"modularcontents:" + blockId + "_inner\" },\n    \"facing=west,half=bottom,shape=inner_right\":  { \"model\": \"modularcontents:" + blockId + "_inner\", \"y\": 180 },\n    \"facing=south,half=bottom,shape=inner_right\": { \"model\": \"modularcontents:" + blockId + "_inner\", \"y\": 90 },\n    \"facing=north,half=bottom,shape=inner_right\": { \"model\": \"modularcontents:" + blockId + "_inner\", \"y\": 270 },\n    \"facing=east,half=bottom,shape=inner_left\":  { \"model\": \"modularcontents:" + blockId + "_inner\", \"y\": 270 },\n    \"facing=west,half=bottom,shape=inner_left\":  { \"model\": \"modularcontents:" + blockId + "_inner\", \"y\": 90 },\n    \"facing=south,half=bottom,shape=inner_left\": { \"model\": \"modularcontents:" + blockId + "_inner\" },\n    \"facing=north,half=bottom,shape=inner_left\": { \"model\": \"modularcontents:" + blockId + "_inner\", \"y\": 180 },\n    \"facing=east,half=top,shape=straight\":  { \"model\": \"modularcontents:" + blockId + "\", \"x\": 180 },\n    \"facing=west,half=top,shape=straight\":  { \"model\": \"modularcontents:" + blockId + "\", \"x\": 180, \"y\": 180 },\n    \"facing=south,half=top,shape=straight\": { \"model\": \"modularcontents:" + blockId + "\", \"x\": 180, \"y\": 90 },\n    \"facing=north,half=top,shape=straight\": { \"model\": \"modularcontents:" + blockId + "\", \"x\": 180, \"y\": 270 },\n    \"facing=east,half=top,shape=outer_right\":  { \"model\": \"modularcontents:" + blockId + "_outer\", \"x\": 180, \"y\": 90 },\n    \"facing=west,half=top,shape=outer_right\":  { \"model\": \"modularcontents:" + blockId + "_outer\", \"x\": 180, \"y\": 270 },\n    \"facing=south,half=top,shape=outer_right\": { \"model\": \"modularcontents:" + blockId + "_outer\", \"x\": 180, \"y\": 180 },\n    \"facing=north,half=top,shape=outer_right\": { \"model\": \"modularcontents:" + blockId + "_outer\", \"x\": 180 },\n    \"facing=east,half=top,shape=outer_left\":  { \"model\": \"modularcontents:" + blockId + "_outer\", \"x\": 180 },\n    \"facing=west,half=top,shape=outer_left\":  { \"model\": \"modularcontents:" + blockId + "_outer\", \"x\": 180, \"y\": 180 },\n    \"facing=south,half=top,shape=outer_left\": { \"model\": \"modularcontents:" + blockId + "_outer\", \"x\": 180, \"y\": 90 },\n    \"facing=north,half=top,shape=outer_left\": { \"model\": \"modularcontents:" + blockId + "_outer\", \"x\": 180, \"y\": 270 },\n    \"facing=east,half=top,shape=inner_right\":  { \"model\": \"modularcontents:" + blockId + "_inner\", \"x\": 180, \"y\": 90 },\n    \"facing=west,half=top,shape=inner_right\":  { \"model\": \"modularcontents:" + blockId + "_inner\", \"x\": 180, \"y\": 270 },\n    \"facing=south,half=top,shape=inner_right\": { \"model\": \"modularcontents:" + blockId + "_inner\", \"x\": 180, \"y\": 180 },\n    \"facing=north,half=top,shape=inner_right\": { \"model\": \"modularcontents:" + blockId + "_inner\", \"x\": 180 },\n    \"facing=east,half=top,shape=inner_left\":  { \"model\": \"modularcontents:" + blockId + "_inner\", \"x\": 180 },\n    \"facing=west,half=top,shape=inner_left\":  { \"model\": \"modularcontents:" + blockId + "_inner\", \"x\": 180, \"y\": 180 },\n    \"facing=south,half=top,shape=inner_left\": { \"model\": \"modularcontents:" + blockId + "_inner\", \"x\": 180, \"y\": 90 },\n    \"facing=north,half=top,shape=inner_left\": { \"model\": \"modularcontents:" + blockId + "_inner\", \"x\": 180, \"y\": 270 }\n  }\n}";
                } else if (type.equals("slab")) {
                    generatedJson = "{\n  \"variants\": {\n    \"half=bottom,variant=default\": { \"model\": \"modularcontents:" + blockId + "\" },\n    \"half=top,variant=default\": { \"model\": \"modularcontents:" + blockId + "_top\" }\n  }\n}";
                } else if (type.equals("fence")) {
                    generatedJson = "{\n  \"multipart\": [\n" +
                        "    { \"apply\": { \"model\": \"modularcontents:" + blockId + "_post\" } },\n" +
                        "    { \"when\": { \"north\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_side\", \"uvlock\": true } },\n" +
                        "    { \"when\": { \"east\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_side\", \"y\": 90, \"uvlock\": true } },\n" +
                        "    { \"when\": { \"south\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_side\", \"y\": 180, \"uvlock\": true } },\n" +
                        "    { \"when\": { \"west\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_side\", \"y\": 270, \"uvlock\": true } }\n" +
                        "  ]\n}";
                } else if (type.equals("wall")) {
                    generatedJson = "{\n  \"multipart\": [\n" +
                        "    { \"when\": { \"up\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_post\" } },\n" +
                        "    { \"when\": { \"north\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_side\", \"uvlock\": true } },\n" +
                        "    { \"when\": { \"east\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_side\", \"y\": 90, \"uvlock\": true } },\n" +
                        "    { \"when\": { \"south\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_side\", \"y\": 180, \"uvlock\": true } },\n" +
                        "    { \"when\": { \"west\": \"true\" }, \"apply\": { \"model\": \"modularcontents:" + blockId + "_side\", \"y\": 270, \"uvlock\": true } }\n" +
                        "  ]\n}";
                } else {
                    if ("horizontal".equalsIgnoreCase(info.rotationType) || "prop".equalsIgnoreCase(info.rotationType)) {
                        generatedJson = "{\n  \"variants\": {\n    \"facing=north\": { \"model\": \"modularcontents:" + blockId + "\" },\n    \"facing=south\": { \"model\": \"modularcontents:" + blockId + "\", \"y\": 180 },\n    \"facing=west\":  { \"model\": \"modularcontents:" + blockId + "\", \"y\": 270 },\n    \"facing=east\":  { \"model\": \"modularcontents:" + blockId + "\", \"y\": 90 }\n  }\n}";
                    } else if ("log".equalsIgnoreCase(info.rotationType)) {
                        generatedJson = "{\n  \"variants\": {\n    \"axis=y\": { \"model\": \"modularcontents:" + blockId + "\" },\n    \"axis=z\": { \"model\": \"modularcontents:" + blockId + "\", \"x\": 90 },\n    \"axis=x\": { \"model\": \"modularcontents:" + blockId + "\", \"x\": 90, \"y\": 90 },\n    \"axis=none\": { \"model\": \"modularcontents:" + blockId + "\" }\n  }\n}";
                    } else {
                        generatedJson = "{\n  \"variants\": {\n    \"normal\": { \"model\": \"modularcontents:" + blockId + "\" }\n  }\n}";
                    }
                }
                return new ByteArrayInputStream(generatedJson.getBytes(StandardCharsets.UTF_8));
            } else if (modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(blockId) != null) {
                String generatedJson = "{\n  \"variants\": {\n    \"crafting=false,facing=north\": { \"model\": \"modularcontents:" + blockId + "\" },\n    \"crafting=false,facing=south\": { \"model\": \"modularcontents:" + blockId + "\", \"y\": 180 },\n    \"crafting=false,facing=west\":  { \"model\": \"modularcontents:" + blockId + "\", \"y\": 270 },\n    \"crafting=false,facing=east\":  { \"model\": \"modularcontents:" + blockId + "\", \"y\": 90 },\n    \"crafting=true,facing=north\": { \"model\": \"modularcontents:" + blockId + "\" },\n    \"crafting=true,facing=south\": { \"model\": \"modularcontents:" + blockId + "\", \"y\": 180 },\n    \"crafting=true,facing=west\":  { \"model\": \"modularcontents:" + blockId + "\", \"y\": 270 },\n    \"crafting=true,facing=east\":  { \"model\": \"modularcontents:" + blockId + "\", \"y\": 90 }\n  }\n}";
                return new ByteArrayInputStream(generatedJson.getBytes(StandardCharsets.UTF_8));
            }
        }

        // Block models generation
        if (path.startsWith("models/block/tinted_")) {
            String blockId = path.substring("models/block/".length(), path.length() - 5);
            String generatedJson = "";
            switch (blockId) {
                case "tinted_half_slab": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#side\"}, \"elements\": [ {\"from\": [0, 0, 0], \"to\": [16, 8, 16], \"faces\": { \"down\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#bottom\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#top\", \"tintindex\": 0}, \"north\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"north\", \"tintindex\": 0}, \"south\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"west\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"east\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_stairs": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#side\"}, \"elements\": [ {\"from\": [0, 0, 0], \"to\": [16, 8, 16], \"faces\": { \"down\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#bottom\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#top\", \"tintindex\": 0}, \"north\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"north\", \"tintindex\": 0}, \"south\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"west\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"east\", \"tintindex\": 0} } }, {\"from\": [8, 8, 0], \"to\": [16, 16, 16], \"faces\": { \"up\": {\"uv\": [8, 0, 16, 16], \"texture\": \"#top\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [8, 0, 16, 8], \"texture\": \"#side\", \"cullface\": \"north\", \"tintindex\": 0}, \"south\": {\"uv\": [8, 0, 16, 8], \"texture\": \"#side\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 0, 16, 8], \"texture\": \"#side\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 0, 16, 8], \"texture\": \"#side\", \"cullface\": \"east\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_inner_stairs": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#side\"}, \"elements\": [ {\"from\": [0, 0, 0], \"to\": [16, 8, 16], \"faces\": { \"down\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#bottom\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#top\", \"tintindex\": 0}, \"north\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"north\", \"tintindex\": 0}, \"south\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"west\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"east\", \"tintindex\": 0} } }, {\"from\": [8, 8, 0], \"to\": [16, 16, 16], \"faces\": { \"up\": {\"uv\": [8, 0, 16, 16], \"texture\": \"#top\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [8, 0, 16, 8], \"texture\": \"#side\", \"cullface\": \"north\", \"tintindex\": 0}, \"south\": {\"uv\": [8, 0, 16, 8], \"texture\": \"#side\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 0, 16, 8], \"texture\": \"#side\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 0, 16, 8], \"texture\": \"#side\", \"cullface\": \"east\", \"tintindex\": 0} } }, {\"from\": [0, 8, 8], \"to\": [8, 16, 16], \"faces\": { \"up\": {\"uv\": [0, 8, 8, 16], \"texture\": \"#top\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [0, 0, 8, 8], \"texture\": \"#side\", \"tintindex\": 0}, \"south\": {\"uv\": [0, 0, 8, 8], \"texture\": \"#side\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [8, 0, 16, 8], \"texture\": \"#side\", \"cullface\": \"west\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 0, 8, 8], \"texture\": \"#side\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_outer_stairs": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#side\"}, \"elements\": [ {\"from\": [0, 0, 0], \"to\": [16, 8, 16], \"faces\": { \"down\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#bottom\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#top\", \"tintindex\": 0}, \"north\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"north\", \"tintindex\": 0}, \"south\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"west\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 8, 16, 16], \"texture\": \"#side\", \"cullface\": \"east\", \"tintindex\": 0} } }, {\"from\": [8, 8, 8], \"to\": [16, 16, 16], \"faces\": { \"up\": {\"uv\": [8, 8, 16, 16], \"texture\": \"#top\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [8, 0, 16, 8], \"texture\": \"#side\", \"tintindex\": 0}, \"south\": {\"uv\": [8, 0, 16, 8], \"texture\": \"#side\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [8, 0, 16, 8], \"texture\": \"#side\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 0, 8, 8], \"texture\": \"#side\", \"cullface\": \"east\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_fence_post": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#texture\"}, \"elements\": [ {\"from\": [6, 0, 6], \"to\": [10, 16, 10], \"faces\": { \"down\": {\"uv\": [6, 6, 10, 10], \"texture\": \"#texture\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [6, 6, 10, 10], \"texture\": \"#texture\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [6, 0, 10, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"south\": {\"uv\": [6, 0, 10, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"west\": {\"uv\": [6, 0, 10, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"east\": {\"uv\": [6, 0, 10, 16], \"texture\": \"#texture\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_fence_side": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#texture\"}, \"elements\": [ {\"from\": [7, 12, 10], \"to\": [9, 15, 16], \"faces\": { \"down\": {\"uv\": [7, 0, 9, 6], \"texture\": \"#texture\", \"tintindex\": 0}, \"up\": {\"uv\": [7, 10, 9, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"north\": {\"uv\": [7, 1, 9, 4], \"texture\": \"#texture\", \"tintindex\": 0}, \"south\": {\"uv\": [7, 1, 9, 4], \"texture\": \"#texture\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 1, 6, 4], \"texture\": \"#texture\", \"tintindex\": 0}, \"east\": {\"uv\": [10, 1, 16, 4], \"texture\": \"#texture\", \"tintindex\": 0} } }, {\"from\": [7, 6, 10], \"to\": [9, 9, 16], \"faces\": { \"down\": {\"uv\": [7, 0, 9, 6], \"texture\": \"#texture\", \"tintindex\": 0}, \"up\": {\"uv\": [7, 10, 9, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"north\": {\"uv\": [7, 7, 9, 10], \"texture\": \"#texture\", \"tintindex\": 0}, \"south\": {\"uv\": [7, 7, 9, 10], \"texture\": \"#texture\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 7, 6, 10], \"texture\": \"#texture\", \"tintindex\": 0}, \"east\": {\"uv\": [10, 7, 16, 10], \"texture\": \"#texture\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_fence_inventory": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#texture\"}, \"elements\": [ {\"from\": [6, 0, 0], \"to\": [10, 16, 4], \"faces\": { \"down\": {\"uv\": [6, 0, 10, 4], \"texture\": \"#texture\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [6, 0, 10, 4], \"texture\": \"#texture\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [6, 0, 10, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"south\": {\"uv\": [6, 0, 10, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 0, 4, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"east\": {\"uv\": [12, 0, 16, 16], \"texture\": \"#texture\", \"tintindex\": 0} } }, {\"from\": [6, 0, 12], \"to\": [10, 16, 16], \"faces\": { \"down\": {\"uv\": [6, 12, 10, 16], \"texture\": \"#texture\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [6, 12, 10, 16], \"texture\": \"#texture\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [6, 0, 10, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"south\": {\"uv\": [6, 0, 10, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"west\": {\"uv\": [12, 0, 16, 16], \"texture\": \"#texture\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 0, 4, 16], \"texture\": \"#texture\", \"tintindex\": 0} } }, {\"from\": [7, 12, 4], \"to\": [9, 15, 12], \"faces\": { \"down\": {\"uv\": [7, 4, 9, 12], \"texture\": \"#texture\", \"tintindex\": 0}, \"up\": {\"uv\": [7, 4, 9, 12], \"texture\": \"#texture\", \"tintindex\": 0}, \"west\": {\"uv\": [4, 1, 12, 4], \"texture\": \"#texture\", \"tintindex\": 0}, \"east\": {\"uv\": [4, 1, 12, 4], \"texture\": \"#texture\", \"tintindex\": 0} } }, {\"from\": [7, 6, 4], \"to\": [9, 9, 12], \"faces\": { \"down\": {\"uv\": [7, 4, 9, 12], \"texture\": \"#texture\", \"tintindex\": 0}, \"up\": {\"uv\": [7, 4, 9, 12], \"texture\": \"#texture\", \"tintindex\": 0}, \"west\": {\"uv\": [4, 7, 12, 10], \"texture\": \"#texture\", \"tintindex\": 0}, \"east\": {\"uv\": [4, 7, 12, 10], \"texture\": \"#texture\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_wall_post": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#wall\"}, \"elements\": [ {\"from\": [4, 0, 4], \"to\": [12, 16, 12], \"faces\": { \"down\": {\"uv\": [4, 4, 12, 12], \"texture\": \"#wall\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [4, 4, 12, 12], \"texture\": \"#wall\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [4, 0, 12, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"south\": {\"uv\": [4, 0, 12, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"west\": {\"uv\": [4, 0, 12, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"east\": {\"uv\": [4, 0, 12, 16], \"texture\": \"#wall\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_wall_side": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#wall\"}, \"elements\": [ {\"from\": [5, 0, 12], \"to\": [11, 14, 16], \"faces\": { \"down\": {\"uv\": [5, 0, 11, 4], \"texture\": \"#wall\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [5, 12, 11, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"north\": {\"uv\": [5, 2, 11, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"south\": {\"uv\": [5, 2, 11, 16], \"texture\": \"#wall\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 2, 4, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"east\": {\"uv\": [12, 2, 16, 16], \"texture\": \"#wall\", \"tintindex\": 0} } } ] }"; break;
                case "tinted_wall_inventory": generatedJson = "{ \"parent\": \"block/block\", \"textures\": {\"particle\": \"#wall\"}, \"elements\": [ {\"from\": [4, 0, 4], \"to\": [12, 16, 12], \"faces\": { \"down\": {\"uv\": [4, 4, 12, 12], \"texture\": \"#wall\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [4, 4, 12, 12], \"texture\": \"#wall\", \"cullface\": \"up\", \"tintindex\": 0}, \"north\": {\"uv\": [4, 0, 12, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"south\": {\"uv\": [4, 0, 12, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"west\": {\"uv\": [4, 0, 12, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"east\": {\"uv\": [4, 0, 12, 16], \"texture\": \"#wall\", \"tintindex\": 0} } }, {\"from\": [5, 0, 0], \"to\": [11, 14, 4], \"faces\": { \"down\": {\"uv\": [5, 0, 11, 4], \"texture\": \"#wall\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [5, 0, 11, 4], \"texture\": \"#wall\", \"tintindex\": 0}, \"north\": {\"uv\": [5, 2, 11, 16], \"texture\": \"#wall\", \"cullface\": \"north\", \"tintindex\": 0}, \"west\": {\"uv\": [12, 2, 16, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"east\": {\"uv\": [0, 2, 4, 16], \"texture\": \"#wall\", \"tintindex\": 0} } }, {\"from\": [5, 0, 12], \"to\": [11, 14, 16], \"faces\": { \"down\": {\"uv\": [5, 12, 11, 16], \"texture\": \"#wall\", \"cullface\": \"down\", \"tintindex\": 0}, \"up\": {\"uv\": [5, 12, 11, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"south\": {\"uv\": [5, 2, 11, 16], \"texture\": \"#wall\", \"cullface\": \"south\", \"tintindex\": 0}, \"west\": {\"uv\": [0, 2, 4, 16], \"texture\": \"#wall\", \"tintindex\": 0}, \"east\": {\"uv\": [12, 2, 16, 16], \"texture\": \"#wall\", \"tintindex\": 0} } } ] }"; break;
            }
            if (!generatedJson.isEmpty()) {
                return new ByteArrayInputStream(generatedJson.getBytes(StandardCharsets.UTF_8));
            }
        }

        if (path.startsWith("models/block/") && path.endsWith(".json")) {
            String blockId = path.substring("models/block/".length(), path.length() - 5);
            boolean isDoubleSlabModel = blockId.endsWith("_double");
            String baseId = blockId.replace("_top_rh", "").replace("_bottom_rh", "").replace("_bottom", "").replace("_top", "").replace("_inner", "").replace("_outer", "").replace("_double", "").replace("_post", "").replace("_side", "").replace("_inventory", "").replace("_pressed", "").replace("_down", "").replace("_open", "");
            if (CustomContentManager.CUSTOM_BLOCKS.containsKey(baseId)) {
                modularcontents.custom.item.CustomBlockInfo info = CustomContentManager.CUSTOM_BLOCKS.get(baseId);
                String tex = info.texture != null && !info.texture.isEmpty() ? info.texture : baseId;
                if (baseId.endsWith("_slab") && tex.equals(baseId)) tex = baseId.substring(0, baseId.length() - 5);

                boolean hasTop = info.textureTop != null && !info.textureTop.isEmpty();
                boolean hasBottom = info.textureBottom != null && !info.textureBottom.isEmpty();
                boolean hasFront = info.textureFront != null && !info.textureFront.isEmpty();
                boolean hasSide = info.textureSide != null && !info.textureSide.isEmpty();

                String tTop = hasTop ? "modularcontents:blocks/" + info.textureTop : "modularcontents:blocks/" + tex;
                String tBottom = hasBottom ? "modularcontents:blocks/" + info.textureBottom : "modularcontents:blocks/" + tex;
                String tSide = hasSide ? "modularcontents:blocks/" + info.textureSide : "modularcontents:blocks/" + tex;
                String tFront = hasFront ? "modularcontents:blocks/" + info.textureFront : tSide;

                String texPath = "modularcontents:blocks/" + tex;
                String generatedJson = "";

                if (isDoubleSlabModel) {
                    if (hasTop || hasBottom || hasFront || hasSide) {
                         generatedJson = "{\n  \"parent\": \"block/orientable\",\n  \"textures\": {\n" +
                             "    \"top\": \"" + tTop + "\",\n" +
                             "    \"bottom\": \"" + tBottom + "\",\n" +
                             "    \"front\": \"" + tFront + "\",\n" +
                             "    \"side\": \"" + tSide + "\"\n" +
                             "  }\n}";
                    } else {
                        String parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "block/leaves" : "block/cube_all";
                        generatedJson = "{\n  \"parent\": \"" + parent + "\",\n  \"textures\": {\n    \"all\": \"" + texPath + "\"\n  }\n}";
                    }
                } else if (blockId.endsWith("_inner")) {
                    generatedJson = "{\n  \"parent\": \"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_inner_stairs" : "block/inner_stairs") + "\",\n  \"textures\": {\n    \"bottom\": \"" + tBottom + "\",\n    \"top\": \"" + tTop + "\",\n    \"side\": \"" + tSide + "\"\n  }\n}";
                } else if (blockId.endsWith("_outer")) {
                    generatedJson = "{\n  \"parent\": \"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_outer_stairs" : "block/outer_stairs") + "\",\n  \"textures\": {\n    \"bottom\": \"" + tBottom + "\",\n    \"top\": \"" + tTop + "\",\n    \"side\": \"" + tSide + "\"\n  }\n}";
                } else if (blockId.endsWith("_top")) {
                    generatedJson = "{\n  \"parent\": \"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_upper_slab" : "block/upper_slab") + "\",\n  \"textures\": {\n    \"bottom\": \"" + tBottom + "\",\n    \"top\": \"" + tTop + "\",\n    \"side\": \"" + tSide + "\"\n  }\n}";
                } else if (blockId.endsWith("_post")) {
                    String parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_fence_post" : "block/fence_post";
                    if ("wall".equalsIgnoreCase(info.blockType)) parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_wall_post" : "block/wall_post";
                    generatedJson = "{\n  \"parent\": \"" + parent + "\",\n  \"textures\": {\n    \"texture\": \"" + texPath + "\",\n    \"wall\": \"" + texPath + "\"\n  }\n}";
                } else if (blockId.endsWith("_side")) {
                    String parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_fence_side" : "block/fence_side";
                    if ("wall".equalsIgnoreCase(info.blockType)) parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_wall_side" : "block/wall_side";
                    generatedJson = "{\n  \"parent\": \"" + parent + "\",\n  \"textures\": {\n    \"texture\": \"" + texPath + "\",\n    \"wall\": \"" + texPath + "\"\n  }\n}";
                } else if (blockId.endsWith("_inventory")) {
                    String parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_fence_inventory" : "block/fence_inventory";
                    if ("wall".equalsIgnoreCase(info.blockType)) parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_wall_inventory" : "block/wall_inventory";
                    generatedJson = "{\n  \"parent\": \"" + parent + "\",\n  \"textures\": {\n    \"texture\": \"" + texPath + "\",\n    \"wall\": \"" + texPath + "\"\n  }\n}";
                } else {
                    String type = info.blockType != null ? info.blockType.toLowerCase() : "block";
                    if (type.equals("stair")) {
                        generatedJson = "{\n  \"parent\": \"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_stairs" : "block/stairs") + "\",\n  \"textures\": {\n    \"bottom\": \"" + tBottom + "\",\n    \"top\": \"" + tTop + "\",\n    \"side\": \"" + tSide + "\"\n  }\n}";
                    } else if (type.equals("slab")) {
                        if (isDoubleSlabModel) {
                            if (hasTop || hasBottom || hasFront || hasSide) {
                                 generatedJson = "{\n  \"parent\": \"block/orientable\",\n  \"textures\": {\n" +
                                     "    \"top\": \"" + tTop + "\",\n" +
                                     "    \"bottom\": \"" + tBottom + "\",\n" +
                                     "    \"front\": \"" + tFront + "\",\n" +
                                     "    \"side\": \"" + tSide + "\"\n" +
                                     "  }\n}";
                            } else {
                                String parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "block/leaves" : "block/cube_all";
                        generatedJson = "{\n  \"parent\": \"" + parent + "\",\n  \"textures\": {\n    \"all\": \"" + texPath + "\"\n  }\n}";
                            }
                        } else {
                            generatedJson = "{\n  \"parent\": \"" + ((info.biomeTint != null && !info.biomeTint.isEmpty()) ? "modularcontents:block/tinted_half_slab" : "block/half_slab") + "\",\n  \"textures\": {\n    \"bottom\": \"" + tBottom + "\",\n    \"top\": \"" + tTop + "\",\n    \"side\": \"" + tSide + "\"\n  }\n}";
                        }
                    } else {
                        if (hasTop || hasBottom || hasFront || hasSide) {
                             generatedJson = "{\n  \"parent\": \"block/orientable\",\n  \"textures\": {\n" +
                                 "    \"top\": \"" + tTop + "\",\n" +
                                 "    \"bottom\": \"" + tBottom + "\",\n" +
                                 "    \"front\": \"" + tFront + "\",\n" +
                                 "    \"side\": \"" + tSide + "\"\n" +
                                 "  }\n}";
                        } else {
                            String parent = (info.biomeTint != null && !info.biomeTint.isEmpty()) ? "block/leaves" : "block/cube_all";
                        generatedJson = "{\n  \"parent\": \"" + parent + "\",\n  \"textures\": {\n    \"all\": \"" + texPath + "\"\n  }\n}";
                        }
                    }
                }
                return new ByteArrayInputStream(generatedJson.getBytes(StandardCharsets.UTF_8));
            } else if (modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(blockId) != null) {
                 modularcontents.custom.pack.WorkbenchConfig config = modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(blockId);
                 String generatedJson = "";
                 String tex = config.texture != null && !config.texture.isEmpty() ? config.texture : blockId;

                 boolean hasTop = config.textureTop != null && !config.textureTop.isEmpty();
                 boolean hasBottom = config.textureBottom != null && !config.textureBottom.isEmpty();
                 boolean hasFront = config.textureFront != null && !config.textureFront.isEmpty();
                 boolean hasSide = config.textureSide != null && !config.textureSide.isEmpty();

                 if (hasTop || hasBottom || hasFront || hasSide) {
                     String tTop = hasTop ? config.textureTop : tex;
                     String tBottom = hasBottom ? config.textureBottom : tex;
                     String tFront = hasFront ? config.textureFront : (hasSide ? config.textureSide : tex);
                     String tSide = hasSide ? config.textureSide : tex;

                     generatedJson = "{\n  \"parent\": \"block/orientable\",\n  \"textures\": {\n" +
                             "    \"top\": \"modularcontents:blocks/" + tTop + "\",\n" +
                             "    \"bottom\": \"modularcontents:blocks/" + tBottom + "\",\n" +
                             "    \"front\": \"modularcontents:blocks/" + tFront + "\",\n" +
                             "    \"side\": \"modularcontents:blocks/" + tSide + "\"\n" +
                             "  }\n}";
                 } else {
                     generatedJson = "{\n  \"parent\": \"block/cube_all\",\n  \"textures\": {\n    \"all\": \"modularcontents:blocks/" + tex + "\"\n  }\n}";
                 }

                 return new ByteArrayInputStream(generatedJson.getBytes(StandardCharsets.UTF_8));
            }
        }

        throw new IOException("Resource not found: " + location);
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        String path = location.getResourcePath();

        // 0. Serve any file directly from pack if it exists
        if (packResourceExists(location)) {
            return true;
        }

        // 0. Serve generated lang files
        if (path.startsWith("lang/") && path.endsWith(".lang")) {
            File langFile = new File(rootPacksDir, path);
            if (langFile.exists()) {
                return true;
            }
        }

        // Model intercept check
        if (path.startsWith("models/item/") && path.endsWith(".json")) {
            String itemId = path.substring("models/item/".length(), path.length() - 5);
            if (CustomContentManager.CUSTOM_ITEMS.containsKey(itemId) || CustomContentManager.CUSTOM_FOODS.containsKey(itemId) || CustomContentManager.CUSTOM_WEAPONS.containsKey(itemId) || CustomContentManager.CUSTOM_TOOLS.containsKey(itemId) || CustomContentManager.CUSTOM_ARMORS.containsKey(itemId) || CustomContentManager.CUSTOM_BLOCKS.containsKey(itemId) || itemId.equals("custom_workbench") || modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(itemId) != null) {
                if (!itemId.equals("custom_workbench")) {
                    return true;
                }
            }
        }

        if (path.startsWith("blockstates/") && path.endsWith(".json")) {
            String blockId = path.substring("blockstates/".length(), path.length() - 5);
            String searchId = blockId.replace("_double", "");
            if (CustomContentManager.CUSTOM_BLOCKS.containsKey(searchId) || modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(searchId) != null) return true;
        }

        if (path.startsWith("models/block/") && path.endsWith(".json")) {
            String blockId = path.substring("models/block/".length(), path.length() - 5);
            if (blockId.startsWith("tinted_")) return true;
            boolean isDoubleSlabModel = blockId.endsWith("_double");
            String baseId = blockId.replace("_top", "").replace("_inner", "").replace("_outer", "").replace("_double", "").replace("_post", "").replace("_side", "").replace("_inventory", "");
            if (CustomContentManager.CUSTOM_BLOCKS.containsKey(baseId) || modularcontents.custom.pack.CustomWorkbenchManager.getWorkbench(baseId) != null) return true;
        }

        // Texture intercept check
        if (path.startsWith("textures/") && path.endsWith(".png")) {
            return textureExists(path);
        }

        return false;
    }

    private InputStream findPackResource(ResourceLocation location) throws IOException {
        String fullPath = "assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
        File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
        if (packDirs != null) {
            for (File packDir : packDirs) {
                File resFile = new File(packDir, fullPath);
                if (resFile.exists() && resFile.isFile()) {
                    return new FileInputStream(resFile);
                }
            }
        }
        return PackZipUtils.findZipResource(rootPacksDir, fullPath);
    }

    private boolean packResourceExists(ResourceLocation location) {
        String fullPath = "assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
        File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
        if (packDirs != null) {
            for (File packDir : packDirs) {
                if (new File(packDir, fullPath).exists() && new File(packDir, fullPath).isFile()) {
                    return true;
                }
            }
        }
        return PackZipUtils.zipResourceExists(rootPacksDir, fullPath);
    }

    private InputStream findTexture(String path) throws IOException {
        // path already contains textures/, e.g., textures/items/fire_axe.png
        String fullPath = "assets/modularcontents/" + path;
        File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
        if (packDirs != null) {
            for (File packDir : packDirs) {
                File textureFile = new File(packDir, fullPath);
                if (textureFile.exists()) {
                    return new FileInputStream(textureFile);
                }
            }
        }
        return PackZipUtils.findZipResource(rootPacksDir, fullPath);
    }

    private boolean textureExists(String path) {
        String fullPath = "assets/modularcontents/" + path;
        File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
        if (packDirs != null) {
            for (File packDir : packDirs) {
                if (new File(packDir, fullPath).exists()) {
                    return true;
                }
            }
        }
        return PackZipUtils.zipResourceExists(rootPacksDir, fullPath);
    }

    @Override
    public Set<String> getResourceDomains() {
        return DOMAINS;
    }

    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    @Override
    public String getPackName() {
        return "ModularContents Dynamic Resources";
    }
}

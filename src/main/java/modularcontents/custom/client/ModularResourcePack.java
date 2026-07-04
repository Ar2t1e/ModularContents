package modularcontents.custom.client;

import com.google.common.collect.ImmutableSet;
import modularcontents.custom.item.CustomItemManager;
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

        // 1. Intercept Model generation (So users don't have to write .json models for items)
        if (path.startsWith("models/item/") && path.endsWith(".json")) {
            String itemId = path.substring("models/item/".length(), path.length() - 5);
            // Check if we actually have a texture for this item, or if it's a registered custom item
            if (CustomItemManager.CUSTOM_ITEMS.containsKey(itemId) || itemId.equals("custom_workbench")) {
                if (!itemId.equals("custom_workbench")) {
                    String generatedJson = "{\n  \"parent\": \"item/generated\",\n  \"textures\": {\n    \"layer0\": \"modularcontents:items/" + itemId + "\"\n  }\n}";
                    System.out.println("[ModularContents] Generated model for item: " + itemId);
                    return new ByteArrayInputStream(generatedJson.getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        // 2. Load PNG textures directly from content packs
        if (path.startsWith("textures/items/") && path.endsWith(".png")) {
            String textureName = path.substring("textures/items/".length());
            File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
            if (packDirs != null) {
                for (File packDir : packDirs) {
                    File textureFile = new File(packDir, "textures/items/" + textureName);
                    if (textureFile.exists()) {
                        System.out.println("[ModularContents] Loaded texture: " + textureName + " from " + textureFile.getAbsolutePath());
                        return new FileInputStream(textureFile);
                    }
                }
            }
        }

        // 3. Load block PNG textures directly from content packs
        if (path.startsWith("textures/blocks/") && path.endsWith(".png")) {
            String textureName = path.substring("textures/blocks/".length());
            File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
            if (packDirs != null) {
                for (File packDir : packDirs) {
                    File textureFile = new File(packDir, "textures/blocks/" + textureName);
                    if (textureFile.exists()) {
                        return new FileInputStream(textureFile);
                    }
                }
            }
        }

        throw new IOException("Resource not found: " + location);
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        String path = location.getResourcePath();

        // Model intercept check
        if (path.startsWith("models/item/") && path.endsWith(".json")) {
            String itemId = path.substring("models/item/".length(), path.length() - 5);
            if (CustomItemManager.CUSTOM_ITEMS.containsKey(itemId) || itemId.equals("custom_workbench")) {
                if (!itemId.equals("custom_workbench")) {
                    return true;
                }
            }
        }

        // Texture intercept check
        if (path.startsWith("textures/items/") && path.endsWith(".png")) {
            String textureName = path.substring("textures/items/".length());
            File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
            if (packDirs != null) {
                for (File packDir : packDirs) {
                    File textureFile = new File(packDir, "textures/items/" + textureName);
                    if (textureFile.exists()) {
                        return true;
                    }
                }
            }
        }

        // Add intercept for block textures from content packs if needed
        if (path.startsWith("textures/blocks/") && path.endsWith(".png")) {
            String textureName = path.substring("textures/blocks/".length());
            File[] packDirs = rootPacksDir.listFiles(File::isDirectory);
            if (packDirs != null) {
                for (File packDir : packDirs) {
                    File textureFile = new File(packDir, "textures/blocks/" + textureName);
                    if (textureFile.exists()) {
                        return true;
                    }
                }
            }
        }

        return false;
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

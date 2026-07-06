package modularcontents.custom.network;

import modularcontents.custom.npc.NPCManager;
import modularcontents.custom.recipe.ListWorkbenchRecipeManager;
import modularcontents.custom.loot.EquipmentManager;
import modularcontents.ModularcontentsMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import java.io.File;
import java.io.FileWriter;

public class PacketSaveContentHandler implements IMessageHandler<PacketSaveContent, IMessage> {
    @Override
    public IMessage onMessage(PacketSaveContent message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            // SECURITY CHECK: Only OPs (level 2+) can save files
            if (!player.canUseCommandBlock()) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to modify server content packs!"));
                return;
            }

            // Sanitize
            String safePack = message.packName.replaceAll("[^a-zA-Z0-9_-]", "");
            if (safePack.isEmpty()) safePack = "custom_pack";

            File packDir = new File(player.getServer().getDataDirectory(), "ModularContents/" + safePack);
            File targetFile = new File(packDir, message.filePath);

            // Path traversal protection
            if (!targetFile.getAbsolutePath().startsWith(packDir.getAbsolutePath())) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid file path!"));
                return;
            }

            try {
                targetFile.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(targetFile)) {
                    writer.write(message.jsonContent);
                }

                player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully saved file to server: " + safePack + "/" + message.filePath));
                player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Reloading server content..."));

                // Hot-reload
                ListWorkbenchRecipeManager.loadRecipes(player.getServer().getDataDirectory());
                EquipmentManager.loadEquipment(player.getServer().getDataDirectory());
                NPCManager.loadNPCs(player.getServer().getDataDirectory());

                // Sync to all clients
                PacketSyncContent syncPacket = ModularcontentsMod.buildContentSyncPacket();
                ModularcontentsMod.PACKET_HANDLER.sendToAll(syncPacket);

                player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Server content reloaded!"));

            } catch (Exception e) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "Failed to save file: " + e.getMessage()));
                e.printStackTrace();
            }
        });
        return null;
    }
}
package modularcontents.custom.network;

import modularcontents.ModularcontentsMod;
import modularcontents.custom.loot.AirdropLootManager;
import modularcontents.custom.loot.EquipmentManager;
import modularcontents.custom.npc.NPCManager;
import modularcontents.custom.pack.PackState;
import modularcontents.custom.recipe.ListWorkbenchRecipeManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;

public class PacketTogglePackHandler implements IMessageHandler<PacketTogglePack, IMessage> {
    @Override
    public IMessage onMessage(PacketTogglePack message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            if (!player.canUseCommandBlock()) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to manage content packs!"));
                return;
            }

            String safePack = message.packName.replaceAll("[^a-zA-Z0-9_\\-.]", "");
            if (safePack.isEmpty()) return;

            File dataDir = player.getServer().getDataDirectory();
            PackState.setEnabled(safePack, message.enabled);
            PackState.save(dataDir);

            ListWorkbenchRecipeManager.loadRecipes(dataDir);
            AirdropLootManager.loadLootTables(dataDir);
            EquipmentManager.loadEquipment(dataDir);
            NPCManager.loadNPCs(dataDir);

            ModularcontentsMod.PACKET_HANDLER.sendToAll(ModularcontentsMod.buildContentSyncPacket());
            ModularcontentsMod.PACKET_HANDLER.sendTo(new PacketSendPackList(PacketRequestPackListHandler.buildPackListJson(dataDir)), player);

            player.sendMessage(new TextComponentString((message.enabled ? TextFormatting.GREEN : TextFormatting.YELLOW)
                    + "Pack " + safePack + (message.enabled ? " enabled" : " disabled")));
            player.sendMessage(new TextComponentString(TextFormatting.GRAY
                    + "Recipes, loot and NPCs reloaded. Blocks and items apply after a game restart."));
        });
        return null;
    }
}

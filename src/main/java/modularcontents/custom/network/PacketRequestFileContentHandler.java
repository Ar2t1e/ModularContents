package modularcontents.custom.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import java.io.File;
import java.nio.file.Files;

public class PacketRequestFileContentHandler implements IMessageHandler<PacketRequestFileContent, IMessage> {
    @Override
    public IMessage onMessage(PacketRequestFileContent message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            if (player.canUseCommandBlock()) {
                File packDir = new File(player.getServer().getDataDirectory(), "ModularContents/" + message.packName);
                File targetFile = new File(packDir, message.filePath);

                // Security check to prevent path traversal
                if (!targetFile.getAbsolutePath().startsWith(packDir.getAbsolutePath())) {
                    return;
                }

                if (targetFile.exists() && targetFile.isFile()) {
                    try {
                        String content = new String(Files.readAllBytes(targetFile.toPath()), "UTF-8");
                        modularcontents.ModularcontentsMod.PACKET_HANDLER.sendTo(
                                new PacketSendFileContent(message.packName, message.filePath, content), player);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return null;
    }
}
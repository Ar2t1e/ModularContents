package modularcontents.custom.network;

import modularcontents.custom.gui.GuiContentCreator;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendFileContentHandler implements IMessageHandler<PacketSendFileContent, IMessage> {
    @Override
    public IMessage onMessage(PacketSendFileContent message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiContentCreator) {
                ((GuiContentCreator) Minecraft.getMinecraft().currentScreen).receiveFileContent(message.packName, message.filePath, message.jsonContent);
            }
        });
        return null;
    }
}
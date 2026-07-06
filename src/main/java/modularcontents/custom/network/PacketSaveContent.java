package modularcontents.custom.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSaveContent implements IMessage {
    public String packName = "";
    public String filePath = "";
    public String jsonContent = "";

    public PacketSaveContent() {}

    public PacketSaveContent(String packName, String filePath, String jsonContent) {
        this.packName = packName;
        this.filePath = filePath;
        this.jsonContent = jsonContent;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, packName);
        ByteBufUtils.writeUTF8String(buf, filePath);
        PacketSendPackList.writeCompressed(buf, jsonContent);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        packName = ByteBufUtils.readUTF8String(buf);
        filePath = ByteBufUtils.readUTF8String(buf);
        jsonContent = PacketSendPackList.readCompressed(buf);
    }
}
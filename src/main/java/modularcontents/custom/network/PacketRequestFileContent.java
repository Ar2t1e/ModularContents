package modularcontents.custom.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketRequestFileContent implements IMessage {
    public String packName = "";
    public String filePath = "";

    public PacketRequestFileContent() {}

    public PacketRequestFileContent(String packName, String filePath) {
        this.packName = packName;
        this.filePath = filePath;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, packName);
        ByteBufUtils.writeUTF8String(buf, filePath);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        packName = ByteBufUtils.readUTF8String(buf);
        filePath = ByteBufUtils.readUTF8String(buf);
    }
}
package modularcontents.custom.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketTogglePack implements IMessage {
    public String packName = "";
    public boolean enabled = true;

    public PacketTogglePack() {}

    public PacketTogglePack(String packName, boolean enabled) {
        this.packName = packName;
        this.enabled = enabled;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, packName);
        buf.writeBoolean(enabled);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        packName = ByteBufUtils.readUTF8String(buf);
        enabled = buf.readBoolean();
    }
}

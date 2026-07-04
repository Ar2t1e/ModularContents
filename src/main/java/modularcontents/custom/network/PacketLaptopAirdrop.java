package modularcontents.custom.network;

import io.netty.buffer.ByteBuf;
import modularcontents.custom.entity.EntityAirdrop;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.WorldServer;

public class PacketLaptopAirdrop implements IMessage {
    public int lx, ly, lz;
    public double targetX, targetZ;

    public PacketLaptopAirdrop() {}

    public PacketLaptopAirdrop(BlockPos laptopPos, double targetX, double targetZ) {
        this.lx = laptopPos.getX();
        this.ly = laptopPos.getY();
        this.lz = laptopPos.getZ();
        this.targetX = targetX;
        this.targetZ = targetZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.lx = buf.readInt();
        this.ly = buf.readInt();
        this.lz = buf.readInt();
        this.targetX = buf.readDouble();
        this.targetZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.lx);
        buf.writeInt(this.ly);
        buf.writeInt(this.lz);
        buf.writeDouble(this.targetX);
        buf.writeDouble(this.targetZ);
    }
}
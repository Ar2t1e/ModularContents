package modularcontents.custom.network;

import modularcontents.custom.entity.EntityAirdrop;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLaptopAirdropHandler implements IMessageHandler<PacketLaptopAirdrop, IMessage> {
    @Override
    public IMessage onMessage(PacketLaptopAirdrop message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        WorldServer world = player.getServerWorld();

        world.addScheduledTask(() -> {
            // Distance check to ensure player is actually near the laptop
            if (player.getDistanceSq(message.lx, message.ly, message.lz) > 64.0D) {
                return;
            }

            // Target max 256 blocks away from the laptop
            double dx = message.targetX - message.lx;
            double dz = message.targetZ - message.lz;
            if (dx * dx + dz * dz > 256 * 256) {
                return; // Too far!
            }

            int delayTicks = 200 + world.rand.nextInt(800); // 10-50 seconds
            EntityAirdrop airdrop = new EntityAirdrop(world, message.targetX, 250.0D, message.targetZ);
            airdrop.setDelayAndCaller(delayTicks, player.getName(), false, message.targetX, message.targetZ);
            world.spawnEntity(airdrop);

            int seconds = delayTicks / 20;
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Laptop Airdrop confirmed! Target coordinates: X: " + (int)message.targetX + ", Z: " + (int)message.targetZ + ". ETA: " + seconds + " seconds."));
        });

        return null;
    }
}
package modularcontents.custom.network;

import modularcontents.ModularcontentsMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenCreatorHandler implements IMessageHandler<PacketOpenCreator, IMessage> {
    @Override
    public IMessage onMessage(PacketOpenCreator message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;

        player.getServerWorld().addScheduledTask(() -> {
            if (player.isCreative()) {
                player.openGui(ModularcontentsMod.instance, 4, player.world, (int)player.posX, (int)player.posY, (int)player.posZ);
            }
        });

        return null;
    }
}
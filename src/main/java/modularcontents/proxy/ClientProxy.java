package modularcontents.proxy;

import modularcontents.custom.block.TileEntityAirdrop;
import modularcontents.custom.client.SoundAirdropSmoke;
import modularcontents.custom.client.particle.ParticleAirdropSmoke;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

    @Override
    public void spawnAirdropSmoke(World world, double x, double y, double z, float r, float g, float b) {
        Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleAirdropSmoke(world, x, y, z, 0.0D, 0.05D, 0.0D, r, g, b));
    }

    @Override
    public void playAirdropSmokeSound(TileEntityAirdrop te) {
        Minecraft.getMinecraft().getSoundHandler().playSound(new SoundAirdropSmoke(te));
    }
}

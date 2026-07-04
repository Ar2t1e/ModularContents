package modularcontents.custom.client;

import modularcontents.ModularcontentsMod;
import modularcontents.custom.block.TileEntityAirdrop;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundAirdropSmoke extends MovingSound {
    private final TileEntityAirdrop te;

    public SoundAirdropSmoke(TileEntityAirdrop te) {
        super(ModularcontentsMod.AIRDROP_SMOKE, SoundCategory.BLOCKS);
        this.te = te;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.5F;
        this.xPosF = te.getPos().getX() + 0.5F;
        this.yPosF = te.getPos().getY() + 0.5F;
        this.zPosF = te.getPos().getZ() + 0.5F;
    }

    @Override
    public void update() {
        if (this.te.isInvalid()) {
            this.donePlaying = true;
        }
    }
}

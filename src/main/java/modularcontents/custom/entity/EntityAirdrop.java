package modularcontents.custom.entity;

import modularcontents.ModularcontentsMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAirdrop extends Entity {
    public EntityAirdrop(World worldIn) {
        super(worldIn);
        this.setSize(0.98F, 0.98F);
        this.preventEntitySpawning = true;
    }

    public EntityAirdrop(World worldIn, double x, double y, double z) {
        this(worldIn);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = -0.2D; // Falling speed
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionY -= 0.04D; // Gravity
        // Terminal velocity for parachute effect
        if (this.motionY < -0.3D) {
            this.motionY = -0.3D;
        }

        this.move(net.minecraft.entity.MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        // Smoke particles
        if (this.world.isRemote) {
            this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 1.5D, this.posZ, 0.0D, 0.1D, 0.0D);
            this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX, this.posY + 1.2D, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (!this.world.isRemote) {
            BlockPos pos = new BlockPos(this);
            IBlockState state = this.world.getBlockState(pos.down());
            
            if (this.onGround || !state.getBlock().isReplaceable(this.world, pos.down())) {
                // Landed!
                this.world.setBlockState(pos, ModularcontentsMod.airdrop.getDefaultState());
                this.setDead();
            } else if (this.posY < 0) {
                this.setDead();
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }
}

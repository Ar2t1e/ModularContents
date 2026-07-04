package modularcontents.custom.entity;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;

public class EntitySignalFlare extends EntityThrowable {
    private int fuse = 0;
    private String callerName = "";
    private boolean landed = false;
    private String customLootTable = "";

    public EntitySignalFlare(World worldIn) {
        super(worldIn);
    }

    public EntitySignalFlare(World worldIn, EntityPlayer throwerIn) {
        super(worldIn, throwerIn);
        this.callerName = throwerIn.getName();
        // Default fuse between 200 and 1000 ticks (10 to 50 seconds)
        this.fuse = 200 + worldIn.rand.nextInt(800);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // Check Entity Data for custom overrides immediately after spawning
        if (this.ticksExisted == 1 && !this.world.isRemote) {
            NBTTagCompound data = this.getEntityData();
            if (data.hasKey("Fuse")) {
                this.fuse = data.getInteger("Fuse");
            }
            if (data.hasKey("LootTable")) {
                this.customLootTable = data.getString("LootTable");
            }
        }

        if (this.landed) {
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;
        }

        if (this.world.isRemote) {
            // Emitting red/orange smoke particles
            this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY + 0.5D, this.posZ, 1.0D, 0.0D, 0.0D); // Red
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.1D, 0.0D);
        }

        if (!this.world.isRemote) {
            this.fuse--;
            if (this.fuse <= 0) {
                // Spawn airdrop high above this flare
                EntityAirdrop airdrop = new EntityAirdrop(this.world, this.posX, 250.0D, this.posZ);
                // No delay for airdrop falling since the flare was the delay
                airdrop.setDelayAndCaller(0, this.callerName, true, this.posX, this.posZ);

                if (this.customLootTable != null && !this.customLootTable.isEmpty()) {
                    airdrop.setLootTable(this.customLootTable);
                }

                this.world.spawnEntity(airdrop);

                this.setDead();
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            this.landed = true;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.fuse = compound.getInteger("Fuse");
        this.callerName = compound.getString("Caller");
        this.landed = compound.getBoolean("Landed");
        if (compound.hasKey("LootTable")) {
            this.customLootTable = compound.getString("LootTable");
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Fuse", this.fuse);
        if (this.callerName != null) {
            compound.setString("Caller", this.callerName);
        }
        compound.setBoolean("Landed", this.landed);
        if (this.customLootTable != null && !this.customLootTable.isEmpty()) {
            compound.setString("LootTable", this.customLootTable);
        }
    }
}
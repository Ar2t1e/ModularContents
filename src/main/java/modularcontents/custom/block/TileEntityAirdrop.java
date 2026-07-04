package modularcontents.custom.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
import modularcontents.custom.inventory.ContainerAirdrop;
import modularcontents.custom.loot.AirdropLootManager;
import java.util.List;
import java.util.Random;

public class TileEntityAirdrop extends TileEntityLockableLoot {
    private NonNullList<ItemStack> airdropContents = NonNullList.withSize(27, ItemStack.EMPTY);
    private String customLootTableName = "";
    private boolean isCustomLootGenerated = false;

    public void setLootTableName(String name) {
        this.customLootTableName = name;
    }

    @Override
    public int getSizeInventory() { return 27; }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.airdropContents) {
            if (!itemstack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.airdrop";
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.airdropContents;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.airdropContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.airdropContents);
        }
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
        if (compound.hasKey("CustomLootTable")) {
            this.customLootTableName = compound.getString("CustomLootTable");
        }
        if (compound.hasKey("CustomLootGenerated")) {
            this.isCustomLootGenerated = compound.getBoolean("CustomLootGenerated");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.airdropContents);
        }
        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
        if (this.customLootTableName != null && !this.customLootTableName.isEmpty()) {
            compound.setString("CustomLootTable", this.customLootTableName);
        }
        compound.setBoolean("CustomLootGenerated", this.isCustomLootGenerated);
        return compound;
    }

    @Override
    public void fillWithLoot(EntityPlayer player) {
        // Also call super in case it's using vanilla loot tables
        super.fillWithLoot(player);

        if (!this.world.isRemote && !this.isCustomLootGenerated && this.customLootTableName != null && !this.customLootTableName.isEmpty()) {
            List<ItemStack> generated = AirdropLootManager.generateLoot(this.customLootTableName);
            Random random = this.world.rand;

            // Randomly scatter items into the inventory slots
            for (ItemStack stack : generated) {
                int attempts = 0;
                while (attempts < 50) {
                    int slot = random.nextInt(this.getSizeInventory());
                    if (this.airdropContents.get(slot).isEmpty()) {
                        this.airdropContents.set(slot, stack);
                        break;
                    }
                    attempts++;
                }
            }
            this.isCustomLootGenerated = true;
            this.markDirty();
        }
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        this.fillWithLoot(playerIn);
        return new ContainerAirdrop(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return "modularcontents:airdrop";
    }
}
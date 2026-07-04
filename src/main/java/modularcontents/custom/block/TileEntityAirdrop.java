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

public class TileEntityAirdrop extends TileEntityLockableLoot {
    private NonNullList<ItemStack> airdropContents = NonNullList.withSize(27, ItemStack.EMPTY);

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
        return compound;
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

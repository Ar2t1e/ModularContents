package modularcontents.custom.block;

import modularcontents.custom.item.CustomBlockInfo;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockCustomDoor extends BlockDoor {
    private Item dropItem;

    public BlockCustomDoor(CustomBlockInfo info) {
        super(getMaterialFromName(info.material));
        this.setRegistryName("modularcontents", info.id);
        this.setUnlocalizedName(info.id);
        this.setHardness(info.hardness);
        this.setResistance(info.resistance);
        this.setLightLevel(info.lightLevel);
        if (info.toolClass != null && !info.toolClass.isEmpty()) {
            this.setHarvestLevel(info.toolClass, info.harvestLevel);
        }
        setSoundFromName(info.material);
    }

    public void setDropItem(Item item) {
        this.dropItem = item;
    }

    @Override
    public Item getItemDropped(net.minecraft.block.state.IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : (this.dropItem != null ? this.dropItem : super.getItemDropped(state, rand, fortune));
    }

    private static Material getMaterialFromName(String name) {
        if (name == null) return Material.ROCK;
        switch (name.toLowerCase()) {
            case "wood": return Material.WOOD;
            case "earth": return Material.GROUND;
            case "iron": return Material.IRON;
            case "glass": return Material.GLASS;
            default: return Material.ROCK;
        }
    }

    private void setSoundFromName(String name) {
        if (name == null) {
            this.setSoundType(SoundType.STONE);
            return;
        }
        switch (name.toLowerCase()) {
            case "wood": this.setSoundType(SoundType.WOOD); break;
            case "earth": this.setSoundType(SoundType.GROUND); break;
            case "iron": this.setSoundType(SoundType.METAL); break;
            case "glass": this.setSoundType(SoundType.GLASS); break;
            default: this.setSoundType(SoundType.STONE); break;
        }
    }
}

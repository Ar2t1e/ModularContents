package modularcontents.custom.block;

import modularcontents.custom.item.CustomBlockInfo;
import net.minecraft.block.BlockButton;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCustomButton extends BlockButton {
    private final boolean isWood;

    public BlockCustomButton(CustomBlockInfo info) {
        super(getMaterialFromName(info.material) == Material.WOOD);
        this.isWood = getMaterialFromName(info.material) == Material.WOOD;
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

    @Override
    protected void playClickSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos) {
        if (this.isWood) {
            worldIn.playSound(player, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        } else {
            worldIn.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        }
    }

    @Override
    protected void playReleaseSound(World worldIn, BlockPos pos) {
        if (this.isWood) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
        } else {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
        }
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

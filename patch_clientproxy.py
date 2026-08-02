import re

with open('src/main/java/modularcontents/proxy/ClientProxy.java', 'r', encoding='utf-8') as f:
    content = f.read()

import_str = """
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.util.ResourceLocation;
import modularcontents.custom.item.CustomContentManager;
import modularcontents.custom.item.CustomBlockInfo;
import java.util.ArrayList;
"""
content = content.replace('import java.util.Map;', 'import java.util.Map;' + import_str)

init_method = """
    @Override
    public void init(FMLInitializationEvent event) {
        ArrayList<Block> coloredBlocks = new ArrayList<>();
        
        for (CustomBlockInfo info : CustomContentManager.CUSTOM_BLOCKS.values()) {
            if (info.biomeTint != null && !info.biomeTint.isEmpty()) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ModularcontentsMod.MODID, info.id));
                if (block != null) {
                    coloredBlocks.add(block);
                    
                    Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                        if (worldIn != null && pos != null) {
                            if ("water".equalsIgnoreCase(info.biomeTint)) {
                                return BiomeColorHelper.getWaterColorAtPos(worldIn, pos);
                            } else if ("foliage".equalsIgnoreCase(info.biomeTint)) {
                                return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
                            } else {
                                return BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
                            }
                        }
                        return "water".equalsIgnoreCase(info.biomeTint) ? 0x3F76E4 : ("foliage".equalsIgnoreCase(info.biomeTint) ? ColorizerFoliage.getFoliageColorBasic() : ColorizerGrass.getGrassColor(0.5D, 1.0D));
                    }, block);
                }
            }
        }
        
        if (!coloredBlocks.isEmpty()) {
            Block[] blockArray = coloredBlocks.toArray(new Block[0]);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
                if (stack.getItem() instanceof ItemBlock) {
                    Block block = ((ItemBlock) stack.getItem()).getBlock();
                    @SuppressWarnings("deprecation")
                    net.minecraft.block.state.IBlockState state = block.getStateFromMeta(stack.getMetadata());
                    return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, null, null, tintIndex);
                }
                return 0xFFFFFF;
            }, blockArray);
        }
    }
"""

content = content.replace('public class ClientProxy extends CommonProxy {', 'public class ClientProxy extends CommonProxy {' + init_method)

with open('src/main/java/modularcontents/proxy/ClientProxy.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Patched ClientProxy")

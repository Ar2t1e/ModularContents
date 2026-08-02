package modularcontents.custom.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.IFuelHandler;

public class CustomFuelHandler implements IFuelHandler {
    @Override
    public int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) return 0;
        Item item = fuel.getItem();
        ResourceLocation reg = item.getRegistryName();
        if (reg != null && reg.getResourceDomain().equals("modularcontents")) {
            String path = reg.getResourcePath();
            
            // Item
            if (CustomContentManager.CUSTOM_ITEMS.containsKey(path)) {
                return CustomContentManager.CUSTOM_ITEMS.get(path).burnTime;
            }
            
            // Block 
            if (CustomContentManager.CUSTOM_BLOCKS.containsKey(path)) {
                return CustomContentManager.CUSTOM_BLOCKS.get(path).burnTime;
            }
            
            // Try base block without suffix
            String baseId = path.replace("_top", "").replace("_inner", "").replace("_outer", "").replace("_double", "").replace("_post", "").replace("_side", "").replace("_inventory", "").replace("_slab", "").replace("_stairs", "").replace("_fence", "").replace("_wall", "");
            if (CustomContentManager.CUSTOM_BLOCKS.containsKey(baseId)) {
                return CustomContentManager.CUSTOM_BLOCKS.get(baseId).burnTime;
            }
        }
        return 0;
    }
}

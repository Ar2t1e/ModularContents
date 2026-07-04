package modularcontents.custom.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCustom extends Item {

    public final CustomItemInfo info;

    public ItemCustom(CustomItemInfo info) {
        this.info = info;
        this.setRegistryName(info.id);
        this.setUnlocalizedName("custom." + info.id);
        this.setMaxStackSize(info.maxStackSize);
        if (info.maxDamage > 0) {
            this.setMaxDamage(info.maxDamage);
            this.setNoRepair(); // Optional: prevent combining in vanilla anvil if it's purely a crafting tool
        }

        // Creative Tab Logic
        boolean foundTab = false;
        for (net.minecraft.creativetab.CreativeTabs tab : net.minecraft.creativetab.CreativeTabs.CREATIVE_TAB_ARRAY) {
            if (tab.getTabLabel().equalsIgnoreCase(info.creativeTab)) {
                this.setCreativeTab(tab);
                foundTab = true;
                break;
            }
        }
        if (!foundTab) {
            this.setCreativeTab(modularcontents.ModularcontentsMod.MODULAR_TAB);
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return info.displayName;
    }
}
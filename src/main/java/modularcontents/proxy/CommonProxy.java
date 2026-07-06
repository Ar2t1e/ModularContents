package modularcontents.proxy;

import modularcontents.custom.block.TileEntityAirdrop;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {}

    public void spawnAirdropSmoke(World world, double x, double y, double z, float r, float g, float b) {}

    public void playAirdropSmokeSound(TileEntityAirdrop te) {}

    public void handleContentSync(String recipesJson, String tabsJson, String requiredPacksJson, String equipmentJson) {}
}

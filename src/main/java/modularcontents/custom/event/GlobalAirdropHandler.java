package modularcontents.custom.event;

import modularcontents.custom.config.ModularContentsConfig;
import modularcontents.custom.entity.EntityAirdrop;
import modularcontents.custom.loot.AirdropLootManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class GlobalAirdropHandler {

    private static long lastDropDay = -1;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote || !ModularContentsConfig.globalAirdropsEnabled) {
            return;
        }

        World world = event.world;
        if (world.provider.getDimension() != 0) {
            return; // Only spawn in overworld
        }

        long totalTime = world.getTotalWorldTime();
        long currentDay = totalTime / 24000L;

        if (lastDropDay == -1) {
            lastDropDay = currentDay;
            return;
        }

        if (currentDay > lastDropDay && (currentDay - lastDropDay) >= ModularContentsConfig.globalAirdropIntervalDays) {
            lastDropDay = currentDay;

            List<EntityPlayer> players = world.playerEntities;
            if (players.isEmpty()) return;

            EntityPlayer randomPlayer = players.get(world.rand.nextInt(players.size()));

            double minRad = ModularContentsConfig.globalAirdropMinRadius;
            double maxRad = Math.max(ModularContentsConfig.globalAirdropMaxRadius, minRad + 1);

            double distance = minRad + world.rand.nextDouble() * (maxRad - minRad);
            double angle = world.rand.nextDouble() * Math.PI * 2;

            double targetX = randomPlayer.posX + Math.cos(angle) * distance;
            double targetZ = randomPlayer.posZ + Math.sin(angle) * distance;
            double targetY = 250.0D;

            EntityAirdrop airdrop = new EntityAirdrop(world, targetX, targetY, targetZ);

            String lootTable = "";
            if (ModularContentsConfig.globalAirdropLootTables.length > 0) {
                lootTable = ModularContentsConfig.globalAirdropLootTables[world.rand.nextInt(ModularContentsConfig.globalAirdropLootTables.length)];
                airdrop.setLootTable(lootTable);
            }

            world.spawnEntity(airdrop);

            for (EntityPlayer player : players) {
                player.sendMessage(new TextComponentString(TextFormatting.YELLOW + "[Global Airdrop] " + TextFormatting.GREEN + "A global airdrop is falling near X: " + (int)targetX + ", Z: " + (int)targetZ + "!"));
            }
        }
    }
}
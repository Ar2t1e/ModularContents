package modularcontents.custom.command;

import modularcontents.custom.recipe.ListWorkbenchRecipeManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandModularContents extends CommandBase {

    @Override
    public String getName() {
        return "modularcontents";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/modularcontents <reload|airdrop>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("mc");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Requires OP (cheats enabled)
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Reloading ModularContents recipes..."));

            // Reload recipes from JSON
            ListWorkbenchRecipeManager.loadRecipes(server.getDataDirectory());

            int count = ListWorkbenchRecipeManager.getAllRecipes().size();
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully reloaded " + count + " recipes!"));
        } else if (args.length > 0 && args[0].equalsIgnoreCase("airdrop")) {
            if (args.length >= 3) {
                try {
                    double x = parseCoordinate(sender.getPosition().getX(), args[1], true).getResult();
                    double z = parseCoordinate(sender.getPosition().getZ(), args[2], true).getResult();

                    net.minecraft.world.World world = sender.getEntityWorld();
                    modularcontents.custom.entity.EntityAirdrop airdrop = new modularcontents.custom.entity.EntityAirdrop(world, x, 250.0D, z);
                    world.spawnEntity(airdrop);

                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Airdrop spawned at X: " + (int)x + " Z: " + (int)z));
                } catch (Exception e) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid coordinates"));
                }
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /modularcontents airdrop <x> <z>"));
            }
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: " + getUsage(sender)));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "reload", "airdrop");
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}

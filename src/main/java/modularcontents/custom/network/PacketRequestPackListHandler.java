package modularcontents.custom.network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import modularcontents.custom.pack.PackState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;

public class PacketRequestPackListHandler implements IMessageHandler<PacketRequestPackList, IMessage> {
    @Override
    public IMessage onMessage(PacketRequestPackList message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            if (player.canUseCommandBlock()) { // OP Check
                modularcontents.ModularcontentsMod.PACKET_HANDLER.sendTo(
                        new PacketSendPackList(buildPackListJson(player.getServer().getDataDirectory())), player);
            }
        });
        return null;
    }

    public static String buildPackListJson(File dataDir) {
        File rootDir = new File(dataDir, "ModularContents");
        JsonObject packsObj = new JsonObject();
        JsonArray disabled = new JsonArray();

        for (File pack : PackState.listAllPacks(rootDir)) {
            JsonArray filesArray = new JsonArray();
            scanDirectory(pack, pack, filesArray);
            packsObj.add(pack.getName(), filesArray);
            if (!PackState.isEnabled(pack.getName())) {
                disabled.add(pack.getName());
            }
        }

        JsonObject root = new JsonObject();
        root.add("packs", packsObj);
        root.add("disabled", disabled);
        return new Gson().toJson(root);
    }

    private static void scanDirectory(File root, File current, JsonArray array) {
        File[] files = current.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(root, file, array);
                } else if (file.getName().endsWith(".json")) {
                    String relPath = root.toURI().relativize(file.toURI()).getPath();
                    array.add(relPath);
                }
            }
        }
    }
}

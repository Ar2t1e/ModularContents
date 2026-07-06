package modularcontents.custom.network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
                File rootDir = new File(player.getServer().getDataDirectory(), "ModularContents");
                JsonObject packsObj = new JsonObject();

                if (rootDir.exists() && rootDir.isDirectory()) {
                    File[] packs = rootDir.listFiles(File::isDirectory);
                    if (packs != null) {
                        for (File pack : packs) {
                            if (pack.getName().equals("generated")) continue; // Skip old generated folder
                            JsonArray filesArray = new JsonArray();
                            scanDirectory(pack, pack, filesArray);
                            packsObj.add(pack.getName(), filesArray);
                        }
                    }
                }

                modularcontents.ModularcontentsMod.PACKET_HANDLER.sendTo(new PacketSendPackList(new Gson().toJson(packsObj)), player);
            }
        });
        return null;
    }

    private void scanDirectory(File root, File current, JsonArray array) {
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
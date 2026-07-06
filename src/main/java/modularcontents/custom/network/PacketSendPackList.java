package modularcontents.custom.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PacketSendPackList implements IMessage {
    public String packListJson = "";

    public PacketSendPackList() {}

    public PacketSendPackList(String json) {
        this.packListJson = json;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeCompressed(buf, packListJson);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        packListJson = readCompressed(buf);
    }

    public static void writeCompressed(ByteBuf buf, String value) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(bytes)) {
                gzip.write(value.getBytes(StandardCharsets.UTF_8));
            }
            byte[] compressed = bytes.toByteArray();
            buf.writeInt(compressed.length);
            buf.writeBytes(compressed);
        } catch (Exception e) {
            buf.writeInt(0);
        }
    }

    public static String readCompressed(ByteBuf buf) {
        try {
            int length = buf.readInt();
            if (length <= 0) return "";
            byte[] compressed = new byte[length];
            buf.readBytes(compressed);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream in = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
                byte[] chunk = new byte[8192];
                int read;
                while ((read = in.read(chunk)) != -1) {
                    out.write(chunk, 0, read);
                }
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}
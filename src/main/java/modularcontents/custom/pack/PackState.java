package modularcontents.custom.pack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PackState {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "disabled_packs.json";
    private static final Set<String> RESERVED = new LinkedHashSet<>(Arrays.asList("generated", "lang"));
    private static final Set<String> DISABLED = new LinkedHashSet<>();

    private PackState() {}

    public static boolean isEnabled(String packName) {
        return packName != null && !DISABLED.contains(key(packName));
    }

    public static List<String> disabledPacks() {
        return Collections.unmodifiableList(new ArrayList<>(DISABLED));
    }

    public static File[] listPacks(File rootPacksDir) {
        File[] dirs = rootPacksDir.listFiles(File::isDirectory);
        if (dirs == null) return new File[0];

        List<File> result = new ArrayList<>();
        for (File dir : dirs) {
            if (RESERVED.contains(key(dir.getName()))) continue;
            if (!isEnabled(dir.getName())) continue;
            result.add(dir);
        }
        return result.toArray(new File[0]);
    }

    public static File[] listAllPacks(File rootPacksDir) {
        File[] dirs = rootPacksDir.listFiles(File::isDirectory);
        if (dirs == null) return new File[0];

        List<File> result = new ArrayList<>();
        for (File dir : dirs) {
            if (RESERVED.contains(key(dir.getName()))) continue;
            result.add(dir);
        }
        return result.toArray(new File[0]);
    }

    public static void load(File gameDir) {
        DISABLED.clear();
        File file = stateFile(gameDir);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            String[] names = GSON.fromJson(reader, String[].class);
            if (names != null) {
                for (String name : names) {
                    if (name != null && !name.isEmpty()) DISABLED.add(key(name));
                }
            }
        } catch (Exception e) {
            System.err.println("[ModularContents] Failed to load disabled pack list: " + e.getMessage());
        }
    }

    public static boolean save(File gameDir) {
        File file = stateFile(gameDir);
        try {
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(GSON.toJson(DISABLED.toArray(new String[0])));
            }
            return true;
        } catch (Exception e) {
            System.err.println("[ModularContents] Failed to save disabled pack list: " + e.getMessage());
            return false;
        }
    }

    public static void setEnabled(String packName, boolean enabled) {
        if (packName == null || packName.isEmpty()) return;
        if (enabled) {
            DISABLED.remove(key(packName));
        } else {
            DISABLED.add(key(packName));
        }
    }

    private static String key(String name) {
        String value = name.toLowerCase(Locale.ROOT);
        return value.endsWith(".zip") ? value.substring(0, value.length() - 4) : value;
    }

    private static File stateFile(File gameDir) {
        return new File(new File(gameDir, "ModularContents"), FILE_NAME);
    }
}

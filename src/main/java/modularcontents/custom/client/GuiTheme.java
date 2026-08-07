package modularcontents.custom.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class GuiTheme {

    public static int ACCENT = 0xFFFFAA00;
    public static int BORDER = 0xFF4A4A4A;
    public static int BORDER_DARK = 0xFF2A2A2A;
    public static int PANEL = 0xFF151515;
    public static int PANEL_ALT = 0xFF18181A;
    public static int SLOT_BG = 0xFF111111;
    public static int TEXT = 0xFFDDDDDD;
    public static int TEXT_DIM = 0xFF888888;
    public static int LINE = 0xFF333333;
    public static int GREEN = 0xFF55DD55;
    public static int RED = 0xFFFF5555;
    public static int SELECTED = 0xFF2A2A11;
    public static int OUTPUT_BG = 0xFF221111;
    public static int CRAFTABLE = 0xFF55DD55;
    public static int CRAFTABLE_DIM = 0xFF2A4A2A;
    public static int SCROLL_TRACK = 0xFF1A1A1A;
    public static int SCROLL_THUMB = 0xFF555555;
    public static int EDITOR_BG = 0xFF0E0E0E;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "theme.json";

    private GuiTheme() {}

    public static Map<String, Integer> snapshot() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("accent", ACCENT);
        map.put("border", BORDER);
        map.put("border_dark", BORDER_DARK);
        map.put("panel", PANEL);
        map.put("panel_alt", PANEL_ALT);
        map.put("slot_bg", SLOT_BG);
        map.put("text", TEXT);
        map.put("text_dim", TEXT_DIM);
        map.put("line", LINE);
        map.put("green", GREEN);
        map.put("red", RED);
        map.put("selected", SELECTED);
        map.put("output_bg", OUTPUT_BG);
        map.put("craftable", CRAFTABLE);
        map.put("craftable_dim", CRAFTABLE_DIM);
        map.put("scroll_track", SCROLL_TRACK);
        map.put("scroll_thumb", SCROLL_THUMB);
        map.put("editor_bg", EDITOR_BG);
        return map;
    }

    public static void apply(String key, int color) {
        switch (key) {
            case "accent": ACCENT = color; break;
            case "border": BORDER = color; break;
            case "border_dark": BORDER_DARK = color; break;
            case "panel": PANEL = color; break;
            case "panel_alt": PANEL_ALT = color; break;
            case "slot_bg": SLOT_BG = color; break;
            case "text": TEXT = color; break;
            case "text_dim": TEXT_DIM = color; break;
            case "line": LINE = color; break;
            case "green": GREEN = color; break;
            case "red": RED = color; break;
            case "selected": SELECTED = color; break;
            case "output_bg": OUTPUT_BG = color; break;
            case "craftable": CRAFTABLE = color; break;
            case "craftable_dim": CRAFTABLE_DIM = color; break;
            case "scroll_track": SCROLL_TRACK = color; break;
            case "scroll_thumb": SCROLL_THUMB = color; break;
            case "editor_bg": EDITOR_BG = color; break;
            default: break;
        }
    }

    public static String toJson() {
        JsonObject root = new JsonObject();
        for (Map.Entry<String, Integer> entry : snapshot().entrySet()) {
            root.addProperty(entry.getKey(), String.format("%08X", entry.getValue()));
        }
        return GSON.toJson(root);
    }

    public static void applyJson(JsonObject root) {
        for (String key : snapshot().keySet()) {
            if (!root.has(key)) continue;
            Integer color = parseColor(root.get(key).getAsString());
            if (color != null) apply(key, color);
        }
    }

    public static Integer parseColor(String text) {
        if (text == null) return null;
        String value = text.trim();
        if (value.startsWith("#")) value = value.substring(1);
        if (value.startsWith("0x") || value.startsWith("0X")) value = value.substring(2);
        if (value.length() == 6) value = "FF" + value;
        if (value.length() != 8) return null;
        try {
            return (int) Long.parseLong(value, 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void load(File gameDir) {
        File file = themeFile(gameDir);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root != null) applyJson(root);
        } catch (Exception e) {
            System.err.println("[ModularContents] Failed to load GUI theme: " + e.getMessage());
        }
    }

    public static boolean save(File gameDir) {
        File file = themeFile(gameDir);
        try {
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(toJson());
            }
            return true;
        } catch (Exception e) {
            System.err.println("[ModularContents] Failed to save GUI theme: " + e.getMessage());
            return false;
        }
    }

    private static File themeFile(File gameDir) {
        return new File(new File(gameDir, "ModularContents"), FILE_NAME);
    }
}

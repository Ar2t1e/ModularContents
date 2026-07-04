package modularcontents.custom.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class ModularContentsConfig {

    public static boolean generateExamplePack = true;
    public static boolean globalAirdropsEnabled = false;
    public static int globalAirdropIntervalDays = 3;
    public static int globalAirdropMinRadius = 100;
    public static int globalAirdropMaxRadius = 500;
    public static String[] globalAirdropLootTables = new String[0]; // Empty means all allowed

    public static void load(File gameDir) {
        File configDir = new File(gameDir, "ModularContents");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        File configFile = new File(configDir, "modularcontents_settings.cfg");
        Configuration config = new Configuration(configFile);

        config.load();
        generateExamplePack = config.getBoolean("Generate Example Pack", Configuration.CATEGORY_GENERAL, true, "Should the mod generate an example content pack with recipes on first launch?");

        String airdropCat = "Global Airdrops";
        config.addCustomCategoryComment(airdropCat, "Settings for automatic global airdrops in the world");
        globalAirdropsEnabled = config.getBoolean("Enabled", airdropCat, false, "Enable automatic global airdrops?");
        globalAirdropIntervalDays = config.getInt("Interval (Days)", airdropCat, 3, 1, 100, "How many in-game days between global airdrops?");
        globalAirdropMinRadius = config.getInt("Min Radius", airdropCat, 100, 10, 10000, "Minimum radius from a random player to spawn the drop");
        globalAirdropMaxRadius = config.getInt("Max Radius", airdropCat, 500, 50, 10000, "Maximum radius from a random player to spawn the drop");
        globalAirdropLootTables = config.getStringList("Allowed Loot Tables", airdropCat, new String[0], "List of allowed loot tables (e.g. 'military_loot'). Leave empty to allow any.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
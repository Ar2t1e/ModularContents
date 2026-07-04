import os
import json

out_dir = r"C:\Users\Ethan\AppData\Roaming\PrismLauncher\instances\CleanRoom\minecraft\ModularContents\vanilla_pack\recipes"
os.makedirs(out_dir, exist_ok=True)

# List of common vanilla items grouped by category
vanilla_items = {
    "Blocks": [
        "stone", "grass", "dirt", "cobblestone", "planks", "sapling", "bedrock", "sand", "gravel",
        "gold_ore", "iron_ore", "coal_ore", "log", "leaves", "sponge", "glass", "lapis_ore", "lapis_block",
        "dispenser", "sandstone", "noteblock", "golden_rail", "web", "tallgrass", "deadbush", "wool",
        "yellow_flower", "red_flower", "brown_mushroom", "red_mushroom", "gold_block", "iron_block",
        "double_stone_slab", "stone_slab", "brick_block", "tnt", "bookshelf", "mossy_cobblestone", "obsidian",
        "torch", "fire", "mob_spawner", "oak_stairs", "chest", "redstone_wire", "diamond_ore", "diamond_block",
        "crafting_table", "wheat", "farmland", "furnace", "lit_furnace", "standing_sign", "wooden_door",
        "ladder", "rail", "stone_stairs", "wall_sign", "lever", "stone_pressure_plate", "iron_door",
        "wooden_pressure_plate", "redstone_ore", "lit_redstone_ore", "unlit_redstone_torch", "redstone_torch",
        "stone_button", "snow_layer", "ice", "snow", "cactus", "clay", "reeds", "jukebox", "fence", "pumpkin",
        "netherrack", "soul_sand", "glowstone", "portal", "lit_pumpkin", "cake", "unpowered_repeater",
        "powered_repeater", "stained_glass", "trapdoor", "monster_egg", "stonebrick", "brown_mushroom_block",
        "red_mushroom_block", "iron_bars", "glass_pane", "melon_block", "pumpkin_stem", "melon_stem", "vine",
        "fence_gate", "brick_stairs", "stone_brick_stairs", "mycelium", "waterlily", "nether_brick",
        "nether_brick_fence", "nether_brick_stairs", "nether_wart", "enchanting_table", "brewing_stand",
        "cauldron", "end_portal", "end_portal_frame", "end_stone", "dragon_egg", "redstone_lamp",
        "lit_redstone_lamp", "double_wooden_slab", "wooden_slab", "cocoa", "sandstone_stairs", "emerald_ore",
        "ender_chest", "tripwire_hook", "tripwire", "emerald_block", "spruce_stairs", "birch_stairs",
        "jungle_stairs", "command_block", "beacon", "cobblestone_wall", "flower_pot", "carrots", "potatoes",
        "wooden_button", "skull", "anvil", "trapped_chest", "light_weighted_pressure_plate",
        "heavy_weighted_pressure_plate", "unpowered_comparator", "powered_comparator", "daylight_detector",
        "daylight_detector_inverted", "redstone_block", "quartz_ore", "hopper", "quartz_block", "quartz_stairs",
        "activator_rail", "dropper", "stained_hardened_clay", "stained_glass_pane", "leaves2", "log2",
        "acacia_stairs", "dark_oak_stairs", "slime", "barrier", "iron_trapdoor", "prismarine", "sea_lantern",
        "hay_block", "carpet", "hardened_clay", "coal_block", "packed_ice", "double_plant", "standing_banner",
        "wall_banner", "daylight_detector_inverted", "red_sandstone", "red_sandstone_stairs", "double_stone_slab2",
        "stone_slab2", "spruce_fence_gate", "birch_fence_gate", "jungle_fence_gate", "dark_oak_fence_gate",
        "acacia_fence_gate", "spruce_fence", "birch_fence", "jungle_fence", "dark_oak_fence", "acacia_fence",
        "spruce_door", "birch_door", "jungle_door", "acacia_door", "dark_oak_door", "end_rod", "chorus_plant",
        "chorus_flower", "purpur_block", "purpur_pillar", "purpur_stairs", "purpur_double_slab", "purpur_slab",
        "end_bricks", "beetroots", "grass_path", "end_gateway", "repeating_command_block", "chain_command_block",
        "frosted_ice", "magma", "nether_wart_block", "red_nether_brick", "bone_block", "structure_void", "observer",
        "white_shulker_box", "orange_shulker_box", "magenta_shulker_box", "light_blue_shulker_box",
        "yellow_shulker_box", "lime_shulker_box", "pink_shulker_box", "gray_shulker_box", "silver_shulker_box",
        "cyan_shulker_box", "purple_shulker_box", "blue_shulker_box", "brown_shulker_box", "green_shulker_box",
        "red_shulker_box", "black_shulker_box", "white_glazed_terracotta", "orange_glazed_terracotta",
        "magenta_glazed_terracotta", "light_blue_glazed_terracotta", "yellow_glazed_terracotta",
        "lime_glazed_terracotta", "pink_glazed_terracotta", "gray_glazed_terracotta", "silver_glazed_terracotta",
        "cyan_glazed_terracotta", "purple_glazed_terracotta", "blue_glazed_terracotta", "brown_glazed_terracotta",
        "green_glazed_terracotta", "red_glazed_terracotta", "black_glazed_terracotta", "concrete",
        "concrete_powder", "structure_block"
    ],
    "Tools": [
        "iron_shovel", "iron_pickaxe", "iron_axe", "flint_and_steel", "apple", "bow", "arrow", "coal",
        "diamond", "iron_ingot", "gold_ingot", "iron_sword", "wooden_sword", "wooden_shovel", "wooden_pickaxe",
        "wooden_axe", "stone_sword", "stone_shovel", "stone_pickaxe", "stone_axe", "diamond_sword",
        "diamond_shovel", "diamond_pickaxe", "diamond_axe", "stick", "bowl", "mushroom_stew", "golden_sword",
        "golden_shovel", "golden_pickaxe", "golden_axe", "string", "feather", "gunpowder", "wooden_hoe",
        "stone_hoe", "iron_hoe", "diamond_hoe", "golden_hoe", "wheat_seeds", "wheat", "bread", "leather_helmet",
        "leather_chestplate", "leather_leggings", "leather_boots", "chainmail_helmet", "chainmail_chestplate",
        "chainmail_leggings", "chainmail_boots", "iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots",
        "diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots", "golden_helmet",
        "golden_chestplate", "golden_leggings", "golden_boots", "flint", "porkchop", "cooked_porkchop", "painting",
        "golden_apple", "sign", "wooden_door", "bucket", "water_bucket", "lava_bucket", "minecart", "saddle",
        "iron_door", "redstone", "snowball", "boat", "leather", "milk_bucket", "brick", "clay_ball", "reeds",
        "paper", "book", "slime_ball", "chest_minecart", "furnace_minecart", "egg", "compass", "fishing_rod",
        "clock", "glowstone_dust", "fish", "cooked_fish", "dye", "bone", "sugar", "cake", "bed", "repeater",
        "cookie", "filled_map", "shears", "melon", "pumpkin_seeds", "melon_seeds", "beef", "cooked_beef",
        "chicken", "cooked_chicken", "rotten_flesh", "ender_pearl", "blaze_rod", "ghast_tear", "gold_nugget",
        "nether_wart", "potion", "glass_bottle", "spider_eye", "fermented_spider_eye", "blaze_powder",
        "magma_cream", "brewing_stand", "cauldron", "ender_eye", "speckled_melon", "spawn_egg", "experience_bottle",
        "fire_charge", "writable_book", "written_book", "emerald", "item_frame", "flower_pot", "carrot",
        "potato", "baked_potato", "poisonous_potato", "map", "golden_carrot", "skull", "carrot_on_a_stick",
        "nether_star", "pumpkin_pie", "fireworks", "firework_charge", "enchanted_book", "comparator",
        "netherbrick", "quartz", "tnt_minecart", "hopper_minecart", "prismarine_shard", "prismarine_crystals",
        "rabbit", "cooked_rabbit", "rabbit_stew", "rabbit_foot", "rabbit_hide", "armor_stand", "iron_horse_armor",
        "golden_horse_armor", "diamond_horse_armor", "lead", "name_tag", "command_block_minecart", "mutton",
        "cooked_mutton", "banner", "end_crystal", "spruce_door", "birch_door", "jungle_door", "acacia_door",
        "dark_oak_door", "chorus_fruit", "chorus_fruit_popped", "beetroot", "beetroot_seeds", "beetroot_soup",
        "dragon_breath", "splash_potion", "spectral_potion", "tipped_arrow", "lingering_potion", "shield",
        "elytra", "spruce_boat", "birch_boat", "jungle_boat", "acacia_boat", "dark_oak_boat", "totem_of_undying",
        "shulker_shell", "iron_nugget", "knowledge_book"
    ]
}

count = 0
for category, items in vanilla_items.items():
    for item in items:
        recipe_id = f"vanilla_{item}"

        recipe = {
            "id": recipe_id,
            "category": category,
            "inputs": [
                {"item": "minecraft:dirt", "count": 1, "meta": 0}
            ],
            "outputs": [
                {"item": f"minecraft:{item}", "count": 1, "meta": 0}
            ],
            "craftingTime": 20 # 1 second for vanilla items
        }

        out_file = os.path.join(out_dir, f"{recipe_id}.json")
        try:
            with open(out_file, "w", encoding="utf-8") as out_f:
                json.dump(recipe, out_f, indent=4)
            count += 1
        except Exception as e:
            print(f"Error saving {recipe_id}: {e}")

print(f"Generated {count} vanilla recipes!")

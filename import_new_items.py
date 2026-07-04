import os
import json
import shutil

ref_dir = r"C:\Users\Ethan\MCreatorWorkspaces\modularcontents\reference"
out_dir = r"C:\Users\Ethan\AppData\Roaming\PrismLauncher\instances\CleanRoom\minecraft\ModularContents\example_pack"

items_dir = os.path.join(out_dir, "items")
textures_dir = os.path.join(out_dir, "textures", "items")

os.makedirs(items_dir, exist_ok=True)
os.makedirs(textures_dir, exist_ok=True)

# Process Components
components_dir = os.path.join(ref_dir, "components")
if os.path.exists(components_dir):
    for f in os.listdir(components_dir):
        if f.endswith(".png"):
            item_id = f.replace(".png", "")
            display_name = item_id.replace("_", " ").title()

            # Copy texture
            src_path = os.path.join(components_dir, f)
            dst_path = os.path.join(textures_dir, f)
            shutil.copy2(src_path, dst_path)

            # Create JSON
            json_path = os.path.join(items_dir, f"{item_id}.json")
            item_data = {
                "id": item_id,
                "display_name": display_name,
                "max_stack_size": 64,
                "creative_tab": "misc"
            }
            with open(json_path, "w", encoding="utf-8") as jf:
                json.dump(item_data, jf, indent=4)
            print(f"Added component {item_id}")

# Process Tools
tools_dir = os.path.join(ref_dir, "tools")
if os.path.exists(tools_dir):
    for f in os.listdir(tools_dir):
        if f.endswith(".png"):
            item_id = f.replace(".png", "")
            display_name = item_id.replace("_", " ").title()

            # Copy texture
            src_path = os.path.join(tools_dir, f)
            dst_path = os.path.join(textures_dir, f)
            shutil.copy2(src_path, dst_path)

            # Create JSON with max_damage
            json_path = os.path.join(items_dir, f"{item_id}.json")
            item_data = {
                "id": item_id,
                "display_name": display_name,
                "max_stack_size": 1,
                "max_damage": 100, # Default durability for tools
                "creative_tab": "tools"
            }
            with open(json_path, "w", encoding="utf-8") as jf:
                json.dump(item_data, jf, indent=4)
            print(f"Added tool {item_id}")

print("Done processing new items!")
